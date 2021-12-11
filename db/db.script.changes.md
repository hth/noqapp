### Changed Dec 11, 2021

Changed field name

    db.getCollection('BIZ_NAME').update({"TAG" : {$exists : true}}, {$rename: {"TAG":"TG"}}, {multi: true});

### Changed Aug 19, 2021

Changed field name 

    db.getCollection('MP_HOUSEHOLD_ITEM').update({"LC" : {$exists : true}}, {$rename: {"LC":"VC"}}, {multi: true});
    db.getCollection('MP_PROPERTY_RENTAL').update({"LC" : {$exists : true}}, {$rename: {"LC":"VC"}}, {multi: true});

### Changed Jun 26, 2021

Remove null from UAI field

     db.getCollection('USER_PREFERENCE').update({UAI: {$type: 10}}, {$unset: {UAI: ""}}, {multi: true});

### Changed Jun 25, 2021

    Drop collection of INVITE

Reset of FT and FS

    db.getCollection('USER_PREFERENCE').update({"FS" : {$gt: []}}, {$set : {"FS" : []}}, {multi: true});
    db.getCollection('USER_PREFERENCE').update({"FT" : {$gt: []}}, {$set : {"FT" : []}}, {multi: true});

### Changed Jun 11, 2021

Delete record as not referenced anywhere 

    c.n.s.a.UserAuthenticationAnomaly.lambda$listOrphanData$0 Message=5fab8cfdb83d170ef6f1beab created Tue Nov 10 23:04:29 PST 2020 not being used

    c.n.s.a.MissingGeneratedUserId.populateWithMissingQID Message=Found missed QID=100000028449
    c.n.s.a.MissingGeneratedUserId.populateWithMissingQID Message=Found missed QID=100000031739

    db.getCollection('USER_ACCOUNT').find({"USER_AUTHENTICATION.$id" : ObjectId("5fab8cfdb83d170ef6f1beab")})
    db.getCollection('USER_AUTHENTICATION').find({_id : ObjectId("5fab8cfdb83d170ef6f1beab")})
    db.getCollection('USER_AUTHENTICATION').remove({_id : ObjectId("5fab8cfdb83d170ef6f1beab")})

### Changed May 29, 2021

    ALTER TABLE `noqapp_test`.`QUEUE`
    MODIFY DT CHAR(5) NOT NULL;   

TS had status as 'C', removed only for noqapp_test and noqapp table did not have 'C' as default

    ALTER TABLE `noqapp_test`.`QUEUE`
    MODIFY TS VARCHAR(1) NOT NULL;   

    ALTER TABLE `noqapp_test`.`QUEUE` ADD INDEX `queue_qid_idx` (`QID`);

### Changed May 22, 2021
    
    db.getCollection('REGISTERED_DEVICE').remove({"COR" : {$exists : false}})

### Changed April 4, 2021

    db.getCollection('REGISTERED_DEVICE').update({"DL" : {$exists : false}}, {$set: {DL : "en"}}, {multi: true});
    db.getCollection('REGISTERED_DEVICE').remove({"U" : {$lt : ISODate("2020-11-30")}})

### Changed April 2, 2021

    db.getCollection('USER_PROFILE').update({AD: {$exists : true}}, {$unset: {AD : ""}}, {multi: true});
    db.getCollection('USER_PROFILE').update({AO: {$exists : true}}, {$unset: {AO : ""}}, {multi: true});

### Changed Mar 24, 2021

