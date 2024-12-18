/* Replace the location to where you saved the data files*/
/*/home/csmajs/dshaw013/PizzaDeliveryApplication/cs166_project_phase3/data*/

COPY Users
FROM '/home/csmajs/mnguy602/PizzaDeliveryApplication/cs166_project_phase3/data/users.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Items
FROM '/home/csmajs/mnguy602/PizzaDeliveryApplication/cs166_project_phase3/data/items.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Store
FROM '/home/csmajs/mnguy602/PizzaDeliveryApplication/cs166_project_phase3/data/store.csv'
WITH DELIMITER ',' CSV HEADER;

COPY FoodOrder
FROM '/home/csmajs/mnguy602/PizzaDeliveryApplication/cs166_project_phase3/data/foodorder.csv'
WITH DELIMITER ',' CSV HEADER;

COPY ItemsInOrder
FROM '/home/csmajs/mnguy602/PizzaDeliveryApplication/cs166_project_phase3/data/itemsinorder.csv'
WITH DELIMITER ',' CSV HEADER;
