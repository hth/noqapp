### MySQL Script

    CREATE SCHEMA `noqapp_test` DEFAULT CHARACTER SET utf8 ;
    
#### Add user
Added user to this schema

#### Add Account limit to the user
Set default from `0` to `1000` to limit queries for a user. For `system user` if you set `0` 
as a value, then it gets endless connections.  
    
#### Create Table     

    ALTER TABLE noqapp_test.QUEUE MODIFY COLUMN ID VARCHAR (24) NOT NULL;
    ALTER TABLE noqapp_test.QUEUE MODIFY COLUMN QR VARCHAR (24) NOT NULL;
    
    //Prod add NOT NULL
    ALTER TABLE noqapp_test.QUEUE MODIFY COLUMN BN VARCHAR (24) NOT NULL;

    CREATE TABLE noqapp_test.QUEUE
    (
        ID VARCHAR(24)      NOT NULL,
        QR VARCHAR(24)      NOT NULL,
        DID VARCHAR(50)     NOT NULL,
        TS VARCHAR(1)       NOT NULL,
        QID VARCHAR(13),
        TN INT(10)          NOT NULL,
        DN VARCHAR(100)     NOT NULL,
        BT VARCHAR(2)       NOT NULL,
        QS VARCHAR(1)       NOT NULL,
        NS TINYINT(1),
        RA TINYINT(1),
        HR TINYINT(1),
        RV VARCHAR(256),
        SN VARCHAR(20),
        SB DATETIME,
        SE DATETIME,
        BN VARCHAR(24)      NOT NULL,
        ST VARCHAR(1),
        V INT(11),
        U DATETIME,
        C DATETIME,
        A TINYINT(1),
        D TINYINT(1),
        PRIMARY KEY (ID)
    );
    
    CREATE TABLE noqapp_test.PURCHASE_ORDER
    (
        ID VARCHAR(24)      NOT NULL,
        QID VARCHAR(13)     NOT NULL,
        BS VARCHAR(24)      NOT NULL,
        BN VARCHAR(24)      NOT NULL,
        QR VARCHAR(30)      NOT NULL,
        DM VARCHAR(2)       NOT NULL,
        PM VARCHAR(3)       NOT NULL,
        PY VARCHAR(2)       NOT NULL,
        PS VARCHAR(2)       NOT NULL,
        DA VARCHAR(128),
        RA TINYINT(1),
        RV VARCHAR(256),
        TN INT(10),
        SD INT(4),
        OP VARCHAR(10),
        BT VARCHAR(2)       NOT NULL, 
        SN VARCHAR(12),
        SB DATETIME,
        SE DATETIME,
        TI VARCHAR(100)     NOT NULL,
        TR VARCHAR(100)     NOT NULL,
        TM VARCHAR(100)     NOT NULL,
        DN VARCHAR(100)     NOT NULL,
        AN VARCHAR(64),
        V INT(11),
        U DATETIME,
        C DATETIME,
        A TINYINT(1),
        D TINYINT(1),
        PRIMARY KEY (ID)
    );
        
    CREATE TABLE noqapp_test.PURCHASE_ORDER_PRODUCT
    (
        ID VARCHAR(24)      NOT NULL,
        PN VARCHAR(64)      NOT NULL,
        PP INT(10),
        PD INT(5),
        PQ INT(3),
        PO VARCHAR(24)      NOT NULL,
        QID VARCHAR(13)    NOT NULL,
        BS VARCHAR(24)      NOT NULL,
        BN VARCHAR(24)      NOT NULL,
        QR VARCHAR(30)      NOT NULL,
        BT VARCHAR(2)       NOT NULL,
        V INT(11),
        U DATETIME,
        C DATETIME,
        A TINYINT(1),
        D TINYINT(1),
        PRIMARY KEY (ID)
    );    
    
    
#### Change DB temp 
    
    SELECT * FROM noqapp_test.QUEUE;
    SET SQL_SAFE_UPDATES = 0;
    UPDATE noqapp_test.QUEUE set QR = "58f11ee1aa664651e8bad4fb", DID = "123";
    SET SQL_SAFE_UPDATES = 1;    
    
#### Alter Table
    
    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `XX` TINYINT(1) NULL DEFAULT NULL AFTER `XY`,
    ADD COLUMN `TS` VARCHAR(1) NOT NULL DEFAULT 'C' AFTER `DID`,
    ADD COLUMN `BT` VARCHAR(2) NOT NULL DEFAULT 'DO' AFTER `DN`,
    ADD COLUMN `RV` VARCHAR(256) AFTER `HR`,
    ADD COLUMN `XA` DATETIME NULL DEFAULT NULL AFTER `XX`;
    
#### Alter Table
        
    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `RV` VARCHAR(256) AFTER `HR`;
    
#### Insert System Date
    
    SET SQL_SAFE_UPDATES = 0;
    UPDATE noqapp_test.QUEUE set ST =  NOW();
    SET SQL_SAFE_UPDATES = 1;
    
#### Updated Table

    SET SQL_SAFE_UPDATES = 0;
    UPDATE 
        noqapp.QUEUE
    SET 
        RA = 0,
        HR = 0,
        RV = "X"
    WHERE 
    	QID = "XX" and DID = "XXX";
    SET SQL_SAFE_UPDATES = 1;    
    