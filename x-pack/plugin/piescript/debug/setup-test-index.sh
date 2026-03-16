#!/usr/bin/env bash
# Create a simple test index with explicit mappings and sample docs.
# Safe to run repeatedly — deletes the old index first.

curl -s -u elastic-admin:elastic-password -X DELETE 'localhost:9200/piescript-test' | jq

curl -s -u elastic-admin:elastic-password -X PUT 'localhost:9200/piescript-test' \
  -H 'Content-Type: application/json' \
  -d '{
    "settings": { "number_of_shards": 1, "number_of_replicas": 0 },
    "mappings": {
      "properties": {
        "name":   { "type": "keyword" },
        "age":    { "type": "integer" },
        "score":  { "type": "double" },
        "active": { "type": "boolean" }
      }
    }
  }' | jq

curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/piescript-test/_bulk?refresh=true' \
  -H 'Content-Type: application/json' \
  -d '
{"index":{}}
{"name":"alice","age":30,"score":9.5,"active":true}
{"index":{}}
{"name":"bob","age":25,"score":7.2,"active":false}
{"index":{}}
{"name":"carol","age":35,"score":8.8,"active":true}
' | jq
