CREATE TABLE User (
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


);
