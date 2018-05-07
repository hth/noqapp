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

Make sure BusinessType in Token_QUEUE is not HO
Change BusinessType in Business NAME from HO to DO


---- Mysql 

Update all HO to DO in mysql

Update all HO to DO in sql-lite 

