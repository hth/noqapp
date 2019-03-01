### Change March 1 2019

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    CHANGE COLUMN `PT` `PM` VARCHAR(2);
    
    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `PY` VARCHAR(2) AFTER `PM`;
    
    SET SQL_SAFE_UPDATES = 0;
    UPDATE `noqapp_test`.`PURCHASE_ORDER` SET PY = "PP";
    SET SQL_SAFE_UPDATES = 1;    

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