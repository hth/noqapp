### MySQL Script

    CREATE SCHEMA `noqapp_test` DEFAULT CHARACTER SET utf8 ;
    
#### Add user
Added user to this schema

#### Add Account limit to the user
Set default from 0 to 1000
    
#### Create Table     

    CREATE TABLE noqapp_test.QUEUE
    (
        ID VARCHAR(30),
        QR VARCHAR(30),
        DID VARCHAR(50),
        RID VARCHAR(13),
        TN INT(11),
        DN VARCHAR(100),
        QS VARCHAR(10),
        NS TINYINT(1),
        V INT(11),
        U DATETIME,
        C DATETIME,
        A TINYINT(1),
        D TINYINT(1)
    );