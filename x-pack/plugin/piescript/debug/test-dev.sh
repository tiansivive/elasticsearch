#!/usr/bin/env bash
# Test the /_piescript/dev endpoint — shows full pipeline debug output.

echo "=== let expression ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let x = 1 + 2 in x"}' | jq

echo ""
echo "=== query (type-checks against real index, eval_error expected) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "query `FROM piescript-test`"}' | jq

echo ""
echo "=== query with pipe ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "query `FROM piescript-test | WHERE age > 25 | LIMIT 5`"}' | jq

echo ""
echo "=== let-bound query ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in docs"}' | jq

echo ""
echo "=== type error ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "1 + true"}' | jq

echo ""
echo "=== parse error ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let = in"}' | jq
