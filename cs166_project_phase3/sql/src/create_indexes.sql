-- Indexes for Users table
CREATE UNIQUE INDEX idx_users_login ON Users(login); -- Redundant if PRIMARY KEY is used
CREATE INDEX idx_users_phoneNum ON Users(phoneNum); -- For lookups by phone number
CREATE INDEX idx_users_role ON Users(role); -- For filtering by user role
CREATE INDEX idx_users_favoriteItems ON Users USING gin(to_tsvector('english', favoriteItems)); -- Optional full-text search on favoriteItems

-- Indexes for Items table
CREATE UNIQUE INDEX idx_items_itemName ON Items(itemName); -- Redundant if PRIMARY KEY is used
CREATE INDEX idx_items_typeOfItem ON Items(typeOfItem); -- For filtering by item type
CREATE INDEX idx_items_price_type ON Items(price, typeOfItem); -- For combined filtering by price and item type
CREATE INDEX idx_items_ingredients ON Items USING gin(to_tsvector('english', ingredients)); -- Full-text search on ingredients

-- Indexes for Store table
CREATE UNIQUE INDEX idx_store_storeID ON Store(storeID); -- Redundant if PRIMARY KEY is used
CREATE INDEX idx_store_city_state ON Store(city, state); -- For geographic searches
CREATE INDEX idx_store_isOpen ON Store(isOpen); -- For filtering by store open/closed status
CREATE INDEX idx_store_reviewScore ON Store(reviewScore); -- For sorting or filtering by review score

-- Indexes for FoodOrder table
CREATE UNIQUE INDEX idx_foodOrder_orderID ON FoodOrder(orderID); -- Redundant if PRIMARY KEY is used
CREATE INDEX idx_foodOrder_login ON FoodOrder(login); -- For retrieving user orders
CREATE INDEX idx_foodOrder_storeID ON FoodOrder(storeID); -- For retrieving orders by store
CREATE INDEX idx_foodOrder_orderTimestamp ON FoodOrder(orderTimestamp); -- For time-based order queries
CREATE INDEX idx_foodOrder_store_time ON FoodOrder(storeID, orderTimestamp); -- Combined filtering by store and time

-- Indexes for ItemsInOrder table
CREATE UNIQUE INDEX idx_itemsInOrder_orderID_itemName ON ItemsInOrder(orderID, itemName); -- Redundant if PRIMARY KEY is used
CREATE INDEX idx_itemsInOrder_itemName ON ItemsInOrder(itemName); -- For retrieving orders containing a specific item
CREATE INDEX idx_itemsInOrder_order_quantity ON ItemsInOrder(orderID, quantity); -- For analyzing quantities in orders
