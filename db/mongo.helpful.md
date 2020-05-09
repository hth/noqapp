##Mongo Helpful Queries
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
