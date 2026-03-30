#!/usr/bin/env bash
# Multi-node cross-node execution tests.
# Requires: 3-node cluster with piescript-test index created.
#
# Works with security enabled or disabled:
#   Security off:  ./gradlew run -Dtests.es.xpack.security.enabled=false -Drun.license_type=trial -I ../scripts/es-dev-config/multinode.gradle
#   Security on:   ./gradlew run -Drun.license_type=trial -I ../scripts/es-dev-config/multinode.gradle
#
# Then:
#   bash x-pack/plugin/piescript/debug/setup-test-index-multinode.sh   # (no-auth setup)
#   -- or --
#   bash x-pack/plugin/piescript/debug/setup-kibana-multinode.sh       # (auth setup + Kibana)
#
#   bash x-pack/plugin/piescript/debug/test-multinode.sh

BASE="localhost:9200"
EVAL="$BASE/_piescript/eval"
CT='Content-Type: application/json'

# Auto-detect security: try unauthenticated request, fall back to auth
if curl -s -o /dev/null -w '%{http_code}' "$BASE" 2>/dev/null | grep -q '^200$'; then
  AUTH=""
else
  AUTH="-u test_user:x-pack-test-password"
fi

post() { curl -s $AUTH -X POST "$EVAL" -H "$CT" -d "$1" | jq; }

echo "========================================"
echo "  Multi-Node Cross-Execution Tests"
echo "========================================"

# ── 1. Cluster topology — verify we see multiple nodes ──
echo ""
echo "=== 1. Cluster topology (expect 3 nodes) ==="
post '{"program": "let topo = Cluster.topology \"cluster\" in { local_id: topo.local.id, local_name: topo.local.name, node_count: List.length topo.nodes }"}'

# ── 2. List all node names ──
echo ""
echo "=== 2. All node names ==="
post '{"program": "let topo = Cluster.topology \"cluster\" in List.map (fn n -> n.name) topo.nodes"}'

# ── 3. Send closure to local inbox — basic smoke test ──
echo ""
echo "=== 3. Send to local inbox (echo back node id) ==="
post '{"program": "let topo = Cluster.topology \"cluster\" in let ch = spawn! in let u = send topo.local.inbox (fn info -> send ch info.id) in when (ch result) -> result"}'

# ── 4. Send closure to a remote node's inbox ──
# Pick the second node in the list (likely different from local).
echo ""
echo "=== 4. Send to remote node inbox (echo back remote node id) ==="
post '{"program": "let topo = Cluster.topology \"cluster\" in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch info.id) in when (ch result) -> { local: topo.local.id, remote_ran_on: result }"}'

# ── 5. Send closure to ALL remote nodes (fan-out) ──
# Each remote node sends back its name; we collect results.
echo ""
echo "=== 5. Fan-out: send closure to each remote node ==="
.
# ── 6. Remote computation — send arithmetic to a remote node ──
echo ""
echo "=== 6. Remote computation (1 + 2 + 3 on remote node) ==="
post '{"program": "let topo = Cluster.topology \"cluster\" in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch (1 + 2 + 3)) in when (ch result) -> { computed_on: remote.name, result: result }"}'

# ── 7. Round-trip: send data to remote, transform it there, get it back ──
echo ""
echo "=== 7. Round-trip: send value, transform remotely, return ==="
post '{"program": "let topo = Cluster.topology \"cluster\" in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch { node: info.name, answer: 21 * 2 }) in when (ch result) -> result"}'

# ── 8. Verify local vs remote — prove code ran on different node ──
echo ""
echo "=== 8. Prove remote execution (local id != remote execution id) ==="
post '{"program": "let topo = Cluster.topology \"cluster\" in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch info.id) in when (ch remote_id) -> { local: topo.local.id, remote: remote_id, same_node: topo.local.id == remote_id }"}'

