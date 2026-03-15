#!/usr/bin/env bash
# Test the /_piescript/eval endpoint with a simple expression.

echo "=== let expression ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let x = 1 + 2 in x"}' | jq

echo ""
echo "=== record literal ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "{ x: 1, y: 2 }"}' | jq

echo ""
echo "=== query (will fail — eager eval not yet implemented) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "query `FROM piescript-test`"}' | jq
