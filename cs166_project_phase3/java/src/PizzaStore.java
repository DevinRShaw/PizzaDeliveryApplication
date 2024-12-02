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
                   case 1: viewProfile(esql); break;
                   case 2: updateProfile(esql); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql, authorisedUser); break;
                   case 6: viewRecentOrders(esql, authorisedUser); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql); break;
                   case 10: updateMenu(esql); break;
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



/*default query 
      String defaultQuery = "SELECT * FROM Items";
         
      try {
         List<List<String>> results = esql.executeQueryAndReturnResult(listQuery);

         for (int i = 0; i < results.size(); i++) {
            List<String> record = results.get(i);
            System.out.println(record);
         }

      } catch (SQLException e) {
            // Handle SQL exception (e.g., problem with the query or connection)
            System.err.println("SQL error: " + e.getMessage());
      }
*/

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















// Rest of the functions definition go in here

   public static void viewProfile(PizzaStore esql) {}
   public static void updateProfile(PizzaStore esql) {}
   
   public static void placeOrder(PizzaStore esql) {}


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
   public static void viewOrderInfo(PizzaStore esql) {}

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






   public static void updateOrderStatus(PizzaStore esql) {}
   public static void updateMenu(PizzaStore esql) {}
   public static void updateUser(PizzaStore esql) {}


}//end PizzaStore

