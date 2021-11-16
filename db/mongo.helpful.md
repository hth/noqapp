##Mongo Helpful Queries

Search null data and other [Types](https://docs.mongodb.com/manual/reference/operator/query/type/).
For null, it is type 10

    db.getCollection('USER_PREFERENCE').find({UAI: {$type: 10}}).limit(2);
    db.getCollection('USER_PREFERENCE').update({UAI: {$type: 10}}, {$unset: {UAI: ""}}, {multi: true});

For other types 

    db.getCollection('USER_PREFERENCE').find({UAI: {$type: 2}, $where: "this.UAI.length > 1"}).limit(2);

Search within email `nel`

    db.getCollection('USER_PROFILE').find({"EM": /^.*nel.*$/})

Search by starting first Capital letter

    db.getCollection('USER_ACCOUNT').find({"UID" : {$regex : "([A-Z][a-zA-Z]*\s*)+"}})

Find registered 

    db.getCollection('BUSINESS_CUSTOMER').find({"BN" : "5eb3b9c0017c222cd473dded"}).count()
    db.getCollection('BUSINESS_CUSTOMER').find({"BN" : "5eb3b9c0017c222cd473dded", "CA" : {$all: ["AP"]} }).count()
    db.getCollection('BUSINESS_CUSTOMER').find({"BN" : "5eb3b9c0017c222cd473dded", "CA" : {$all: ["AP"]}, "PL" : "S" }).count()
    db.getCollection('BUSINESS_CUSTOMER').find({"BN" : "5eb3b9c0017c222cd473dded", "CA" : {$all: ["AP"]}, "PL" : "P" }).count()

Reset Queue

    db.TOKEN_QUEUE.update({},  { $set : {"LN" : NumberInt(0)}}, false, true);
    db.TOKEN_QUEUE.update({},  { $set : {"CS" : NumberInt(0)}}, false, true);

Beginning with 91 or 1

    db.getCollection('USER_PROFILE').find({"PH" : {$regex: "^91[9|8|7|6]"}}).count()
    db.getCollection('USER_PROFILE').find({"PH" : {$regex: "^911"}}).count()
    db.getCollection('USER_PROFILE').find({"PH" : {$regex: "^14"}}).count()
    
Not beginning with 91    

    db.getCollection('USER_PROFILE').find({"PH" : {$regex: "^((?!91).)*$"}}).count()
    
Total   
 
    db.getCollection('USER_PROFILE').find().count()
    
Update All

    db.M_RECORD.update({},  { $set : {"BS" : "5b392567783cea3482064bdd"}}, false, true);    
    
Remove one field

    db.getCollection('TOKEN_QUEUE').update({}, {$unset: {BC:""}} , {multi: true});
    
Registered Device Clean Up

    db.getCollection('REGISTERED_DEVICE').remove({"TK" : "BLACKLISTED"})
    db.getCollection('REGISTERED_DEVICE').remove({"U" : {$lte : ISODate("2018-12-01 09:52:42.492Z")}})
    
Find within range

    db.USER_PROFILE.find( { QID: { $gt: "100000000009", $lt : "100000000019" } } ); 

Remove Old Mail

    db.getCollection('MAIL').remove({"C" : {$lte : ISODate("2020-04-30 06:40:06.454Z")}})           

Updated Inventory

     db.getCollection('STORE_PRODUCT').update({"BS" : "5f0d754eb452cd1a5d46170d"}, {$set : {"IC":10000}}, false, true);

Queue Sort with selective columns  
    
    db.getCollection('QUEUE').find({"DN" : "Grocery/Liquor Ex-Servicemen"}, { TN: 1, SL: 1, C: 1, EB: 1 }).sort({ TN: -1 })
    db.getCollection('QUEUE').find({"QR" : "5ecda209547fbc370e824e1e"}, { TN: 1, SL: 1, C: 1, EB: 1 }).sort({ TN: -1 })
    
Regex NOT

    db.getCollection('EMAIL_VALIDATE').find({ QID: { $not: /^1.*/ } }).count()    

Compare columns within same collection

    db.getCollection('QUEUE').find( { $where: "this.SL != this.OSL" } )

Update field value

    db.getCollection('MP_PROPERTY_RENTAL').update({}, {$set: {"PU": ISODate("2021-11-30 00:00:00.000Z")}}, {multi: true});