# ── 9. Triangle coordination: A orchestrates B↔C direct communication ──
# A sends identical closures (abstracted via let-binding) to B and C.
# Each creates local channels, sends refs back to A.
# A cross-forwards the refs so B and C can message each other directly.
# B and C ack back to A after receiving each other's message.
# Exercises: name-passing (channels-in-channels), closure capture, multi-phase
# coordination, direct B↔C communication, higher-order function abstraction.
echo ""
echo "=== 9. Triangle: A orchestrates B↔C direct communication ==="
PROG9='let topo = Cluster.topology "cluster"
in let remotes = List.filter (fn n -> n.id != topo.local.id) topo.nodes
in let nodeB = List.head remotes
in let nodeC = List.head (List.tail remotes)

in let ackB = spawn!
in let ackC = spawn!
in let refsFromB = spawn!
in let refsFromC = spawn!

in let makeAgent = fn refsBack ack label -> fn info ->
  let myCh = spawn!
  in let peerCh = spawn!
  in let u = send refsBack { myCh: myCh, peerCh: peerCh }
  in when (peerCh peer) ->
    let u2 = send peer { from: info.name, role: label }
    in when (myCh msg) ->
      send ack { node: info.name, role: label,
                 peer_node: msg.from, peer_role: msg.role }

in let u1 = send nodeB.inbox (makeAgent refsFromB ackB "agent-B")
in let u3 = send nodeC.inbox (makeAgent refsFromC ackC "agent-C")

in when (refsFromB bRefs) & (refsFromC cRefs) ->
  let u5 = send bRefs.peerCh cRefs.myCh
  in let u6 = send cRefs.peerCh bRefs.myCh
  in when (ackB rb) & (ackC rc) ->
    { b_ran_on: rb.node, b_role: rb.role,
      b_heard_from: rb.peer_node, b_peer_role: rb.peer_role,
      c_ran_on: rc.node, c_role: rc.role,
      c_heard_from: rc.peer_node, c_peer_role: rc.peer_role }'
post "$(jq -n --arg p "$PROG9" '{"program": $p}')"

# ── 10. Block D: use declaration + local shard data access ──
echo ""
echo "=== 10. use declaration + Index.shards ==="
post '{"program": "use \"piescript-test\" as idx; Index.shards idx"}'

echo ""
echo "=== 11. Shard.open fan-out to ALL shards (tagged with node info) ==="
PROG11='use "piescript-test" as idx;
let shards = Index.shards idx
in let s0 = List.at 0 shards
in let s1 = List.at 1 shards
in let s2 = List.at 2 shards

in let ch0 = spawn!
in let ch1 = spawn!
in let ch2 = spawn!

in let u0 = send s0.node.inbox (fn info ->
  let dc = Shard.open idx s0 { match_all: true }
  in when (dc searcher) ->
    let docs = Shard.consume 100.0 searcher
    in send ch0 { node: info.name, shard_id: s0.shard_id,
                   rows: List.map (fn ref -> Shard.read ref) docs }
)
in let u1 = send s1.node.inbox (fn info ->
  let dc = Shard.open idx s1 { match_all: true }
  in when (dc searcher) ->
    let docs = Shard.consume 100.0 searcher
    in send ch1 { node: info.name, shard_id: s1.shard_id,
                   rows: List.map (fn ref -> Shard.read ref) docs }
)
in let u2 = send s2.node.inbox (fn info ->
  let dc = Shard.open idx s2 { match_all: true }
  in when (dc searcher) ->
    let docs = Shard.consume 100.0 searcher
    in send ch2 { node: info.name, shard_id: s2.shard_id,
                   rows: List.map (fn ref -> Shard.read ref) docs }
)
in when (ch0 r0) & (ch1 r1) & (ch2 r2) ->
  { shard_0: r0, shard_1: r1, shard_2: r2 }'
post "$(jq -n --arg p "$PROG11" '{"program": $p}')"

