##Mongo Helpful Queries

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
    