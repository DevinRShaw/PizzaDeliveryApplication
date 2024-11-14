DROP TABLE IF EXISTS P_User CASCADE;
DROP TABLE IF EXISTS Item CASCADE;
DROP TABLE IF EXISTS Store CASCADE;
DROP TABLE IF EXISTS P_Order CASCADE;
DROP TABLE IF EXISTS has;
DROP TABLE IF EXISTS views;
DROP TABLE IF EXISTS availableAt;


CREATE TABLE P_User (
    login CHAR(15) PRIMARY KEY,
    password CHAR(50) NOT NULL, 
    phoneNumber CHAR(60) NOT NULL,
    role CHAR(10) NOT NULL,
    favoriteItem CHAR(500)
);

CREATE TABLE Item (
    itemName CHAR(50) PRIMARY KEY,
    type CHAR(40) NOT NULL,
    price float NOT NULL, 
    ingredients CHAR(500) NOT NULL,
    description CHAR(600),
    imageURL CHAR(256)
);

CREATE TABLE Store (
    storeID char(50) PRIMARY KEY,
    address char(40) NOT NULL,
    city char(40) NOT NULL,
    state char(40) NOT NULL,
    isOpen char(40) NOT NULL,
    reviewStore float
);

CREATE TABLE P_Order (
    orderID char(60) PRIMARY KEY,
    orderTimestamp timestamp NOT NULL,
    orderStatus char(50) NOT NULL,
    totalPrice float NOT NULL,
    userLogin CHAR(15) NOT NULL, -- User mandatory
    storeID CHAR(50) NOT NULL, -- Store mandatory
    FOREIGN KEY (userLogin) REFERENCES P_User(login),
    FOREIGN KEY (storeID) REFERENCES Store(storeID)
);

CREATE TABLE has (
    orderID CHAR(60) NOT NULL,
    storeID CHAR(50) NOT NULL,
    PRIMARY KEY(orderID, storeID),
    FOREIGN KEY (orderID) REFERENCES P_Order(orderID),
    FOREIGN KEY (storeID) REFERENCES Store(storeID)
);

CREATE TABLE views (
    userLogin CHAR(15),
    itemName CHAR(50),
    PRIMARY KEY(userLogin, itemName),
    FOREIGN KEY (userLogin) REFERENCES P_User(login),
    FOREIGN KEY (itemName) REFERENCES Item(itemName)
);

CREATE TABLE availableAt (
    itemName CHAR(50),
    storeID CHAR(50),
    PRIMARY KEY(itemName, storeID),
    FOREIGN KEY (storeID) REFERENCES Store(storeID),
    FOREIGN KEY (itemName) REFERENCES Item(itemName)
);