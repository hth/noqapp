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
        RA TINYINT(1),
        HR TINYINT(1),
        ST DATETIME,
        V INT(11),
        U DATETIME,
        C DATETIME,
        A TINYINT(1),
        D TINYINT(1)
    );
    
#### Change DB temp 
    
    SELECT * FROM noqapp_test.QUEUE;
    SET SQL_SAFE_UPDATES = 0;
    UPDATE noqapp_test.QUEUE set QR = "58f11ee1aa664651e8bad4fb", DID = "123";
    SET SQL_SAFE_UPDATES = 1;    
    
#### Alter Table
    
    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `RA` TINYINT(1) NULL DEFAULT NULL AFTER `NS`,
    ADD COLUMN `HR` TINYINT(1) NULL DEFAULT NULL AFTER `RA`,
    ADD COLUMN `ST` DATETIME NULL DEFAULT NULL AFTER `HR`;
    
#### Insert System Date
    
    SET SQL_SAFE_UPDATES = 0;
    UPDATE noqapp_test.QUEUE set ST =  NOW();
    SET SQL_SAFE_UPDATES = 1;
    