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