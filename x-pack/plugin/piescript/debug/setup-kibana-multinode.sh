#!/usr/bin/env bash
# Set up a multinode ES cluster for Kibana development.
#
# Prerequisites:
#   ./gradlew run -Drun.license_type=trial -I ../scripts/es-dev-config/multinode.gradle --stacktrace
#
# This keeps security ENABLED (the default) so Kibana can connect.
# Default superuser: test_user / x-pack-test-password
#
# After running this script, start Kibana with:
#   yarn start --no-base-path
#
# Login to Kibana as: test_user / x-pack-test-password

BASE="localhost:9200"

# Auto-detect credentials — try common defaults from gradlew run
USER=""
for creds in "elastic:password" "elastic-admin:elastic-password" "test_user:x-pack-test-password"; do
  if curl -s -u "$creds" -o /dev/null -w '%{http_code}' "$BASE" 2>/dev/null | grep -q '^200$'; then
    USER="$creds"
    break
  fi
done
if [ -z "$USER" ]; then
  echo "ERROR: could not authenticate to $BASE with any known credentials"
  exit 1
fi
echo "Using credentials: ${USER%%:*}"

echo ""
echo "=== Waiting for cluster ==="
until curl -s -u "$USER" "$BASE/_cluster/health" | grep -q '"status"'; do
  echo "  waiting..."
  sleep 2
done
echo "  cluster is up"

echo ""
echo "=== Cluster health ==="
curl -s -u "$USER" "$BASE/_cluster/health" | jq '{status, number_of_nodes, active_primary_shards}'

echo ""
echo "=== Creating piescript-test index (3 shards) ==="
curl -s -u "$USER" -X DELETE "$BASE/piescript-test" 2>/dev/null | jq '.acknowledged // .error.type' 2>/dev/null
curl -s -u "$USER" -X PUT "$BASE/piescript-test" \
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
curl -s -u "$USER" -X POST "$BASE/piescript-test/_bulk?refresh=true" \
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
' | jq '{errors, took, items: (.items | length)}'

echo ""
echo "=== Shard allocation ==="
curl -s -u "$USER" "$BASE/_cat/shards/piescript-test?v"

echo ""
echo "=== Node info ==="
curl -s -u "$USER" "$BASE/_cat/nodes?v&h=name,ip,node.role"

# Set passwords LAST — changing elastic password invalidates our current credentials
echo ""
echo "=== Setting elastic password to 'changeme' (Kibana dev default) ==="
curl -s -u "$USER" -X POST "$BASE/_security/user/elastic/_password" \
  -H 'Content-Type: application/json' \
  -d '{"password": "changeme"}' | jq

# Now set kibana_system AFTER elastic password change, using new elastic creds
echo ""
echo "=== Setting kibana_system password to 'changeme' ==="
curl -s -u "elastic:changeme" -X POST "$BASE/_security/user/kibana_system/_password" \
  -H 'Content-Type: application/json' \
  -d '{"password": "changeme"}' | jq

echo ""
echo "========================================"
echo "  Ready for Kibana"
echo "========================================"
echo ""
echo "  Kibana dev (yarn start --no-base-path) uses elastic:changeme by default."
echo "  No kibana.yml changes needed."
echo ""
echo "  Login to Kibana:  elastic / changeme"
echo "  Curl access:      curl -u elastic:changeme localhost:9200/..."
echo "========================================"
