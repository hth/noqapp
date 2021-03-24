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
        TN INT              NOT NULL,
        DT CHAR(5)          NOT NULL,
        DN VARCHAR(100)     NOT NULL,
        BT CHAR(3)          NOT NULL,
        QS VARCHAR(1)       NOT NULL,
        TI VARCHAR(100),
        NS TINYINT,
        RA TINYINT,
        HR TINYINT,
        RV VARCHAR(256),
        SN VARCHAR(20),
        SB DATETIME,
        SE DATETIME,
        BN VARCHAR(24)      NOT NULL,
        ST VARCHAR(1),
        AC VARCHAR(13),
        V INT,
        U DATETIME,
        C DATETIME,
        A TINYINT,
        D TINYINT,
        PRIMARY KEY (ID)
    );
    
    CREATE TABLE noqapp_test.PURCHASE_ORDER
    (
        ID VARCHAR(24)      NOT NULL,
        QID VARCHAR(13)     NOT NULL,
        BS VARCHAR(24)      NOT NULL,
        BN VARCHAR(24)      NOT NULL,
        QR VARCHAR(30)      NOT NULL,
        DID VARCHAR(50)     NOT NULL,
        DM VARCHAR(2)       NOT NULL,
        PM VARCHAR(3),
        PY VARCHAR(2),
        PS VARCHAR(2)       NOT NULL,
        AI VARCHAR(24),
        RA TINYINT,
        RV VARCHAR(256),
        ST VARCHAR(1),
        TN INT              NOT NULL,
        DT CHAR(5)          NOT NULL,
        SD INT,
        PP VARCHAR(10),
        OP VARCHAR(10),
        TA VARCHAR(10),
        GT VARCHAR(10),
        BT CHAR(3)          NOT NULL, 
        PQ VARCHAR(13),
        FQ VARCHAR(13),
        CQ VARCHAR(13),
        CI VARCHAR(24),
        SN VARCHAR(20),
        SB DATETIME,
        SE DATETIME,
        TI VARCHAR(100)     NOT NULL,
        TR VARCHAR(100),
        TM VARCHAR(100),
        TV CHAR(1)          NOT NULL DEFAULT 'U',
        DN VARCHAR(100)     NOT NULL,
        AN VARCHAR(64),
        V INT,
        U DATETIME,
        C DATETIME,
        A TINYINT,
        D TINYINT,
        PRIMARY KEY (ID)
    );
        
    CREATE TABLE noqapp_test.PURCHASE_ORDER_PRODUCT
    (
        ID VARCHAR(24)      NOT NULL,
        PN VARCHAR(100)     NOT NULL,
        PP INT,
        TA CHAR(3),
        PD INT,
        PT CHAR(2),
        UV INT,
        UM CHAR(2),
        PS INT,
        PQ INT,
        PO VARCHAR(24)      NOT NULL,
        QID VARCHAR(13)     NOT NULL,
        BS VARCHAR(24)      NOT NULL,
        BN VARCHAR(24)      NOT NULL,
        QR VARCHAR(30)      NOT NULL,
        BT CHAR(3)          NOT NULL,
        V INT,
        U DATETIME,
        C DATETIME,
        A TINYINT,
        D TINYINT,
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
