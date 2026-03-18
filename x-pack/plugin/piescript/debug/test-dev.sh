#!/usr/bin/env bash
# Test the /_piescript/dev endpoint — shows full pipeline debug output.

echo "=== let expression ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let x = 1 + 2 in x"}' | jq

echo ""
echo "=== query (type-checks + evaluates against real index) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "query `FROM piescript-test`"}' | jq

echo ""
echo "=== query with pipe ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "query `FROM piescript-test | WHERE age > 25 | LIMIT 5`"}' | jq

echo ""
echo "=== let-bound query ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in docs"}' | jq

echo ""
echo "=== bare builtin (map) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "map"}' | jq

echo ""
echo "=== partial application (map with identity fn) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "map (fn x -> x)"}' | jq

echo ""
echo "=== map over query stream ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in map (fn r -> r.name) docs"}' | jq

echo ""
echo "=== filter over query stream ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in filter (fn r -> r.active) docs"}' | jq

echo ""
echo "=== reduce over query stream ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in reduce (fn acc x -> acc + x.age) 0 docs"}' | jq

echo ""
echo "=== map with pipe syntax ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "query `FROM piescript-test` |> map (fn r -> r.name)"}' | jq

echo ""
echo "=== spawn a pure value ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let ch = spawn 42 in when (ch x) -> x + 1"}' | jq

echo ""
echo "=== spawn a computation ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let ch = spawn (1 + 2 + 3) in when (ch sum) -> sum * 10"}' | jq

echo ""
echo "=== spawn a query ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let ch = spawn (query `FROM piescript-test`) in when (ch docs) -> map (fn r -> r.name) docs"}' | jq

echo ""
echo "=== multi-channel when (two spawned queries) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let a = spawn (query `FROM piescript-test | WHERE active == true`) in let b = spawn (query `FROM piescript-test | WHERE active == false`) in when (a active) & (b inactive) -> { active: map (fn r -> r.name) active, inactive: map (fn r -> r.name) inactive }"}' | jq

echo ""
echo "=== type error ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "1 + true"}' | jq

echo ""
echo "=== parse error ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let = in"}' | jq

echo ""
echo "=== spawn! (bare channel) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let ch = spawn! in let u = send ch 42 in when (ch x) -> x + 1"}' | jq

echo ""
echo "=== cluster topology ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "topology \"cluster\""}' | jq

echo ""
echo "=== index routing ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "routing \"piescript-test\""}' | jq

echo ""
echo "=== send closure to local inbox via topology.local ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let topo = topology \"cluster\" in let ch = spawn! in let u = send topo.local.inbox (fn info -> send ch info.id) in when (ch result) -> result"}' | jq
