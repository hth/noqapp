### DSL Query Example

#### Search with Filter

Example from Stackoverflow[link](https://stackoverflow.com/questions/16776260/elasticsearch-multi-match-with-filter) 

    {
      "from" : 0,
      "size" : 10,
      "sort" : "publishDate",
      "query": {
        "bool": {  
          "must" : {
            "multi_match" : {
              "query":      "wedding",
              "type":       "most_fields",
              "fields":     [ "title", "text" ]
            }
          },
          "filter": {
            "term": {
              "locale": "english"
            }
          }
        }
      }
    }  
    
#### Search with multiple fields

    {
      "query": {
        "multi_match" : {
          "query" : "this is a test",
          "fields" : [ "subject^3", "message" ] 
        }
      }
    }
    
#### Search with Should match

    {
      "query": {
        "bool": {
          "should": [
            { "match": { "title":          "quick brown fox" }},
            { "match": { "title.original": "quick brown fox" }},
            { "match": { "title.shingles": "quick brown fox" }}
          ]
        }
      }
    }  
    
#### Search all fields

    curl -XGET 'localhost:9200/_search?pretty' -H 'Content-Type: application/json' -d'
        {
            "query": {
                "match_all" : {}
            }
        }
        '
                
#### Search all fields

    curl -XGET 'localhost:9200/_search?pretty&scroll=1m' -H 'Content-Type: application/json' -d'
        {
            "query": {
                "match_all" : {}
            }
        }
        '        
        
#### Search all fields with distance
    
    curl -XGET 'localhost:9200/_search?pretty' -H 'Content-Type: application/json' -d'
        {
          "from": 0,
          "query": {
            "bool": {
              "must": {
                "match_all": {}
              },
              "filter": {
                "geo_distance": {
                  "distance": "150km",
                  "GH": "te7ut71tgd9n"
                }
              }
            }
          },
          "size": 10
        }
        '    
    
    
### Changing Mapping with Zero Downtime

- Follow [link](https://www.elastic.co/blog/reindex-is-coming)
- Time series [link](https://www.elastic.co/blog/timeseries-if-then-else-with-timelion?baymax=rtp&storm=recommendation&elektra=blog&iesrc=rcmd&astid=8e0aef24-80c1-423b-87a9-f591a5514e99&at=16&rcmd_source=WIDGET&req_id=58f77773-6de7-41b3-8b08-6ba654335ead)

### How to make elasticsearch add the timestamp field to every document in all indices?

[Refer](https://stackoverflow.com/questions/17136138/how-to-make-elasticsearch-add-the-timestamp-field-to-every-document-in-all-indic?rq=1)

You can do this by providing it when creating your index.

    $curl -XPOST localhost:9200/test -d '{
    "settings" : {
        "number_of_shards" : 1
    },
    "mappings" : {
        "_default_":{
            "_timestamp" : {
                "enabled" : true,
                "store" : true
            }
        }
      }
    }'
    
That will then automatically create a _timestamp for all stuff that you put in the index. Then after indexing something when requesting the _timestamp field it will be returned.

### Timestamp not appearing in Kibana

[Refer](https://stackoverflow.com/questions/29429201/timestamp-not-appearing-in-kibana?noredirect=1&lq=1)          


You'll need to take these quick steps first :

- Go to Settings → Advanced.
- Edit the metaFields and add "_timestamp". Hit save.
- Now go back to Settings → Indices and _timestamp will be available in the drop-down list for "Time-field name".


### Elastic Queries

##### Note the difference in including query with search and no search parameter
    
    curl http://localhost:9200/noqapp_biz_store/_search/?pretty=true -- will list SOURCE DATA on the index
    curl http://localhost:9200/noqapp_biz_store/?pretty=true -- will lists META DATA only
 
##### For getting Mapping
 
    curl http://localhost:9200/noqapp_biz_store/biz_store/_mapping/?pretty=true
 
##### Different GET queries
 
    curl -XGET http://localhost:9200/noqapp_biz_store/biz_store/_search?q=country:India
    curl http://localhost:9200/noqapp_biz_store/_search/?pretty=true
    curl -X GET http://localhost:9200/
    curl http://localhost:9200/x/_search/?pretty=true
    curl http://localhost:9200/noqapp_biz_store/x/_search/?pretty=true
     
##### Delete Single Index
 
    curl -XDELETE 'localhost:9200/twitter?pretty'
    
##### Delete All Index
     
    curl -XDELETE 'localhost:9200/*?pretty'
    
##### Insert New Index and Type with Mapping

This creates Index `test` and Type `type1`. If index already exists then it fails with `400` bad http request error.

`Dynamic false` will prevent from adding fields at runtime. For new field add it here, drop index and 
create new index by changing index version appended from v1 to v2. 

    curl -XPUT 'localhost:9200/test?pretty' -H 'Content-Type: application/json' -d'
    {
        "mappings" : {
            "type1" : {
                "dynamic": false,
                "properties" : {
                    "field1" : { "type" : "text" }
                }
            }
        }
    }
    '    

#### More elastic query example

    curl -XGET "http://10.0.0.74:9200/noqueue_v5_biz_store_spatial/_search" -H 'Content-Type: application/json' -d'{  "query": {    "match_all": {}  }}'
    
    curl -XGET "http://10.0.0.74:9200/noqueue_v5_biz_store_spatial/_search" -H 'Content-Type: application/json' -d
    '
    {
      "query": {
        "bool" : {
          "should" : [
            { "term" : { "BT.keyword": "GS" } },
            { "term" : { "BT.keyword": "DO" } }
          ],
          "minimum_should_match" : 1,
          "boost" : 1.0
        }
      }
    }
    '
    
    GET /noqueue_v5_biz_store_spatial/_search
    {
      "query": {
        "bool" : {
          "should" : [
            { "term" : { "BT.keyword": "GS" } },
            { "term" : { "BT.keyword": "DO" } }
          ],
          "minimum_should_match" : 1,
          "boost" : 1.0
        }
      }
    }
    
    
    GET /noqueue_v5_biz_store_spatial/_search
    {
     "query": {
       "bool" : {
         "filter" : [
           {
             "geo_distance" : {
               "GH" : [
                 72.81469993293285,
                 18.906699903309345
               ],
               "distance" : 4000000.0,
               "distance_type" : "arc",
               "validation_method" : "STRICT",
               "ignore_unmapped" : false,
               "boost" : 1.0
             }
           }
         ],
         "should" : [
           {
             "term" : {
               "BT.keyword" : {
                 "value" : "GS",
                 "boost" : 1.0
               }
             }
           },
           {
             "term" : {
               "BT.keyword" : {
                 "value" : "DO",
                 "boost" : 1.0
               }
             }
           }
         ],
         "adjust_pure_negative" : true,
         "boost" : 1.0
       }
     }
    }
       
