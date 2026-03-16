#!/usr/bin/env bash
# Test the /_piescript/dev endpoint — shows full pipeline debug output.

echo "=== let expression ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let x = 1 + 2 in x"}' | jq

echo ""
echo "=== query (type-checks + evaluates against real index) ==="
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
echo "=== bare builtin (map) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "map"}' | jq

echo ""
echo "=== partial application (map with identity fn) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "map (fn x -> x)"}' | jq

echo ""
echo "=== map over query stream ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in map (fn r -> r.name) docs"}' | jq

echo ""
echo "=== filter over query stream ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in filter (fn r -> r.active) docs"}' | jq

echo ""
echo "=== reduce over query stream ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in reduce (fn acc x -> acc + x.age) 0 docs"}' | jq

echo ""
echo "=== map with pipe syntax ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "query `FROM piescript-test` |> map (fn r -> r.name)"}' | jq

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
