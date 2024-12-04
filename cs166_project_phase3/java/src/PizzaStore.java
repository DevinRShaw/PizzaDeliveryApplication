/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

//my imports 
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class PizzaStore {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of PizzaStore
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public PizzaStore(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end PizzaStore

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            PizzaStore.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      PizzaStore esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the PizzaStore object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new PizzaStore (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Menu");
                System.out.println("4. Place Order"); //make sure user specifies which store
                System.out.println("5. View Full Order ID History");
                System.out.println("6. View Past 5 Order IDs");
                System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                System.out.println("8. View Stores"); 

                //**the following functionalities should only be able to be used by drivers & managers**
                System.out.println("9. Update Order Status");

                //**the following functionalities should ony be able to be used by managers**
                System.out.println("10. Update Menu");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql); break;
                   case 2: updateProfile(esql); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql); break;
                   case 10: updateMenu(esql, authorisedUser); break;
                   case 11: updateUser(esql); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   

   public static void CreateUser(PizzaStore esql){

      String userName;
      String password;
      String phonenumber;



      //username validation loop 
      do {
         //username input
         Scanner myObj = new Scanner(System.in);
         System.out.print("Create Username: ");
         userName = myObj.nextLine();

         //length of username restrictions 
         if(userName.length() < 5 ){
            System.out.println("invalid username: must be at least 5 characters");
            continue; //prompts for username again 
         }
         if(userName.length() > 50){
            System.out.println("invalid username: must less than 51 characters");
            continue; //prompts for username again 
         }


         //username existance, if exists print warning and continue, if not break 
            //test this with mfarrears0, try to make an account with said username
            //break; use to leave username creation loop

         String existsQuery = "SELECT * FROM users U WHERE U.login = '" + userName + "'";

         try {
            // Use the executeQuery method from the PizzaStore instance (esql)
            int existingCount = esql.executeQuery(existsQuery); // This will return the number of rows returned

            if (existingCount > 0) { // if more than 0 there is a user with that name 
                System.out.println("Invalid username: username is already taken.");
                
                continue; // prompts for username again
            }
            // If username doesn't exist, break out of the loop
            System.out.println("Username available!");
            break; // Exit the loop when username is valid
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            continue; // Continue the loop if there is an exception
        }
      }while (true);


      //password validation loop 
      do {
         //password input
         Scanner myObj = new Scanner(System.in);
         System.out.print("Create Password: ");
         password = myObj.nextLine();

         //length of username restrictions 
         if(password.length() < 1 ){
            System.out.println("invalid password: must be at least 1 character");
            continue; //prompts for password again 
         }

         if(password.length() > 30){
            System.out.println("invalid password: must be less than 31 characters");
            continue; //prompts for password again 
         }

         break;
      }while (true);


      //phonenumber validation loop 
      do {
         //phonenumber input
         Scanner myObj = new Scanner(System.in);
         System.out.print("Add phone number in XXX-XXX-XXXX format: ");
         phonenumber = myObj.nextLine();

         //length of username restrictions 
         if(phonenumber.length() != 12 ){
            System.out.println("invalid phone number: must be in XXX-XXX-XXXX format");
            continue; //prompts for password again 
         }

         String regex = "\\d{3}-\\d{3}-\\d{4}";
         Pattern pattern = Pattern.compile(regex);
         Matcher matcher = pattern.matcher(phonenumber);

         if(!matcher.matches()){
            System.out.println("invalid phone number: must be in XXX-XXX-XXXX format");
            continue;
         }

         break;

      }while (true);
      //at this point we have the username, password and phone number handled, userName password phonenumber

      String userInsertQuery = "INSERT INTO users (login, password, role, phoneNum) VALUES ('" + userName + "','" + password + "', 'customer', '" + phonenumber + "')";

      try {
            // Use the executeQuery method from the PizzaStore instance (esql)
            esql.executeUpdate(userInsertQuery); // This will return the number of rows returned
            System.out.println("User: " + userName + " created");

         
        } catch (SQLException e) {
            System.err.println("Error creating new user: " + e.getMessage());
        }

   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(PizzaStore esql){
      
      String username;
      String password;

      // username input
      Scanner myObj = new Scanner(System.in);
      System.out.print("Enter Username: ");
      username = myObj.nextLine();

      // password input
      System.out.print("Enter password: ");
      password = myObj.nextLine();

      // username validation
      String userExistsQuery = "SELECT * FROM users U WHERE U.login = '" + username + "'AND U.password = '" + password + "'";

      try {
         // use the executeQuery method from the PizzaStore instance (esql)
         int existingUserCount = esql.executeQuery(userExistsQuery); // This will return the number of rows returned
       
         // if username doesn't exist, return null
         if (existingUserCount <= 0) {
            System.out.println("Invalid credentials");
            return null;
         } else {
            return username;
         }
      } catch (SQLException e) {
         System.out.println("Error checking credentials: " + e.getMessage());
         return null;
      }

      

   }//end

// Rest of the functions definition go in here

   public static void viewProfile(PizzaStore esql) {}


   
   public static void updateProfile(PizzaStore esql) {}



   public static void viewMenu(PizzaStore esql) {}


   public static void placeOrder(PizzaStore esql) {}
   public static void viewAllOrders(PizzaStore esql) {}
   public static void viewRecentOrders(PizzaStore esql) {}
   public static void viewOrderInfo(PizzaStore esql) {}
   public static void viewStores(PizzaStore esql) {}
   public static void updateOrderStatus(PizzaStore esql) {}

   public static void updateMenu(PizzaStore esql, String authorisedUser) {

      // check if manager role
      String query = "SELECT * FROM Users U WHERE U.login='" + authorisedUser + "' AND role='manager'";
      int rowCount;
      try {
         rowCount = esql.executeQuery(query);
         if (rowCount == 0) {
            System.out.println("Access Denied");
            return;
         }   
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }

      String itemName;
      String ingredients;
      String typeOfItem;
      float price;
      String description;

      Scanner myObj = new Scanner(System.in);

      // --- maybe call browse menu, so user can see the list of items here ---

      // prompt if the user would like to add a new item or update an existing item
      System.out.println("Would you like to ADD, UPDATE, or DELETE an item?");
      System.out.println("------------------");
      System.out.println("1. Add");
      System.out.println("2. Update");
      System.out.println("3. Delete");
      System.out.println(".........................");
      System.out.println("9. Go back");

      switch (readChoice()) {
         // add an item
         case 1:
            do {
               // prompt user for the name of the item they would like to add
               System.out.print("What is the name of the item you would like to add? ");
               itemName = myObj.nextLine();

               //length of item name restrictions 
               if(itemName.length() < 1 ){
                  System.out.println("invalid item name: must be at least 1 character");
                  continue; //prompts for item name again 
               }
               if(itemName.length() > 50){
                  System.out.println("invalid item name: must less than 51 characters");
                  continue; //prompts for item name again 
               }
               break;
            } while (true);

            //check if the item already exists
            query = "SELECT * FROM Items WHERE itemName='" + itemName + "'";
            try {
               rowCount = esql.executeQuery(query);
               if (rowCount != 0) {
                  System.out.println("Item already exists.");
                  return;
               }
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }

            // prompt for ingredients
            do {
               System.out.println("Please enter the list of ingredients for this item (separated by a comma):");
               ingredients = myObj.nextLine();
               //length of ingredients restrictions 
               if (ingredients.length() < 1) {
                  System.out.println("invalid ingredients: must be at least 1 character");
                  continue; //prompts for ingredients again 
               }
               if(ingredients.length() > 300){
                  System.out.println("invalid ingredients: must less than 301 characters");
                  continue; //prompts for ingredients again 
               }
               break;
            } while (true);

            // prompt for type of item
            System.out.println("Please enter the type of item.");
            System.out.println("------------------------------");
            System.out.println("1. Drink");
            System.out.println("2. Entree");
            System.out.println("3. Side");
            switch (readChoice()) {
               case 1: typeOfItem = "drinks"; break;
               case 2: typeOfItem = "entree"; break;
               case 3: typeOfItem = "sides"; break;

               default: System.out.println("Unrecognized choice!"); return;
            }
            
            // prompt for price
            do {
               System.out.println("Please enter the price of this item: ");
               // check if the input is a valid float
               if (myObj.hasNextFloat()) {
                  price = myObj.nextFloat();
                  break;
               } else {
                  System.out.println("Invalid price value.");
                  myObj.nextLine(); // consume invalid input
               }
            } while (true);

            // prompt for description
            System.out.println("Would you like to enter a description?");
            System.out.println("--------------------------------");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.println("------------");
            switch(readChoice()) {
               case 1: 
                  System.out.println("Please enter a description for this item: ");
                  myObj.nextLine(); // consume left over newline from readChoice()
                  description = myObj.nextLine();
                  break;
               case 2:
                  description = "";
                  break;

               default: System.out.println("Unrecoginzed choice!"); return;
            }

            // add item info to db
            query = "INSERT INTO Items (itemName, ingredients, typeOfItem, price, description) "
                                    + "VALUES ('" + itemName + "', '" + ingredients + "', '" + typeOfItem + "', " 
                                    + price + ", '" + description + "')";
            try {
               esql.executeUpdate(query);
               System.out.println("Item added!");
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }
            break;
         // update an item
         case 2:
            System.out.print("Enter the name of the item you would like to update: ");
            itemName = myObj.nextLine();

            // check if the item exists
            query = "SELECT * FROM Items WHERE itemName='" + itemName + "'";
            try {
               rowCount = esql.executeQuery(query);
               if (rowCount == 0) {
                  System.out.println("Item doesn't exist."); 
                  return;
               }
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }

            // prompt which category of the item to update
            System.out.println("Which category of the item would you like to update?");
            System.out.println("-----------------------------");
            System.out.println("1. Ingredients");
            System.out.println("2. Type of item");
            System.out.println("3. Price");
            System.out.println("4. Item description");
            System.out.println("---------------------");
            System.out.println("9. Go back");
            switch (readChoice()) {
               case 1:
                  do {
                     System.out.println("Please enter your new list of ingredients: ");
                     ingredients = myObj.nextLine();
                     //length of ingredients restrictions 
                     if (ingredients.length() < 1) {
                        System.out.println("invalid ingredients: must be at least 1 character");
                        continue; //prompts for ingredients again 
                     }
                     if(ingredients.length() > 300){
                        System.out.println("invalid ingredients: must less than 301 characters");
                        continue; //prompts for ingredients again 
                     }
                     break;
                  } while (true);

                  // update item in db
                  query = "UPDATE Items SET ingredients = '" + ingredients + "' WHERE itemName = '" + itemName + "'"; 
                  try {
                     esql.executeUpdate(query);
                     System.out.println("Item updated!");
                  } catch (SQLException e) {
                     System.err.println(e.getMessage());
                  }
                  break;
               case 2:
                  System.out.println("Please enter the new item type: ");
                  System.out.println("-----------------------------");
                  System.out.println("1. Drink");
                  System.out.println("2. Entree");
                  System.out.println("3. Side");
                  
                  switch (readChoice()) {
                     case 1: typeOfItem = "drinks"; break;
                     case 2: typeOfItem = "entree"; break;
                     case 3: typeOfItem = "sides"; break;

                     default: System.out.println("Unrecognized choice!"); return;
                  }

                  // update item in db
                  query = "UPDATE Items SET typeOfItem = '" + typeOfItem + "' WHERE itemName = '" + itemName + "'"; 
                  try {
                     esql.executeUpdate(query);
                     System.out.println("Item updated!");
                  } catch (SQLException e) {
                     System.err.println(e.getMessage());
                  }
                  break;
               case 3:
                  do {
                     System.out.println("Please enter the new price of this item: ");
                     // check if the input is a valid float
                     if (myObj.hasNextFloat()) {
                        price = myObj.nextFloat();
                        break;
                     } else {
                        System.out.println("Invalid price value.");
                        myObj.nextLine(); // consume invalid input
                     }
                  } while (true);

                  // update item in db
                  query = "UPDATE Items SET price = '" + price + "' WHERE itemName = '" + itemName + "'"; 
                  try {
                     esql.executeUpdate(query);
                     System.out.println("Item updated!");
                  } catch (SQLException e) {
                     System.err.println(e.getMessage());
                  }
                  break;
               case 4:
                  System.out.println("Please enter the new description for this item: ");
                  description = myObj.nextLine();

                  // update item in db
                  query = "UPDATE Items SET description = '" + description + "' WHERE itemName = '" + itemName + "'"; 
                  try {
                     esql.executeUpdate(query);
                     System.out.println("Item updated!");
                  } catch (SQLException e) {
                     System.err.println(e.getMessage());
                  }
                  break;                              
            
               case 9: break;
               default: System.out.println("Unrecognized choice!"); break;
            }
            break;
         // delete an item
         case 3:
             // --- maybe call browse menu, so user can see the list of items here ---
            System.out.print("Enter the name of the item you would like to delete: ");
            itemName = myObj.nextLine();

            // check if the item exists
            query = "SELECT * FROM Items WHERE itemName='" + itemName + "'";
            try {
               rowCount = esql.executeQuery(query);
               if (rowCount == 0) {
                  System.out.println("Item doesn't exist."); 
                  return;
               }
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }

            // delete item from db
            query = "DELETE FROM Items WHERE itemName = '" + itemName + "'";
            try { 
               esql.executeUpdate(query);
               System.out.println("Item deleted.");
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }
            break;


         case 9: break;
         default: System.out.println("Unrecognized choice!"); break;
      }
   }

   public static void updateUser(PizzaStore esql) {}


}//end PizzaStore

