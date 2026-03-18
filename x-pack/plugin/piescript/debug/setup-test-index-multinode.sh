#!/usr/bin/env bash
# Create the piescript-test index with 3 shards (one per node in a 3-node cluster).
# Security disabled — no auth needed.
# Safe to run repeatedly — deletes the old index first.

BASE="localhost:9200"

echo "=== Deleting old index ==="
curl -s -X DELETE "$BASE/piescript-test" | jq

echo ""
echo "=== Creating index (3 shards, 0 replicas) ==="
curl -s -X PUT "$BASE/piescript-test" \
  -H 'Content-Type: application/json' \
  -d '{
    "settings": { "number_of_shards": 3, "number_of_replicas": 0 },
    "mappings": {
      "properties": {
        "name":   { "type": "keyword" },
        "age":    { "type": "integer" },
        "score":  { "type": "double" },
        "active": { "type": "boolean" }
      }
    }
  }' | jq

echo ""
echo "=== Indexing sample docs ==="
curl -s -X POST "$BASE/piescript-test/_bulk?refresh=true" \
  -H 'Content-Type: application/json' \
  -d '
{"index":{}}
{"name":"alice","age":30,"score":9.5,"active":true}
{"index":{}}
{"name":"bob","age":25,"score":7.2,"active":false}
{"index":{}}
{"name":"carol","age":35,"score":8.8,"active":true}
{"index":{}}
{"name":"dave","age":28,"score":6.1,"active":true}
{"index":{}}
{"name":"eve","age":22,"score":9.9,"active":false}
{"index":{}}
{"name":"frank","age":40,"score":5.5,"active":true}
' | jq

echo ""
echo "=== Shard allocation ==="
curl -s "$BASE/_cat/shards/piescript-test?v" 
