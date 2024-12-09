CREATE INDEX idx_item_name
ON Items (itemName);


CREATE INDEX idx_items_price 
ON Items(price);


CREATE UNIQUE INDEX idx_users_login 
ON Users(login);
