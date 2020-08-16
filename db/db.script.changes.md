### Changed Aug 16, 2020

    db.getCollection('USER_ACCOUNT').update({}, {$set: {OC:NumberInt(0)}}, {multi: true});

### Changes July 20, 2020

    Decreased auto delete records from 2 months to 15 days

### Changes July 5, 2020

    db.getCollection('BIZ_NAME').update({"LS" : {$exists : true}}, {$rename:{"LS":"SD"}}, false, true);
    
    Note: Deleted store (trial store created by bani camp) - with QR Code - 5f005dfe5a857416901c2516

### Changes June 18, 2020

    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `AC` VARCHAR(13) AFTER `ST`;

### Change May 29, 2020
    
    db.getCollection('BUSINESS_CUSTOMER').remove({"BN" : "5eb3b9c0017c222cd473dded"})

### Change May 26, 2020
    
    ALTER DATABASE noqapp CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
    
    ALTER TABLE QUEUE CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ALTER TABLE PURCHASE_ORDER CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

### Change May 25, 2020

    // Removed Authorized User to store and moved to BizName
    db.getCollection('BIZ_STORE').update({}, {$unset: {AU:false}}, {multi: true});
    
    db.getCollection('BIZ_NAME').update({}, {$set: {PA:"F"}}, {multi: true});
    db.getCollection('BUSINESS_CUSTOMER').update({}, {$set: {PL:"I"}}, {multi: true});
    
    drop index business_customer_idx

### Change May 18, 2020

    //Email migrated to AWS and has to reset to send out pending mails. 
    db.getCollection('MAIL').find({"MS" : "F"}).count()
    db.getCollection('MAIL').update({"MS" : "F"}, {$set: {"MS":"N"}}, {multi: true});

    // Added Authorized User to store
    db.getCollection('BIZ_STORE').update({}, {$set: {AU:false}}, {multi: true});

### Change May 17, 2020

    // Limited Service by Days
    db.getCollection('BIZ_NAME').update({}, {$set: {"LS":NumberInt(0)}}, {multi: true});

### Change May 8, 2020

    db.getCollection('BIZ_STORE').update({}, {$set: {AU:false}}, {multi: true});

### Change April 20, 2020

    ALTER TABLE `noqapp_test`.`QUEUE` MODIFY BT VARCHAR(3);
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` MODIFY BT VARCHAR(3);
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER_PRODUCT` MODIFY BT VARCHAR(3);

### Change Feb 18, 2020

    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `RR` VARCHAR(24) AFTER `BN`;
        
### Change Feb 8, 2020

    db.getCollection('BIZ_STORE').update({}, {$set: {WS:"E"}}, {multi: true});

### Change July 20, 2019

    db.getCollection('BIZ_STORE').update({}, {$unset: {PE:""}}, {multi: true});
    db.getCollection('BIZ_STORE').update({}, {$set: {PS:"O"}}, {multi: true});

### Change June 4, 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `CQ` VARCHAR(13) AFTER `FQ`;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `CI` VARCHAR(24) AFTER `CQ`;

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER`
    MODIFY SN VARCHAR(20);
    
    /* For Anita P, demo account. */
    SET SQL_SAFE_UPDATES = 0;
    UPDATE noqapp.PURCHASE_ORDER SET QID = '100000000935'  where TI = 'ad518e-6862-106-5cc1958d';
    SET SQL_SAFE_UPDATES = 1;  
    
    db.getCollection('BIZ_STORE').update({}, {$unset: {SP:""}}, {multi: true});
    db.getCollection('USER_PREFERENCE').dropIndex( "USER_PROFILE" );
    db.getCollection('USER_PREFERENCE').update({}, {$unset: {USER_PROFILE:""}}, {multi: true});
    db.getCollection('USER_PREFERENCE').update({},  { $set : {"PS" : "R"}}, false, true);
    db.getCollection('USER_PREFERENCE').update({},  { $set : {"FN" : "R"}}, false, true);
    
    /* This store is no longer active. */
    db.BIZ_STORE.remove({"_id" : ObjectId("5cf0bbfd96799f4f42326faf"), "QR" : "5cf0bbfd96799f4f42326fb0"});

### Change May 21, 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER_PRODUCT` 
    ADD COLUMN `PT` CHAR(2) AFTER `PD`;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER_PRODUCT` 
    ADD COLUMN `UV` INT(5) AFTER `PT`;
        
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER_PRODUCT` 
    ADD COLUMN `UM` CHAR(2) AFTER `UV`;
            
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER_PRODUCT` 
    ADD COLUMN `PS` INT(3) AFTER `UM`;

### Change April 30, 2019

###### Modified from column size of 64 to 100
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER_PRODUCT`
    MODIFY PN CHAR(100) NOT NULL;

### Change April 27, 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `DID` VARCHAR(50) AFTER `QR`;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `ST` VARCHAR(1) AFTER `RV`;

### Change April 26, 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `PQ` VARCHAR(13) AFTER `BT`;
        
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `FQ` VARCHAR(13) AFTER `PQ`;
### Change March 20, 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `TV` CHAR(1) NOT NULL DEFAULT 'U' AFTER `TM`;
    
    ALTER TABLE `noqapp_test`.`QUEUE`
    MODIFY BT CHAR(2) NOT NULL;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER`
    MODIFY BT CHAR(2) NOT NULL;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER_PRODUCT`
    MODIFY BT CHAR(2) NOT NULL;
    
    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `TI` VARCHAR(100) AFTER `QS`;

### Change March 11, 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `PP` VARCHAR(10) AFTER `SD`;

### Change March 1, 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    CHANGE COLUMN `PT` `PM` VARCHAR(2);
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `PY` VARCHAR(2) AFTER `PM`;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `TR` VARCHAR(100) AFTER `TI`;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `TM` VARCHAR(100) AFTER `TR`;
    
    SET SQL_SAFE_UPDATES = 0;
    UPDATE `noqapp_test`.`PURCHASE_ORDER` SET PY = "PP";
    SET SQL_SAFE_UPDATES = 1;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER`
    MODIFY PM VARCHAR(3)     

### Change Jan 15, 2019

Add column ST

    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `ST` VARCHAR(1) AFTER `BN`;

### Change Oct 26, 2018

Add column AN

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `AN` VARCHAR(64) AFTER `DN`;

### Changes Aug, 2018

Add column BN

    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `BN` VARCHAR(30) AFTER `SE`;
