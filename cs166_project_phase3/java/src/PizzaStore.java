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

import java.util.Arrays;
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
                   case 1: viewProfile(esql, authorisedUser); break;
                   case 2: updateProfile(esql, authorisedUser); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break;
                   case 5: viewAllOrders(esql, authorisedUser); break;
                   case 6: viewRecentOrders(esql, authorisedUser); break;
                   case 7: viewOrderInfo(esql, authorisedUser); break;                   
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql, authorisedUser); break;
                   case 10: updateMenu(esql, authorisedUser); break;
                   case 11: authorisedUser = updateUser(esql, authorisedUser); break;

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
         System.err.println("Error checking credentials: " + e.getMessage());
         return null;
      }

      

   }//end

// Rest of the functions definition go in here

   public static void viewProfile(PizzaStore esql, String authorisedUser) {

      // query to find info for authorised user
      String phoneNumQuery = "SELECT phoneNum FROM Users WHERE login='" + authorisedUser + "'";
      String favoriteItemsQuery = "SELECT favoriteItems FROM Users WHERE login='" + authorisedUser + "'";

      List<List<String>> results;

      try {
         // print out users
         System.out.println("");
         System.out.println("--------------------------------");
         System.out.print("Phone Number: ");
         results = esql.executeQueryAndReturnResult(phoneNumQuery);
         System.out.println(results.get(0));
         System.out.print("Favorite Item(s): ");
         results = esql.executeQueryAndReturnResult(favoriteItemsQuery);
         System.out.println(results.get(0));
         System.out.println("--------------------------------");
         System.out.println("");
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }

   }


   public static void updateProfile(PizzaStore esql, String authorisedUser) {
      
      Scanner myObj = new Scanner(System.in);
      int rowCount;
      String query;

      // display user's profile
      viewProfile(esql, authorisedUser);

      // ask user what they would like to update
      System.out.println("What would you like to update?");
      System.out.println("------------------");
      System.out.println("1. Phone Number");
      System.out.println("2. Favorite Item");
      System.out.println("3. Password");
      System.out.println("------------------");
      System.out.println("9. Go back");

      switch (readChoice()){
         // update phone number
         case 1: 
            String newPhoneNumber;
            // prompt user for new phone number
            do {
               System.out.print("Enter your new phone number: "); 
               newPhoneNumber = myObj.nextLine();

               //length of phone number restrictions 
               if (newPhoneNumber.length() != 12 ) {
                  System.out.println("invalid phone number: must be in XXX-XXX-XXXX format");
                  continue; //prompts for password again 
               }

               String regex = "\\d{3}-\\d{3}-\\d{4}";
               Pattern pattern = Pattern.compile(regex);
               Matcher matcher = pattern.matcher(newPhoneNumber);

               if (!matcher.matches()) {
                  System.out.println("invalid phone number: must be in XXX-XXX-XXXX format");
                  continue;
               }
               break;
            } while (true);

            // query to update user's phone number in db
            query = "UPDATE Users SET phoneNum = '" + newPhoneNumber + "' WHERE login = '" + authorisedUser + "'";

            try {
               // execute query
               esql.executeUpdate(query);
               System.out.println("Phone number updated!");
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }
            break;

         // update favorite items
         case 2:
            //list of items
            List<String> favItemsResult = new ArrayList<>(); 

            // print and add items to favItemsResult
            query = "SELECT * FROM Items";

            try {
               List<List<String>> results = esql.executeQueryAndReturnResult(query);
               
               for (int i = 0; i < results.size(); i++) {
                  List<String> record = results.get(i);
                  System.out.println(record);

                  //create a list of items 
                  favItemsResult.add(record.get(0).trim());
               }
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }

            boolean validInput;
            String favItems;

            do {
               validInput = true;
               // prompt user for new favorite item
               System.out.print("Enter your new favorite item(s) (leave \", \" between multiple items): "); 
               favItems = myObj.nextLine();

               // Split the input string by spaces
               String[] favItemsArr = favItems.split(",\\s+");  // "\\s+" handles multiple spaces
               List<String> typesList = Arrays.asList(favItemsArr);

               // Check if each favitem in the input is present in the favItemsResult list
               for (String favItem : favItemsArr) {
                  if (!favItemsResult.contains(favItem)) {
                     System.err.println("Error: Item '" + favItem + "' is not valid.");
                     validInput = false;
                     break;
                  }
               }

               // If input is valid, break the loop; otherwise, prompt for input again
               if (validInput) break; // Exit the loop if the input is valid

            }
            while (!validInput);

            // query to update user's phone number in db
            query = "UPDATE Users SET favoriteItems = '" + favItems + "' WHERE login = '" + authorisedUser + "'";
            try {
               // execute query
               esql.executeUpdate(query);
               System.out.println("Favorite item updated!");
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }
            break;

         // update password
         case 3: 
            String password;
            do {
               // prompt user for new password
               System.out.println("Enter your new password: "); 
               password = myObj.nextLine();

               // password validation
               if (password.length() < 1) {
                  System.out.println("invalid password: must be at least 1 character");
                  continue;
               }
               if (password.length() > 30) {
                  System.out.println("invalid password: must be less than 31 characters");
                  continue;
               }
               break;
            } while (true);

            // query to update user's password in db
            query = "UPDATE Users SET password = '" + password + "' WHERE login = '" + authorisedUser + "'";
            try {
               // execute query
               esql.executeUpdate(query);
               System.out.println("Password updated.");
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }
            break;

         case 9: break;
         default : System.out.println("Unrecognized choice!"); break;
      }
   }



   public static void viewMenu(PizzaStore esql) {
      //custom search validation loop
      //switch case menu and then verify option is exceptable 
      boolean viewmenu = true;
      while(viewmenu) {
         System.out.println("VIEW MENU");
         System.out.println("---------");
         System.out.println("1. Full Menu");
         System.out.println("2. Filter Menu");
         System.out.println("3. Main Menu");
         switch (readChoice()){

            case 1: //show the entire menu to the user 

               String defaultQuery = "SELECT * FROM Items";
            
               try {
                  List<List<String>> results = esql.executeQueryAndReturnResult(defaultQuery);

                  for (int i = 0; i < results.size(); i++) {
                     List<String> record = results.get(i);
                     System.out.println(record);
                  }

               } catch (SQLException e) {
                  // Handle SQL exception (e.g., problem with the query or connection)
                  System.err.println("SQL error: " + e.getMessage());
               }
               break;


            case 2: 
               //print out availible types for user to filter by 
               List<String> typesResult = new ArrayList<>(); //list of types

               String typeListQuery = "SELECT DISTINCT typeOfItem FROM Items";
         
               try {
                  List<List<String>> results = esql.executeQueryAndReturnResult(typeListQuery);
                   

                  for (int i = 0; i < results.size(); i++) {
                     String record = results.get(i).get(0);
                     System.out.println(record);

                     //create a list of types 
                     typesResult.add(record.trim());
                  }

               } catch (SQLException e) {
                  // Handle SQL exception (e.g., problem with the query or connection)
                  System.err.println("SQL error: " + e.getMessage());
               }

               //type input 
               String types = "";
               //types input
               Scanner myObj = new Scanner(System.in);

               do {
                  
                  System.out.print("Filter by types? (leave empty to view all, space for multiple): ");
                  types = myObj.nextLine();

                  // Check if the input is empty (indicating view all types)
                  if (types.trim().isEmpty()) {
                     System.out.println("Viewing all types.");
                     break;  // Break the loop if the input is empty (viewing all)
                  }

                  // Split the input string by spaces
                  String[] typesArray = types.split("\\s+");  // "\\s+" handles multiple spaces
                  List<String> typesList = Arrays.asList(typesArray);

                  // Flag to track if the input is valid
                  boolean validInput = true;

                  // Check if each type in the input is present in the typesResult list
                  for (String type : typesList) {
                     if (!typesResult.contains(type)) {
                        System.err.println("Error: Type '" + type + "' is not valid.");
                        validInput = false;
                     }
                  }

                  // If input is valid, break the loop; otherwise, prompt for input again
                  if (validInput) {
                     break; // Exit the loop if the input is valid
                  }

               }while (true);


               // Price input
               String priceInput = "";
               
               double filterPrice = -1;

               do {
                     System.out.print("Filter under a price? (leave empty to view all prices): ");
                     priceInput = myObj.nextLine();

                     // Check if the input is empty (indicating view all prices)
                     if (priceInput.trim().isEmpty()) {
                        System.out.println("Viewing all prices.");
                        break;  // Break the loop if the input is empty (viewing all)
                     }

                     // Try to parse the price input into a valid number (Double)
                     try {
                        filterPrice = Double.parseDouble(priceInput);
                     } catch (NumberFormatException e) {
                        System.err.println("Error: Invalid price format. Please enter a valid number.");
                        continue;  // Prompt again if the input is not a valid number
                     }

                     // Flag to track if the input is valid
                     boolean validInput = true;

                     // Check if the filter price is valid (should be positive)
                     if (filterPrice <= 0) {
                        System.err.println("Error: Price must be greater than 0.");
                        validInput = false;
                     }

                     // If the input is valid, break the loop; otherwise, prompt for input again
                     if (validInput) {
                        break; // Exit the loop if the input is valid
                     }

               } while (true);  // Repeat until valid input is provided

               // Order input
               String orderInput = "";

               do {
                     System.out.print("Choose order (a for ascending, d for descending, or leave empty for no order): ");
                     orderInput = myObj.nextLine().trim().toLowerCase();  // Normalize to lower case

                     // Validate the order input
                     if (orderInput.isEmpty()) {
                        System.out.println("No order selected. Results will be in the default order.");
                        break;
                     } else if (orderInput.equals("a") || orderInput.equals("d")) {
                        System.out.println("Selected order: " + (orderInput.equals("a") ? "ascending" : "descending"));
                        break;
                     } else {
                        System.err.println("Error: Invalid order. Please enter 'a', 'd', or leave empty for no order.");
                     }

               } while (true);  // Repeat until valid order is provided


               //once you have query parameters execute and show said query (simplier than input validation)

               StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Items WHERE 1=1");

               // Filter by types (if user entered valid types)
               if (!types.trim().isEmpty()) {
                  String[] typesArray = types.split("\\s+");  // Split the input types by space
                  for (int i = 0; i < typesArray.length; i++) {
                     if (i == 0) {
                           // For the first type, use "AND" to start the filter condition
                           queryBuilder.append(" AND (");
                     }
                     queryBuilder.append("typeOfItem LIKE '%").append(typesArray[i].trim()).append("'");
                     
                     if (i < typesArray.length - 1) {
                           // Add "OR" between types
                           queryBuilder.append(" OR ");
                     }
                  }
                  // Close the parentheses for the OR conditions
                  queryBuilder.append(")");
               }


               // Filter by price (if user entered a valid price)
               if (filterPrice > 0) {
                  queryBuilder.append(" AND price <= ").append(filterPrice);
               }

               // Add order clause (if the user specified an order)
               if (!orderInput.isEmpty()) {
                  // Assuming orderClause contains 'ASC' or 'DESC'
                  if (orderInput.equals("a")) {
                     queryBuilder.append(" ORDER BY price ASC");
                  } else if (orderInput.equals("d")) {
                     queryBuilder.append(" ORDER BY price DESC");
                  }
               }

               // Final custom query string
               String customQuery = queryBuilder.toString();
               //System.out.println(customQuery);
            
               try {
                  List<List<String>> results = esql.executeQueryAndReturnResult(customQuery);

                  for (int i = 0; i < results.size(); i++) {
                     List<String> record = results.get(i);
                     System.out.println(record);
                  }

               } catch (SQLException e) {
                  // Handle SQL exception (e.g., problem with the query or connection)
                  System.err.println("SQL error: " + e.getMessage());
               }
               break;

               
            case 3: viewmenu = false; break;

            default : System.out.println("Unrecognized choice!"); break;
         }
      }
   }


   public static void placeOrder(PizzaStore esql, String authorisedUser) {
      String storeCity;

      //store city validation loop 
      do {
         //city input
         Scanner myObj = new Scanner(System.in);
         System.out.print("Enter Store City: ");
         storeCity = myObj.nextLine();

         //length of city restrictions 
         if(storeCity.length() < 5 ){
            System.out.println("invalid city name: must be at least 5 characters");
            continue; //prompts for city again 
         }


         //store city existence 

         String existsQuery = "SELECT * FROM Store S WHERE S.city = '" + storeCity + "'";

         try {
            // Use the executeQuery method from the PizzaStore instance (esql)
            int existingCount = esql.executeQuery(existsQuery); // This will return the number of rows returned

            if (existingCount == 0) { // if more than 0 there is a user with that name 
                System.out.println("No stores in " + storeCity);
                continue; // prompts for city again
            }
            
            break; // Exit the loop when store in city exists 

        } catch (SQLException e) {
            System.err.println("Error checking city: " + e.getMessage());
            continue; // Continue the loop if there is an exception
        }
      }while (true);


      //at this point we have the city of the store, handle non unique store city concept later 

      //item input validation 

      List<String> items = new ArrayList<String>();
      List<Integer> quantities = new ArrayList<Integer>(); // To store item quantities
      String item = "";

      do {
         //item input loop
         Scanner myObj = new Scanner(System.in);
         System.out.print("Enter Item (one at a time)(\"done\" to finish): ");
         item = myObj.nextLine();

         if(item.equals("done") && items.size() != 0){
            break; //exit loop everything 
         }

         if(item == "done" && items.size() == 0){
            System.out.print("Need at least 1 item for an order");
            continue; //exit loop everything 
         }


         //length of item restrictions 
         if(item.length() < 3 ){
            System.out.println("invalid item name: must be at least 3 characters");
            continue; //prompts for item again 
         }


         //item existence 
         if(item != "done"){

            String existsQuery = "SELECT * FROM Items I WHERE I.itemName = '" + item + "'";

            try {
               // Use the executeQuery method from the PizzaStore instance (esql)
               int existingCount = esql.executeQuery(existsQuery); // This will return the number of rows returned

               if (existingCount == 0) { // if more than 0 there is a user with that name 
                  System.out.println("No items named " + item);
                  continue; // prompts for city again
               }

               // Prompt for quantity
               int quantity = 0;
               do {
                  System.out.print("Enter quantity for " + item + ": ");
                  String quantityInput = myObj.nextLine();
                  try {
                     quantity = Integer.parseInt(quantityInput);

                     if (quantity <= 0) {
                        System.out.println("Quantity must be a positive integer");
                        continue; // Prompt for quantity again
                     }
                     break; // Valid quantity entered
                  } catch (NumberFormatException e) {
                     System.out.println("Invalid input. Please enter a valid integer.");
                  }
               } while (true);
               
               items.add(item);
               quantities.add(quantity);

            } catch (SQLException e) {
               System.err.println("Error checking city: " + e.getMessage());
               continue; // Continue the loop if there is an exception
            }
         }
         

      }while (true);

      //at this point we have a list of items and a list of their quantities
         //input info into FoodOrder, (orderID,login,storeID, total price, orderTimestamp, orderStatus)
         //input each ith place in both lists into item in order (orderID, item, quantity)
      
      //storeID 
      String query = "SELECT storeID FROM Store WHERE city = '" + storeCity + "'";
      int storeID = -1;
      try {
         // Execute query and retrieve results
         List<List<String>> results = esql.executeQueryAndReturnResult(query);

         // Parse the first storeID from the results
         storeID = Integer.parseInt(results.get(0).get(0));


      } catch (SQLException e) {
         System.err.println("Error retrieving store: " + e.getMessage());
      }

      //calculating the total price of the order
      double totalPrice = 0; // Initialize total price

      for (int i = 0; i < items.size(); i++) {
         String itemIn = items.get(i);       // Current item name
         int quantityIn = quantities.get(i); // Quantity for the current item

         // Query to get the price of the current item
         String queryPrice = "SELECT price FROM Items WHERE itemName = '" + itemIn + "'";

         try {
               // Execute the query and retrieve the price
               List<List<String>> resultsPrice = esql.executeQueryAndReturnResult(queryPrice);

               if (resultsPrice.isEmpty()) {
                  System.out.println("Error: Item '" + itemIn + "' not found in the database.");
                  continue;
               }

               // Parse the price from the query result
               double price = Double.parseDouble(resultsPrice.get(0).get(0));

               // Calculate the cost for the current item and add to total price
               totalPrice += price * quantityIn;

         } catch (SQLException e) {
               System.err.println("Error retrieving price for item '" + itemIn + "': " + e.getMessage());
         }
      }

      // Get the last inserted orderID (for use in ItemsInOrder table)
      String getOrderIDinQuery = "SELECT MAX(orderID) FROM FoodOrder";
      int orderIDin = 0;
      try {
         List<List<String>> results = esql.executeQueryAndReturnResult(getOrderIDinQuery);

         if (!results.isEmpty() && !results.get(0).isEmpty()) {
               orderIDin = Integer.parseInt(results.get(0).get(0)) + 1; // Increment the orderID
         }
      } catch (SQLException e) {
         System.err.println("Error retrieving orderID: " + e.getMessage());
      }



      // Insert the order into the FoodOrder table
      String insertOrderQuery = "INSERT INTO FoodOrder (orderID, login, storeID, totalPrice, orderTimestamp, orderStatus) "
            + "VALUES (" + orderIDin + ", '" + authorisedUser + "', " + storeID + ", " + totalPrice + ", CURRENT_TIMESTAMP, 'Pending')";

      try {
         esql.executeUpdate(insertOrderQuery);
      } catch (SQLException e) {
         System.err.println("Error inserting order: " + e.getMessage());
         return;  // Exit if the insertion fails
      }


      // Get the last inserted orderID (for use in ItemsInOrder table)
      String getOrderIDQuery = "SELECT MAX(orderID) FROM FoodOrder";
      int orderID = 0;
      try {
         // Use executeQueryAndReturnResult to get the result as a List of Lists
         List<List<String>> results = esql.executeQueryAndReturnResult(getOrderIDQuery);
         
         // Check if the results are not empty and extract the first value (which is the MAX(orderID))
         if (!results.isEmpty() && !results.get(0).isEmpty()) {
            orderID = Integer.parseInt(results.get(0).get(0));  // Get the orderID of the most recent order
         }
      } catch (SQLException e) {
         System.err.println("Error retrieving orderID: " + e.getMessage());
      }

      // Step 7: Insert Items into the ItemsInOrder Table
      for (int i = 0; i < items.size(); i++) {
         String itemName = items.get(i);       // Current item name
         int quantity = quantities.get(i);     // Quantity for the current item

         String insertItemQuery = "INSERT INTO ItemsInOrder (orderID, itemName, quantity) "
                  + "VALUES (" + orderID + ", '" + itemName + "', " + quantity + ")";
         try {
               esql.executeUpdate(insertItemQuery);
         } catch (SQLException e) {
               System.err.println("Error inserting item: " + e.getMessage());
         }
      }

      // Step 8: Confirmation
      System.out.println("Your order has been placed successfully!");
      System.out.println("Total price: $" + totalPrice);
   }


   public static void viewAllOrders(PizzaStore esql, String authorisedUser) {
      // similar to view menu/stores, but on condition that login = current user
      // how to check current user condition, changed login to return the username for queries 
      //authorisedUser is the variable we are using 

      String defaultQuery = "SELECT * FROM FoodOrder WHERE login = '" + authorisedUser + "'";
            
         try {
            List<List<String>> results = esql.executeQueryAndReturnResult(defaultQuery);

            for (int i = 0; i < results.size(); i++) {
               List<String> record = results.get(i);
               System.out.println(record);
            }

         } catch (SQLException e) {
            // Handle SQL exception (e.g., problem with the query or connection)
            System.err.println("SQL error: " + e.getMessage());
         }
   }


   public static void viewRecentOrders(PizzaStore esql, String authorisedUser) {
      //same as teh view all orders just add sorting on timestamp and add a limit with no offset 
      String defaultQuery = "SELECT * FROM FoodOrder WHERE login = '" + authorisedUser + 
         "' ORDER BY orderTimestamp DESC LIMIT " + 5 + ";";

         try {
            List<List<String>> results = esql.executeQueryAndReturnResult(defaultQuery);

            for (int i = 0; i < results.size(); i++) {
               List<String> record = results.get(i);
               System.out.println(record);
            }

         } catch (SQLException e) {
            // Handle SQL exception (e.g., problem with the query or connection)
            System.err.println("SQL error: " + e.getMessage());
         }
   }


   public static void viewOrderInfo(PizzaStore esql, String authorisedUser) {      //add an input loop for orderID
      String orderID; 
      do {
         //password input
         Scanner myObj = new Scanner(System.in);
         System.out.print("Enter orderID to search: ");
         orderID = myObj.nextLine();

         //length of username restrictions 
         if(orderID.length() < 1 ){
            System.out.println("invalid orderID: must be at least 1 character");
            continue; //prompts for orderID again 
         }

         break;
      }while (true);


      String defaultQuery = "SELECT * FROM FoodOrder WHERE login = '" + authorisedUser + "' and orderID = " + orderID;

      try {
         List<List<String>> results = esql.executeQueryAndReturnResult(defaultQuery);

         if (results.size() < 1){
            System.out.println("No orders match order ID");
         }

         for (int i = 0; i < results.size(); i++) {
            List<String> record = results.get(i);
            System.out.println(record);
         }

      } catch (SQLException e) {
         // Handle SQL exception (e.g., problem with the query or connection)
         System.err.println("SQL error: " + e.getMessage());
      }
   }

   
   public static void viewStores(PizzaStore esql) {
      //same logic as the default view menu logic 

      String defaultQuery = "SELECT * FROM Store";
            
         try {
            List<List<String>> results = esql.executeQueryAndReturnResult(defaultQuery);

            for (int i = 0; i < results.size(); i++) {
               List<String> record = results.get(i);
               System.out.println(record);
            }

         } catch (SQLException e) {
            // Handle SQL exception (e.g., problem with the query or connection)
            System.err.println("SQL error: " + e.getMessage());
         }
   }


   public static void updateOrderStatus(PizzaStore esql, String authorisedUser) {

      String query;
      Scanner myObj = new Scanner(System.in);
      int rowCount;

      // check if manager or driver
      query = "SELECT * FROM Users U WHERE U.login='" + authorisedUser + "' AND (role='manager' OR role='driver')";
      try {
         rowCount = esql.executeQuery(query);
         if (rowCount == 0) {
            System.out.println("Access Denied.");
            return;
         }
      } catch (SQLException e) {
         System.err.println(e.getMessage());
         return;
      }

      String orderID;
      do {
         System.out.print("Enter the ID of the order you would like to update: ");
         orderID = myObj.nextLine();
         // check if order exists
         query = "SELECT * FROM FoodOrder WHERE orderID = " + orderID;
         try {
            rowCount = esql.executeQuery(query);
            if (rowCount == 0) {
               System.out.println("Order not found.");
               continue;
            }
         } catch (SQLException e) {
            System.err.println(e.getMessage());
            continue;
         }
         break;
      } while (true);

      System.out.println("Enter the new order status: ");
      System.out.println("-------------------");
      System.out.println("1. Complete");
      System.out.println("2. Incomplete");
      System.out.println("-------------------");
      System.out.println("9. Go back");
      String newOrderStatus;
      switch (readChoice()) {
         case 1: newOrderStatus = "complete"; break;
         case 2: newOrderStatus = "incomplete"; break;

         case 9: return;
         default: System.out.println("Unrecognized choice!"); return;
      }

      // update order
      query = "UPDATE FoodOrder SET orderStatus = '" + newOrderStatus + "' WHERE orderID = '" + orderID + "'";
      try {
         esql.executeUpdate(query);
         System.out.println("Order status updated!");
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
   
   }


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

      // display menu
      query = "SELECT * FROM Items";
      try {
         // print out users
         List<List<String>> results = esql.executeQueryAndReturnResult(query);

         for (int i = 0; i < results.size(); i++) {
            List<String> record = results.get(i);
            System.out.println(record);
         }
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }

      // prompt if the user would like to add a new item or update an existing item
      System.out.println("");
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
               System.out.print("What is the name of the item you would like to add (enter to go back)? ");
               itemName = myObj.nextLine();

               if (itemName.isEmpty()) return;

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


   public static String updateUser(PizzaStore esql, String authorisedUser) {

      String query;
      Scanner myObj = new Scanner(System.in);
      int rowCount;

      // check if manager role
      query = "SELECT * FROM Users WHERE login='" + authorisedUser + "' AND role='manager'";
      try {
         rowCount = esql.executeQuery(query);
         // if not manager, deny access and return
         if (rowCount == 0) {
            System.out.println("Access Denied.");
            return authorisedUser;
         }
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }

      // output list of users
      query = "SELECT * FROM Users";
      try {
         // print out users
         List<List<String>> results = esql.executeQueryAndReturnResult(query);

         for (int i = 0; i < results.size(); i++) {
            List<String> record = results.get(i);
            System.out.println(record);
         }
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
      
      System.out.println("Would you like to UPDATE or DELETE a user? ");
      System.out.println("----------------------------------");
      System.out.println("1. Update");
      System.out.println("2. Delete");
      System.out.println("----------------------------------");
      System.out.println("9. Go back");
      switch (readChoice()) {
         case 1:
            // get user to update
            String userToUpdate;
            System.out.print("Enter the username of the user you would like to update: ");
            userToUpdate = myObj.nextLine();

            // check if user exists
            query = "SELECT * FROM Users WHERE login='" + userToUpdate + "'";
            try {
               rowCount = esql.executeQuery(query);
               // if doesn't exist, return
               if (rowCount == 0) {
                  System.out.println("User does not exist.");
                  return authorisedUser;
               }
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }

            // prompt which part of the user to update
            System.out.println("Which area of the user would you like to update?");
            System.out.println("-----------------------------------");
            System.out.println("1. Role");
            System.out.println("2. Favorite Items");
            System.out.println("3. Phone Number");
            System.out.println("-----------------------------------");
            System.out.println("9. Go back");
            switch (readChoice()) {
               case 1:
                  String newRole;
                  System.out.println("Enter new role: ");
                  System.out.println("---------------");
                  System.out.println("1. Manager");
                  System.out.println("2. Driver");
                  System.out.println("3. Customer");
                  System.out.println("---------------");
                  switch (readChoice()) {
                     case 1: newRole = "manager"; break;
                     case 2: newRole = "driver"; break;
                     case 3: newRole = "customer"; break;

                     default: System.out.println("Unrecognized choice!"); return authorisedUser;
                  }
                  query = "UPDATE Users SET role = '" + newRole + "' WHERE login = '" + userToUpdate + "'";
                  break;
               case 2:
                  String newFavoriteItems;
                  do {
                     // MAYBE SHOW MENU ITEMS HERE
                     System.out.print("Enter new favorite item: ");
                     newFavoriteItems = myObj.nextLine();

                     // check if item exists
                     query = "SELECT * FROM Items WHERE itemName = '" + newFavoriteItems + "'";
                     try {
                        rowCount = esql.executeQuery(query);
                        if (rowCount == 0) {
                           System.out.println("Item doesn't exist.");
                           continue; // item doesn't exist
                        }
                        else break; // item exists
                     } catch (SQLException e) {
                        System.err.println(e.getMessage());
                     }
                  } while (true);

                  query = "UPDATE Users SET favoriteItems = '" + newFavoriteItems + "' WHERE login = '" + userToUpdate + "'";
                  break;
               case 3:
                  String newPhoneNumber;
                  do {
                     System.out.print("Enter new phone number in XXX-XXX-XXXX format: ");
                     newPhoneNumber = myObj.nextLine();

                     //length of phone number restrictions 
                     if (newPhoneNumber.length() != 12 ) {
                        System.out.println("invalid phone number: must be in XXX-XXX-XXXX format");
                        continue; //prompts for password again 
                     }

                     String regex = "\\d{3}-\\d{3}-\\d{4}";
                     Pattern pattern = Pattern.compile(regex);
                     Matcher matcher = pattern.matcher(newPhoneNumber);

                     if (!matcher.matches()) {
                        System.out.println("invalid phone number: must be in XXX-XXX-XXXX format");
                        continue;
                     }
                     break;
                  } while (true);

                  query = "UPDATE Users SET phoneNum = '" + newPhoneNumber + "' WHERE login = '" + userToUpdate + "'";
                  break;

               case 9: return authorisedUser;
               default: System.out.println("Unrecognized choice!"); return authorisedUser;
            }
            // execute query to update user
            try {
               esql.executeUpdate(query);
               System.out.println("User updated.");
               return authorisedUser;
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }
            break;
         case 2:
            // get user to delete
            String userToDelete;
            System.out.print("Enter the username of the user you would like to delete: ");
            userToDelete = myObj.nextLine();

            // check if user exists
            query = "SELECT * FROM Users WHERE login='" + userToDelete + "'";
            try {
               rowCount = esql.executeQuery(query);
               // if doesn't exist, return
               if (rowCount == 0) {
                  System.out.println("User does not exist.");
                  return authorisedUser;
               }
            } catch (SQLException e) {
               System.err.println(e.getMessage());
            }

            // delete user
            query = "DELETE FROM Users WHERE login = '" + userToDelete + "'";
            try {
               esql.executeUpdate(query);
               System.out.println("User deleted.");
               return null;
            } catch (SQLException e) {
               System.out.println(e.getMessage());
            }
            break;

         case 9: return authorisedUser;
         default: System.out.println("Unrecognized choice!"); return authorisedUser;
      }

      return authorisedUser;
   }

}//end PizzaStore