# ── 12. Block E: Remote shard write via shipped closure ──
echo ""
echo "=== 12. Remote Shard.writer + Shard.write (primary write on data node) ==="
PROG12='use "piescript-test" as idx;
let shards = Index.shards idx
in let primary = List.head (List.filter (fn s -> s.primary) shards)
in let ch = spawn!
in let u = send primary.node.inbox (fn info ->
  let wch = Shard.writer idx primary
  in when (wch writer) ->
    let r = Shard.write writer "remote-written-1" { name: "remote-written", age: 99, score: 7.77, active: true }
    in send ch { node: info.name, seq_no: r.seq_no, version: r.version, result: r.result }
)
in when (ch result) -> result'
post "$(jq -n --arg p "$PROG12" '{"program": $p}')"

# ── 13. Block E: Index.bulk from coordinator ──
echo ""
echo "=== 13. Index.bulk (high-level Bulk API write) ==="
post '{"program": "let ch = Index.bulk \"piescript-mn-bulk\" [{ name: \"mn-alice\", score: 90 }, { name: \"mn-bob\", score: 80 }]; when (ch result) -> result"}'

# ── 14. Block E: Shard.globalCheckpoint (shipped to data node) ──
echo ""
echo "=== 14. Shard.globalCheckpoint ==="
PROG14='use "piescript-test" as idx;
let shards = Index.shards idx
in let shard = List.head shards
in let ch = spawn!
in let u = send shard.node.inbox (fn info ->
  send ch { node: info.name, checkpoint: Shard.globalCheckpoint idx shard }
)
in when (ch result) -> result'
post "$(jq -n --arg p "$PROG14" '{"program": $p}')"

# ── 15. Block F: ESQL query compilation ──
echo ""
echo "=== 15. ESQL.from + ESQL.where + ESQL.limit ==="
post '{"program": "use \"piescript-test\" as idx; query ESQL.from idx |> ESQL.where (fn r -> r.active == true) |> ESQL.limit 10;"}'

echo ""
echo "=== 16. ESQL.explain (compiled ESQL string) ==="
post '{"program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.where (fn r -> r.age > 30) |> ESQL.keep (fn r -> { name: r.name, age: r.age }) |> ESQL.limit 5)"}'

echo ""
echo "=== 17. ESQL.where with captured variable ==="
post '{"program": "use \"piescript-test\" as idx; let threshold = 25; query ESQL.from idx |> ESQL.where (fn r -> r.age > threshold) |> ESQL.limit 10;"}'

echo ""
echo "=== 18. ESQL.sort ascending ==="
post '{"program": "use \"piescript-test\" as idx; query ESQL.from idx |> ESQL.sort (fn r -> r.age) |> ESQL.limit 10;"}'

echo ""
echo "=== 19. ESQL.keep with closure syntax ==="
post '{"program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.keep (fn r -> { name: r.name, age: r.age }))"}'

echo ""
echo "=== 20. ESQL.drop with closure syntax ==="
post '{"program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.drop (fn r -> { score: r.score }))"}'

echo ""
echo "=== 21. ESQL.stats — global count ==="
post '{"program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.stats (fn r -> { count: ESQL.count \"*\" }))"}'

echo ""
echo "=== 22. ESQL.statsBy — count + avg grouped by active ==="
post '{"program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.statsBy (fn r -> { count: ESQL.count \"*\", avg_age: ESQL.avg (fn r2 -> r2.age) }) (fn r -> { active: r.active }))"}'

echo ""
echo "=== 23. ESQL.stats — end-to-end query execution ==="
post '{"program": "use \"piescript-test\" as idx; query ESQL.from idx |> ESQL.stats (fn r -> { count: ESQL.count \"*\" });"}'

# ──────────────────────────────────────────
# Block G: Streaming data access
# ──────────────────────────────────────────

echo ""
echo "=== 24. Shard.stream + Page.count — batch docs into a Page ==="
PROG24='use "piescript-test" as idx;
let shards = Index.shards idx
in let shard = List.head shards
in let node = shard.node
in let result_ch = spawn!
in let u = send node.inbox (fn info ->
  let data_ch = Shard.open idx shard { match_all: true }
  in when (data_ch searcher) ->
    let docs = Shard.consume 10 searcher
    in let page = Shard.stream searcher docs
    in send result_ch (Page.count page))
