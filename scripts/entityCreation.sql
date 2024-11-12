CREATE TABLE User (
    login CHAR(15) NOT NULL UNIQUE,
    password CHAR(50) NOT NULL, 
    phoneNumber CHAR(60) NOT NULL,
    role CHAR(10) NOT NULL,
    favoriteItem CHAR(500)
);

CREATE TABLE Item (


);

CREATE TABLE Store (


);