For new version

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` RENAME COLUMN `DA` TO `AI`;

For older version

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    CHANGE COLUMN `DA` `AI` VARCHAR(24) NULL DEFAULT NULL ;

### Changed Mar 23, 2021
    
    db.getCollection('USER_PROFILE').update({QID : "100000001777"}, {$unset: {AD : ""}}, {multi: true});
    db.getCollection('USER_PROFILE').update({QID : "100000001824"}, {$unset: {AD : ""}}, {multi: true});
    db.getCollection('USER_PROFILE').update({QID : "100000002047"}, {$unset: {AD : ""}}, {multi: true});
    db.getCollection('USER_PROFILE').update({QID : "100000002052"}, {$unset: {AD : ""}}, {multi: true});
    db.getCollection('USER_PROFILE').update({QID : "100000014150"}, {$unset: {AD : ""}}, {multi: true});
    db.getCollection('USER_PROFILE').update({QID : "100000019912"}, {$unset: {AD : ""}}, {multi: true});
    db.getCollection('USER_PROFILE').update({QID : "100000020923"}, {$unset: {AD : ""}}, {multi: true});
    db.getCollection('USER_PROFILE').update({QID : "100000020974"}, {$unset: {AD : ""}}, {multi: true});

    db.getCollection('USER_ADDRESS').remove({QID : "100000008897"});
    db.getCollection('USER_ADDRESS').remove({QID : "100000032005"});
    db.getCollection('USER_ADDRESS').remove({QID : "100000032360"});

    db.getCollection('USER_PROFILE').update({AD : ""}, {$unset: {AD : ""}}, {multi: true});

### Changed Feb 24, 2021

    db.getCollection('USER_PREFERENCE').update({"FS" : {$gt: []}}, {$set : {"FS" : []}}, {multi: true});

Find the ones greater than zero items in list

    db.getCollection('USER_PREFERENCE').find({"FS" : {$gt: []}})

### Changed Dec 29, 2020

    db.getCollection('PUBLISH_ARTICLE').update({"CO" : {$exists : true}}, {$rename: {"CO":"DS"}}, {multi: true});

### Changed Dec 20, 2020
    
    db.getCollection('REGISTERED_DEVICE').update({ST: {$exists : true}}, {$unset: {ST : ""}}, {multi: true});

### Changed Dec 01, 2020

Added Payment settings for each store accepting order.  
    
    db.getCollection('BIZ_STORE').find({"BT" : "RS"})
    db.getCollection('BIZ_STORE').find({"BT" : "ST"})
    db.getCollection('BIZ_STORE').find({"BT" : "GS"})
    db.getCollection('BIZ_STORE').find({"BT" : "CD"})

    db.getCollection('BIZ_STORE').update({"BT" : "RS"}, {$set: {AP : ["COD", "ONP"], AD : ["HOM", "PIK"]}}, {multi: true});
    db.getCollection('BIZ_STORE').update({"BT" : "ST"}, {$set: {AP : ["COD", "ONP"], AD : ["HOM", "PIK"]}}, {multi: true});
    db.getCollection('BIZ_STORE').update({"BT" : "GS"}, {$set: {AP : ["COD", "ONP"], AD : ["HOM", "PIK"]}}, {multi: true});
    db.getCollection('BIZ_STORE').update({"BT" : "CD"}, {$set: {AP : ["COD", "ONP"], AD : ["HOM", "PIK"]}}, {multi: true});

### Changed Nov 07, 2020

    db.getCollection('BIZ_STORE').update({"AD" : {$exists : true}}, {$rename: {"AD":"SA"}}, {multi: true});

### Changed Nov 06, 2020

    db.getCollection('REGISTERED_DEVICE').remove({"U" : {$lte : ISODate("2020-01-01 03:55:25.151Z")}})

### Changed Oct 30, 2020
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
        ADD COLUMN `GT` VARCHAR(10) AFTER `TA`;
        
    SET SQL_SAFE_UPDATES = 0;
    update `noqapp_test`.`PURCHASE_ORDER` as Q1,
          (select Q2.ID, Q2.DT, Q2.TN, Q2.OP from `noqapp_test`.`PURCHASE_ORDER` as Q2 where Q2.GT is null) AS Q3 
    set Q1.GT = Q3.OP
    where
     Q1.ID = Q3.ID;  
    SET SQL_SAFE_UPDATES = 1;        

### Changed Oct 20, 2020
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
        ADD COLUMN `TA` VARCHAR(10) AFTER `OP`;
        
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER_PRODUCT` 
        ADD COLUMN `TA` CHAR(3) AFTER `PP`;
        
    SET SQL_SAFE_UPDATES = 0;
    UPDATE `noqapp_test`.`PURCHASE_ORDER_PRODUCT` SET TA = "ZE";
    UPDATE `noqapp_test`.`PURCHASE_ORDER` SET TA = "0";
    SET SQL_SAFE_UPDATES = 1;  
    
### Changed Oct 15, 2020

    db.getCollection('BIZ_STORE').update({"TA" : {$exists : true}}, {$rename: {"TA":"SC"}}, {multi: true});          

### Changed Sept 15, 2020

    db.getCollection('STORE_PRODUCT').update({IM: {$exists : false}}, {$set: {IM : ""}}, {multi: true});

### Changed Sept 14, 2020
        
    db.getCollection('STORE_PRODUCT').update({IM: {$exists : true}}, {$unset: {IM : ""}}, {multi: true});

### Changed Sept 07, 2020

     db.getCollection('TOKEN_QUEUE').update({}, {$set: {AP:"Q"}}, {multi: true});

### Changed Aug 28, 2020

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `DT` CHAR(10) AFTER `TN`;
    
    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `DT` CHAR(10) AFTER `TN`;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER`
    MODIFY TN INT NOT NULL;
    
    SET SQL_SAFE_UPDATES = 0;
    update `noqapp_test`.`QUEUE` as Q1,
          (select Q2.ID, Q2.DT, Q2.TN from `noqapp_test`.`QUEUE` as Q2 where Q2.DT is null) AS Q3 
    set Q1.DT = Q3.TN
    where
     Q1.ID = Q3.ID; 
    SET SQL_SAFE_UPDATES = 1;
    
    ALTER TABLE `noqapp_test`.`QUEUE`
    MODIFY DT CHAR(10) NOT NULL;
    
    SET SQL_SAFE_UPDATES = 0;
    update `noqapp_test`.`PURCHASE_ORDER` as Q1,
          (select Q2.ID, Q2.DT, Q2.TN from `noqapp_test`.`PURCHASE_ORDER` as Q2 where Q2.DT is null) AS Q3 
    set Q1.DT = Q3.TN
    where
     Q1.ID = Q3.ID;  
    SET SQL_SAFE_UPDATES = 1;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER`
    MODIFY DT CHAR(10) NOT NULL;   
       
### Changed Aug 22, 2020

    db.getCollection('BIZ_NAME').update({}, {$set: {SL:"en_IN"}}, {multi: true});
    
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
