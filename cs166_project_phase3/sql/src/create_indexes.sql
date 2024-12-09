CREATE INDEX type_of_item
ON Items (typeOfItem);


CREATE INDEX idx_items_price 
ON Items(price);


CREATE UNIQUE INDEX idx_users_login 
ON Users(login);
