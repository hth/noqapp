### MySQL Script

    CREATE SCHEMA `token_test` DEFAULT CHARACTER SET utf8 ;
    
#### Add user
Added user to this schema
    
#### Create Table     

    CREATE TABLE token_test.QUEUE
    (
        QR VARCHAR(30),
        DID VARCHAR(50),
        TN INT,
        DN VARCHAR(100),
        QS VARCHAR(10),
        NS BOOLEAN,
        V INT,
        U DATETIME,
        C DATETIME,
        A BOOLEAN,
        D BOOLEAN
    );