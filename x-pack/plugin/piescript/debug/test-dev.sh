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
  -d '{"program": "List.map"}' | jq

echo ""
echo "=== partial application (map with identity fn) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "List.map (fn x -> x)"}' | jq

echo ""
echo "=== map over query stream ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in List.map (fn r -> r.name) docs"}' | jq

echo ""
echo "=== filter over query stream ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in List.filter (fn r -> r.active) docs"}' | jq

echo ""
echo "=== reduce over query stream ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let docs = query `FROM piescript-test` in List.reduce (fn acc x -> acc + x.age) 0 docs"}' | jq

echo ""
echo "=== map with pipe syntax ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "query `FROM piescript-test` |> List.map (fn r -> r.name)"}' | jq

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
  -d '{"program": "let ch = spawn (query `FROM piescript-test`) in when (ch docs) -> List.map (fn r -> r.name) docs"}' | jq

echo ""
echo "=== multi-channel when (two spawned queries) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let a = spawn (query `FROM piescript-test | WHERE active == true`) in let b = spawn (query `FROM piescript-test | WHERE active == false`) in when (a active) & (b inactive) -> { active: List.map (fn r -> r.name) active, inactive: List.map (fn r -> r.name) inactive }"}' | jq

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
  -d '{"program": "Cluster.topology \"cluster\""}' | jq

echo ""
echo "=== index routing ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; Index.routing idx"}' | jq

echo ""
echo "=== send closure to local inbox via topology.local ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let topo = Cluster.topology \"cluster\" in let ch = spawn! in let u = send topo.local.inbox (fn info -> send ch info.id) in when (ch result) -> result"}' | jq

echo ""
echo "=== use declaration (debug view) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; idx"}' | jq

echo ""
echo "=== Shard.open + Shard.consume + Shard.read pipeline ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let shards = Index.shards idx; let shard = List.head shards; let ch = Shard.open idx shard { match_all: true }; when (ch searcher) -> let docs = Shard.consume 10.0 searcher; List.map (fn ref -> Shard.read ref) docs"}' | jq

echo ""
echo "=== Shard.consume exhausted ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let shards = Index.shards idx; let shard = List.head shards; let ch = Shard.open idx shard { match_all: true }; when (ch searcher) -> let first = Shard.consume 100.0 searcher; let second = Shard.consume 100.0 searcher; { first_count: List.length first, second_count: List.length second }"}' | jq

echo ""
echo "=== pattern matching (match) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "match { a: 1, b: 2 } | { a: x } -> x"}' | jq

echo ""
echo "=== pattern matching (if/else sugar) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "if true then 42 else 0"}' | jq

echo ""
echo "=== recursion (factorial) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let f = fn x -> match x | 0 -> 1 | n -> n * f (n - 1) in f 5"}' | jq

echo ""
echo "=== recursion guard (expect type_error) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let x = x + 1 in x"}' | jq

echo ""
echo "=== loop/repeat (counter) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "loop 0 | 10 -> \"done\" | n -> repeat (n + 1)"}' | jq

echo ""
echo "=== loop/repeat (accumulator) ==="
curl -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/dev' \
  -H 'Content-Type: application/json' \
  -d '{"program": "loop { acc: 0, n: 5 } | { acc, n: 0 } -> acc | { acc, n } -> repeat { acc: acc + n, n: n - 1 }"}' | jq
