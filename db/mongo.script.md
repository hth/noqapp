### Change April 28 2019

    Set DOB as 1900-01-01 for 15 patients
    
    100000000217
    100000000202
    100000000226
    100000000014
    100000000081
    100000000104
    100000000136
    100000000068
    100000000146
    100000000267
    100000000132
    100000000123
    100000000058
    100000000135
    100000000138

### Change April 03 2019

    db.BIZ_STORE.update({"SP" : "N"},  { $set : {"SP" : "O"}}, false, true);
    drop index m_record_qr_idx and it re-creates with sparse

### Change March 11 2019

    db.BIZ_STORE.update({}, {$unset:{"CQ":1}}, false, true);   

### Change March 1, 2019

     db.PURCHASE_ORDER.update({}, {$rename:{"PT":"PM"}}, false, true);
     
     ##### This will not be used on prod as its being deploted for first time
     db.PURCHASE_ORDER.update({"PY" : "UP"},  { $set : {"PY" : "PP"}}, false, true);
     
### Change Feb 02, 2019 

    drop index business_customer_bc_idx from BUSINESS_CUSTOMER

Next Release Update Mongo

    db.STORE_CATEGORY.dropIndex("store_category_idx");
    db.STORE_CATEGORY.dropIndex("store_category_name_idx");
    db.STORE_HOUR.dropIndex("store_hour_idx");
    
    db.STORE_PRODUCT.dropIndex("store_product_idx");
    db.PURCHASE_ORDER.dropIndex("po_qid_bz_idx");
    db.PURCHASE_ORDER.dropIndex("po_cqr_idx");
    
    db.PURCHASE_ORDER.dropIndex("por_qid_bz_idx");
    db.PURCHASE_ORDER.dropIndex("por_cqr_idx");
    db.STORE_CATEGORY.update({}, {$rename:{"BZ":"BS"}}, false, true);
    
    db.STORE_HOUR.update({}, {$rename:{"BZ":"BS"}}, false, true);
    db.STORE_PRODUCT.update({}, {$rename:{"BZ":"BS"}}, false, true);
    db.PURCHASE_ORDER.update({}, {$rename:{"BZ":"BS"}}, false, true);
    
    db.PURCHASE_ORDER.update({}, {$rename:{"CQR":"QR"}}, false, true);
    db.PURCHASE_ORDER_PRODUCT.update({}, {$rename:{"BZ":"BS"}}, false, true);
    db.PURCHASE_ORDER_PRODUCT.update({}, {$rename:{"CQR":"QR"}}, false, true);

##### Also in Mongo (DO NOT FORGET)

    1. Make sure BusinessType in Token_QUEUE is not HO
    2. Change BusinessType in Business NAME from HO to DO
    
    db.BIZ_STORE.update({"BT" : "HO"},  { $set : {"BT" : "DO"}}, false, true);
    
### Drop index
    queue_idx from QueueEntity  
    
    OR
      
    db.QUEUE.drop();  

### Mysql 

Update all HO to DO in mysql

    ALTER TABLE `noqapp`.`QUEUE` 
    ADD COLUMN `BT` VARCHAR(2) NOT NULL DEFAULT 'DO' AFTER `DN`;

    SET SQL_SAFE_UPDATES = 0;
    UPDATE noqapp.QUEUE 
    SET
        BT = "DO"
    WHERE 
        BT = "HO";
    SET SQL_SAFE_UPDATES = 1;    

Update all HO to DO in sql-lite 

