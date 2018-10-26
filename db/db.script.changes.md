### Change Oct 26 2018

Add column AN

    ALTER TABLE `noqapp_test`.`PURCHASE_ORDER` 
    ADD COLUMN `AN` VARCHAR(64) AFTER `DN`;

### Changes Aug 2018

Add column BN

    ALTER TABLE `noqapp_test`.`QUEUE` 
    ADD COLUMN `BN` VARCHAR(30) AFTER `SE`;