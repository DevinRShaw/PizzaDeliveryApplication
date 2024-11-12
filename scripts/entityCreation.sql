CREATE TABLE user (
    login CHAR(15) PRIMARY KEY,
    password CHAR(50) NOT NULL, 
    phoneNumber CHAR(60) NOT NULL,
    role CHAR(10) NOT NULL,
    favoriteItem CHAR(500)
);

CREATE TABLE item (
    itemName CHAR(50) PRIMARY KEY,
    type CHAR(40) NOT NULL,
    price float NOT NULL, 
    ingredients CHAR(500) NOT NULL,
    description CHAR(600),
    imageURL CHAR(256)
);

CREATE TABLE store (
    storeID CHAR(50) PRIMARY KEY,
    address CHAR(40) NOT NULL,
    city CHAR(40) NOT NULL, 
    state CHAR(40) NOT NULL,
    isOpen CHAR(40) NOT NULL,
    reviewScore float
);

-- User views Item relationship (many to many, both partial)
-- Review for on delete and on update parts of foreign key 
-- Review for NOT NULL attributes as a team 
CREATE TABLE views (
    user_id CHAR(15),
    item_id CHAR(50),
    FOREIGN KEY (user_id) REFERENCES user(login),
    FOREIGN KEY (item_id) REFERENCES item(itemName)
);

-- Item availible_at Store relationship (many to many, both partial)
-- Review for on delete and on update parts of foreign key 
-- Review for NOT NULL attributes as a team 

CREATE TABLE available_at (
    item_id CHAR(40),
    store_id CHAR(50),
    FOREIGN KEY (store_id) REFERENCES store(storeID),
    FOREIGN KEY (item_id) REFERENCES item(itemName)
)