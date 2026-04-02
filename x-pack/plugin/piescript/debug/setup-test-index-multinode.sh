#!/usr/bin/env bash
# Create the piescript-test index with 3 shards (one per node in a 3-node cluster).
# Works with security enabled or disabled.
# Safe to run repeatedly — deletes the old index first.

BASE="localhost:9200"

# Auto-detect security: try unauthenticated, then known credential combos
AUTH=""
if ! curl -s -o /dev/null -w '%{http_code}' "$BASE" 2>/dev/null | grep -q '^200$'; then
  for creds in "elastic:password" "elastic-admin:elastic-password" "test_user:x-pack-test-password"; do
    if curl -s -u "$creds" -o /dev/null -w '%{http_code}' "$BASE" 2>/dev/null | grep -q '^200$'; then
      AUTH="-u $creds"
      break
    fi
  done
fi

echo "=== Cancelling in-flight piescript tasks ==="
curl -s $AUTH -X POST "$BASE/_tasks/_cancel?actions=indices:data/read/piescript*&wait_for_completion=false" 2>/dev/null | jq '.node_failures // empty' 2>/dev/null
sleep 1

echo "=== Deleting old index ==="
curl -s $AUTH -X DELETE "$BASE/piescript-test" | jq

echo ""
echo "=== Creating index (3 shards, 0 replicas) ==="
curl -s $AUTH -X PUT "$BASE/piescript-test" \
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
curl -s $AUTH -X POST "$BASE/piescript-test/_bulk?refresh=true" \
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
curl -s $AUTH "$BASE/_cat/shards/piescript-test?v"
