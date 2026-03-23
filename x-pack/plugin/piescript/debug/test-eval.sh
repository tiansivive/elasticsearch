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

echo ""
echo "=== Block E: Index.bulk (high-level write) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "let ch = Index.bulk \"piescript-bulk-debug\" [{ name: \"debug-alice\", score: 95 }, { name: \"debug-bob\", score: 87 }]; when (ch result) -> result"}' | jq

echo ""
echo "=== Block E: Shard.writer + Shard.write + Shard.refresh (shard-level write) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let shards = Index.shards idx; let shard = List.head shards; let wch = Shard.writer idx shard; when (wch writer) -> let r = Shard.write writer \"piescript-write-1\" { message: \"written-by-piescript\", status: 200 }; let rch = Shard.refresh writer; when (rch ack) -> { write_result: r, refreshed: ack.refreshed }"}' | jq

echo ""
echo "=== Block E: Shard.globalCheckpoint ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let shards = Index.shards idx; let shard = List.head shards; Shard.globalCheckpoint idx shard"}' | jq

echo ""
echo "=== Block E: Shard.write with _id (idempotent) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let shards = Index.shards idx; let shard = List.head shards; let wch = Shard.writer idx shard; when (wch writer) -> Shard.write writer \"piescript-idempotent-1\" { message: \"idempotent-write\", status: 42 }"}' | jq

echo ""
echo "=== Block E: Negative: WriterVal not serializable (expect error) ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let shards = Index.shards idx; let shard = List.head shards; let wch = Shard.writer idx shard; when (wch writer) -> writer"}' | jq

echo ""
echo "=== Block F: ESQL.from + ESQL.where + ESQL.limit ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; query ESQL.from idx |> ESQL.where (fn r -> r.status > 400) |> ESQL.limit 10;"}' | jq

echo ""
echo "=== Block F: ESQL.from + ESQL.keep ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; query ESQL.from idx |> ESQL.keep [\"message\"] |> ESQL.limit 5;"}' | jq

echo ""
echo "=== Block F: ESQL.explain ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.where (fn r -> r.status == 200) |> ESQL.limit 3)"}' | jq

echo ""
echo "=== Block F: ESQL.where with captured variable ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; let threshold = 400; query ESQL.from idx |> ESQL.where (fn r -> r.status > threshold) |> ESQL.limit 10;"}' | jq

echo ""
echo "=== Block F: ESQL.sort ==="
curl -s -u elastic-admin:elastic-password -X POST 'localhost:9200/_piescript/eval' \
  -H 'Content-Type: application/json' \
  -d '{"program": "use \"piescript-test\" as idx; query ESQL.from idx |> ESQL.sort (fn r -> r.status) |> ESQL.limit 10;"}' | jq
