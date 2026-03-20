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
echo "=== query ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "query `FROM piescript-test`"}' | jq

echo ""
echo "=== bare builtin (returns partially applied BuiltinVal) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "List.map"}' | jq

echo ""
echo "=== partial application (map with fn) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "List.map (fn x -> x)"}' | jq

echo ""
echo "=== map over query ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in List.map (fn r -> r.name) docs"}' | jq

echo ""
echo "=== filter over query ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in List.filter (fn r -> r.active) docs"}' | jq

echo ""
echo "=== spawn + when (pure) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let ch = spawn 42 in when (ch x) -> x + 1"}' | jq

echo ""
echo "=== spawn + when (query) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let ch = spawn (query `FROM piescript-test`) in when (ch docs) -> List.map (fn r -> r.name) docs"}' | jq

echo ""
echo "=== multi-channel when (concurrent queries) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let a = spawn (query `FROM piescript-test | WHERE active == true`) in let b = spawn (query `FROM piescript-test | WHERE active == false`) in when (a active) & (b inactive) -> { active: List.map (fn r -> r.name) active, inactive: List.map (fn r -> r.name) inactive }"}' | jq

echo ""
echo "=== spawn! + send + when (bare channel) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let ch = spawn! in let u = send ch 42 in when (ch x) -> x + 1"}' | jq

echo ""
echo "=== cluster topology (local node + all nodes) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "Cluster.topology \"cluster\""}' | jq

echo ""
echo "=== index routing (shards + nodes) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; Index.routing idx"}' | jq

echo ""
echo "=== shards convenience ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; Index.shards idx"}' | jq

echo ""
echo "=== nodes convenience ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; Index.nodes idx"}' | jq

echo ""
echo "=== send closure to local inbox via topology.local ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let topo = Cluster.topology \"cluster\" in let ch = spawn! in let u = send topo.local.inbox (fn info -> send ch info.id) in when (ch result) -> result"}' | jq

echo ""
echo "=== use declaration (Index r type) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; idx"}' | jq

echo ""
echo "=== Shard.open + Shard.consume + Shard.read (full local-data pipeline) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let shards = Index.shards idx; let shard = List.head shards; let ch = Shard.open idx shard { match_all: true }; when (ch searcher) -> let docs = Shard.consume 10.0 searcher; List.map (fn ref -> Shard.read ref) docs"}' | jq

echo ""
echo "=== Shard.consume exhausted (second consume returns empty) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let shards = Index.shards idx; let shard = List.head shards; let ch = Shard.open idx shard { match_all: true }; when (ch searcher) -> let first = Shard.consume 100.0 searcher; let second = Shard.consume 100.0 searcher; { first_count: List.length first, second_count: List.length second }"}' | jq

echo ""
echo "=== Negative: SearcherVal not serializable in response (expect error) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let shards = Index.shards idx; let shard = List.head shards; let ch = Shard.open idx shard { match_all: true }; when (ch searcher) -> searcher"}' | jq