in when (result_ch count) -> count'
post "{\"program\": $(echo "$PROG24" | jq -Rs .)}"

echo ""
echo "=== 25. Shard.stream + Page.toList — materialize Page to records ==="
PROG25='use "piescript-test" as idx;
let shards = Index.shards idx
in let shard = List.head shards
in let node = shard.node
in let result_ch = spawn!
in let u = send node.inbox (fn info ->
  let data_ch = Shard.open idx shard { match_all: true }
  in when (data_ch searcher) ->
    let docs = Shard.consume 10 searcher
    in let page = Shard.stream searcher docs
    in send result_ch (Page.toList page))
in when (result_ch rows) -> rows'
post "{\"program\": $(echo "$PROG25" | jq -Rs .)}"

echo ""
echo "=== 26. Shard.stream + Page.toList — compare with Shard.read ==="
PROG26='use "piescript-test" as idx;
let shards = Index.shards idx
in let shard = List.head shards
in let node = shard.node
in let result_ch = spawn!
in let u = send node.inbox (fn info ->
  let data_ch = Shard.open idx shard { match_all: true }
  in when (data_ch searcher) ->
    let docs = Shard.consume 10 searcher
    in let page = Shard.stream searcher docs
    in let page_rows = Page.toList page
    in let read_rows = List.map (fn d -> Shard.read d) docs
    in send result_ch { page_count: List.length page_rows, read_count: List.length read_rows })
in when (result_ch counts) -> counts'
post "{\"program\": $(echo "$PROG26" | jq -Rs .)}"

echo ""
echo "=== 27. Exchange.open + Exchange.sink + Exchange.connect + addPage + poll ==="
PROG27='use "piescript-test" as idx;
let shards = Index.shards idx
in let shard = List.head shards
in let node = shard.node
in let result_ch = spawn!
in let u = send node.inbox (fn info ->
  let data_ch = Shard.open idx shard { match_all: true }
  in when (data_ch searcher) ->
    let docs = Shard.consume 10 searcher
    in let page = Shard.stream searcher docs
    in let ex = Exchange.open ["name", "age", "score", "active"] 8
    in let snk = Exchange.sink ex
    in let src = Exchange.connect ex
    in let u1 = Exchange.addPage snk page
    in let u2 = Exchange.finish snk
    in let collect_ch = spawn!
    in let poll_ch = Exchange.poll src (fn p -> send collect_ch (Page.count p))
    in when (poll_ch done) & (collect_ch count) ->
      send result_ch count)
in when (result_ch count) -> count'
post "{\"program\": $(echo "$PROG27" | jq -Rs .)}"

echo ""
echo "=== 28. ESQL.statsBy with ESQL.top — MV aggregate returns List ==="
PROG28='use "piescript-test" as idx;
query ESQL.from idx
  |> ESQL.statsBy
       (fn r -> { top_ages: ESQL.top r.age 3 "desc" })
       (fn r -> { active: r.active })
  |> ESQL.limit 10;'
post "{\"program\": $(echo "$PROG28" | jq -Rs .)}"

echo ""
echo "=== 29. ESQL.values — unique values as List ==="
PROG29='use "piescript-test" as idx;
query ESQL.from idx
  |> ESQL.stats (fn r -> { names: ESQL.values r.name });'
post "{\"program\": $(echo "$PROG29" | jq -Rs .)}"

echo ""
echo "=== 30. ESQL.top + List.reduce — user-defined aggregate over MV result ==="
PROG30='use "piescript-test" as idx;
let raw = query ESQL.from idx
  |> ESQL.statsBy
       (fn r -> { top_scores: ESQL.top r.score 5 "desc" })
       (fn r -> { active: r.active });
in List.map (fn row -> {
  active: row.active,
  top_scores: row.top_scores,
  total: List.reduce (fn acc v -> acc + v) 0 row.top_scores
}) raw'
post "{\"program\": $(echo "$PROG30" | jq -Rs .)}"

echo ""
echo "========================================"
echo "  Done"
echo "========================================"
