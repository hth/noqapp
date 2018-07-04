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
    
    db.﻿BIZ_STORE.update({"BT" : "HO"},  { $set : {"BT" : "DO"}}, false, true);
    
### Drop index
    queue_idx from QueueEntity  
    
    OR
      
    db.QUEUE.drop();  

### Mysql 

Update all HO to DO in mysql

    SET SQL_SAFE_UPDATES = 0;
    UPDATE noqapp_test.QUEUE 
    SET
        BT = "DO"
    WHERE 
        BT = "HO";
    SET SQL_SAFE_UPDATES = 1;    

Update all HO to DO in sql-lite 

