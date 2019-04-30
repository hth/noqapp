### Change April 30 2019

###### Modified from column size of 64 to 100
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER_PRODUCT`
    MODIFY PN CHAR(100) NOT NULL;

### Change April 27 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `DID` VARCHAR(50) AFTER `QR`;
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `ST` VARCHAR(1) AFTER `RV`;

### Change April 26 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `PQ` VARCHAR(13) AFTER `BT`;
        
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `FQ` VARCHAR(13) AFTER `PQ`;
### Change March 20 2019

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

### Change March 11 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `PP` VARCHAR(10) AFTER `SD`;

### Change March 1 2019

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

### Change Jan 15 2019

Add column ST

    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `ST` VARCHAR(1) AFTER `BN`;

### Change Oct 26 2018

Add column AN

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `AN` VARCHAR(64) AFTER `DN`;

### Changes Aug 2018

Add column BN

    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `BN` VARCHAR(30) AFTER `SE`;