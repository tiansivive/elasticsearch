# Multi-node Cross-Node Execution Tests (Kibana Dev Tools)

This document provides a step-by-step conversion of the `test-multinode.sh` debug script into Kibana Dev Tools Console requests. It includes the initial setup and all test cases as individual requests. You can copy-paste each block into the Kibana Dev Tools Console for execution.

---

## Initial Setup

**1. Create the test index (if not already created):**

```json
PUT piescript-test
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 0
  },
  "mappings": {
    "properties": {
      "name": { "type": "keyword" },
      "age": { "type": "integer" },
      "score": { "type": "float" },
      "active": { "type": "boolean" }
    }
  }
}
```

**2. Insert sample documents:**

```json
POST piescript-test/_bulk
{ "index": {} }
{ "name": "alice", "age": 30, "score": 88.5, "active": true }
{ "index": {} }
{ "name": "bob", "age": 40, "score": 92.0, "active": false }
{ "index": {} }
{ "name": "carol", "age": 25, "score": 77.0, "active": true }
{ "index": {} }
{ "name": "dave", "age": 35, "score": 85.0, "active": true }
{ "index": {} }
{ "name": "eve", "age": 50, "score": 99.0, "active": false }
```

---

## Multi-node Cross-Execution Tests

All requests below use the Piescript eval API. Replace the endpoint as needed if your cluster is not running locally.

---

### 1. Cluster topology (expect 3 nodes)
```json
POST _piescript/eval
{
  "program": "let topo = Cluster.topology \"cluster\" in { local_id: topo.local.id, local_name: topo.local.name, node_count: List.length topo.nodes }"
}
```

### 2. All node names
```json
POST _piescript/eval
{
  "program": "let topo = Cluster.topology \"cluster\" in List.map (fn n -> n.name) topo.nodes"
}
```

### 3. Send to local inbox (echo back node id)
```json
POST _piescript/eval
{
  "program": "let topo = Cluster.topology \"cluster\" in let ch = spawn! in let u = send topo.local.inbox (fn info -> send ch info.id) in when (ch result) -> result"
}
```

### 4. Send to remote node inbox (echo back remote node id)
```json
POST _piescript/eval
{
  "program": "let topo = Cluster.topology \"cluster\" in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch info.id) in when (ch result) -> { local: topo.local.id, remote_ran_on: result }"
}
```

### 5. Fan-out: send closure to each remote node
```json
POST _piescript/eval
{
  "program": "let topo = Cluster.topology \"cluster\" in let remotes = List.filter (fn n -> n.id != topo.local.id) topo.nodes in let ch = spawn! in let u = List.map (fn n -> send n.inbox (fn info -> send ch info.name)) remotes in let results = List.map (fn _ -> when (ch name) -> name) remotes in results"
}
```

### 6. Remote computation (1 + 2 + 3 on remote node)
```json
POST _piescript/eval
{
  "program": "let topo = Cluster.topology \"cluster\" in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch (1 + 2 + 3)) in when (ch result) -> { computed_on: remote.name, result: result }"
}
```

### 7. Round-trip: send value, transform remotely, return
```json
POST _piescript/eval
{
  "program": "let topo = Cluster.topology \"cluster\" in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch { node: info.name, answer: 21 * 2 }) in when (ch result) -> result"
}
```

### 8. Prove remote execution (local id != remote execution id)
```json
POST _piescript/eval
{
  "program": "let topo = Cluster.topology \"cluster\" in let remote = List.head (List.filter (fn n -> n.id != topo.local.id) topo.nodes) in let ch = spawn! in let u = send remote.inbox (fn info -> send ch info.id) in when (ch remote_id) -> { local: topo.local.id, remote: remote_id, same_node: topo.local.id == remote_id }"
}
```

### 9. Triangle: A orchestrates B↔C direct communication
```json
POST _piescript/eval
{
  "program": "let topo = Cluster.topology \"cluster\"\nin let remotes = List.filter (fn n -> n.id != topo.local.id) topo.nodes\nin let nodeB = List.head remotes\nin let nodeC = List.head (List.tail remotes)\n\nin let ackB = spawn!\nin let ackC = spawn!\nin let refsFromB = spawn!\nin let refsFromC = spawn!\n\nin let makeAgent = fn refsBack ack label -> fn info ->\n  let myCh = spawn!\n  in let peerCh = spawn!\n  in let u = send refsBack { myCh: myCh, peerCh: peerCh }\n  in when (peerCh peer) ->\n    let u2 = send peer { from: info.name, role: label }\n    in when (myCh msg) ->\n      send ack { node: info.name, role: label,\n                 peer_node: msg.from, peer_role: msg.role }\n\nin let u1 = send nodeB.inbox (makeAgent refsFromB ackB \"agent-B\")\nin let u3 = send nodeC.inbox (makeAgent refsFromC ackC \"agent-C\")\n\nin when (refsFromB bRefs) & (refsFromC cRefs) ->\n  let u5 = send bRefs.peerCh cRefs.myCh\n  in let u6 = send cRefs.peerCh bRefs.myCh\n  in when (ackB rb) & (ackC rc) ->\n    { b_ran_on: rb.node, b_role: rb.role,\n      b_heard_from: rb.peer_node, b_peer_role: rb.peer_role,\n      c_ran_on: rc.node, c_role: rc.role,\n      c_heard_from: rc.peer_node, c_peer_role: rc.peer_role }"
}
```

### 10. use declaration + Index.shards
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; Index.shards idx"
}
```

### 11. Shard.open fan-out to ALL shards (tagged with node info)
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nlet shards = Index.shards idx\nin let s0 = List.at 0 shards\nin let s1 = List.at 1 shards\nin let s2 = List.at 2 shards\n\nin let ch0 = spawn!\nin let ch1 = spawn!\nin let ch2 = spawn!\n\nin let u0 = send s0.node.inbox (fn info ->\n  let dc = Shard.open idx s0 { match_all: true }\n  in when (dc searcher) ->\n    let docs = Shard.consume 100.0 searcher\n    in send ch0 { node: info.name, shard_id: s0.shard_id,\n                   rows: List.map (fn ref -> Shard.read ref) docs }\n)\nin let u1 = send s1.node.inbox (fn info ->\n  let dc = Shard.open idx s1 { match_all: true }\n  in when (dc searcher) ->\n    let docs = Shard.consume 100.0 searcher\n    in send ch1 { node: info.name, shard_id: s1.shard_id,\n                   rows: List.map (fn ref -> Shard.read ref) docs }\n)\nin let u2 = send s2.node.inbox (fn info ->\n  let dc = Shard.open idx s2 { match_all: true }\n  in when (dc searcher) ->\n    let docs = Shard.consume 100.0 searcher\n    in send ch2 { node: info.name, shard_id: s2.shard_id,\n                   rows: List.map (fn ref -> Shard.read ref) docs }\n)\nin when (ch0 r0) & (ch1 r1) & (ch2 r2) ->\n  { shard_0: r0, shard_1: r1, shard_2: r2 }"
}
```

### 12. Remote Shard.writer + Shard.write (primary write on data node)
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nlet shards = Index.shards idx\nin let primary = List.head (List.filter (fn s -> s.primary) shards)\nin let ch = spawn!\nin let u = send primary.node.inbox (fn info ->\n  let wch = Shard.writer idx primary\n  in when (wch writer) ->\n    let r = Shard.write writer \"remote-written-1\" { name: \"remote-written\", age: 99, score: 7.77, active: true }\n    in send ch { node: info.name, seq_no: r.seq_no, version: r.version, result: r.result }\n)\nin when (ch result) -> result"
}
```

### 13. Index.bulk (high-level Bulk API write)
```json
POST _piescript/eval
{
  "program": "let ch = Index.bulk \"piescript-mn-bulk\" [{ name: \"mn-alice\", score: 90 }, { name: \"mn-bob\", score: 80 }]; when (ch result) -> result"
}
```

### 14. Shard.globalCheckpoint
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nlet shards = Index.shards idx\nin let shard = List.head shards\nin let ch = spawn!\nin let u = send shard.node.inbox (fn info ->\n  send ch { node: info.name, checkpoint: Shard.globalCheckpoint idx shard }\n)\nin when (ch result) -> result"
}
```

### 15. ESQL.from + ESQL.where + ESQL.limit
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; query ESQL.from idx |> ESQL.where (fn r -> r.active == true) |> ESQL.limit 10;"
}
```

### 16. ESQL.explain (compiled ESQL string)
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.where (fn r -> r.age > 30) |> ESQL.keep (fn r -> { name: r.name, age: r.age }) |> ESQL.limit 5)"
}
```

### 17. ESQL.where with captured variable
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; let threshold = 25; query ESQL.from idx |> ESQL.where (fn r -> r.age > threshold) |> ESQL.limit 10;"
}
```

### 18. ESQL.sort ascending
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; query ESQL.from idx |> ESQL.sort (fn r -> r.age) |> ESQL.limit 10;"
}
```

### 19. ESQL.keep with closure syntax
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.keep (fn r -> { name: r.name, age: r.age }))"
}
```

### 20. ESQL.drop with closure syntax
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.drop (fn r -> { score: r.score }))"
}
```

### 21. ESQL.stats — global count
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.stats (fn r -> { count: ESQL.count \"*\" }))"
}
```

### 22. ESQL.statsBy — count + avg grouped by active
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; ESQL.explain (ESQL.from idx |> ESQL.statsBy (fn r -> { count: ESQL.count \"*\", avg_age: ESQL.avg (fn r2 -> r2.age) }) (fn r -> { active: r.active }))"
}
```

### 23. ESQL.stats — end-to-end query execution
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx; query ESQL.from idx |> ESQL.stats (fn r -> { count: ESQL.count \"*\" });"
}
```

### 24. Shard.stream + Page.count — batch docs into a Page
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nlet shards = Index.shards idx\nin let shard = List.head shards\nin let node = shard.node\nin let result_ch = spawn!\nin let u = send node.inbox (fn info ->\n  let data_ch = Shard.open idx shard { match_all: true }\n  in when (data_ch searcher) ->\n    let docs = Shard.consume 10 searcher\n    in let page = Shard.stream searcher docs\n    in send result_ch (Page.count page))\nin when (result_ch count) -> count"
}
```

### 25. Shard.stream + Page.toList — materialize Page to records
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nlet shards = Index.shards idx\nin let shard = List.head shards\nin let node = shard.node\nin let result_ch = spawn!\nin let u = send node.inbox (fn info ->\n  let data_ch = Shard.open idx shard { match_all: true }\n  in when (data_ch searcher) ->\n    let docs = Shard.consume 10 searcher\n    in let page = Shard.stream searcher docs\n    in send result_ch (Page.toList page))\nin when (result_ch rows) -> rows"
}
```

### 26. Shard.stream + Page.toList — compare with Shard.read
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nlet shards = Index.shards idx\nin let shard = List.head shards\nin let node = shard.node\nin let result_ch = spawn!\nin let u = send node.inbox (fn info ->\n  let data_ch = Shard.open idx shard { match_all: true }\n  in when (data_ch searcher) ->\n    let docs = Shard.consume 10 searcher\n    in let page = Shard.stream searcher docs\n    in let page_rows = Page.toList page\n    in let read_rows = List.map (fn d -> Shard.read d) docs\n    in send result_ch { page_count: List.length page_rows, read_count: List.length read_rows })\nin when (result_ch counts) -> counts"
}
```

### 27. Exchange.open + Exchange.sink + Exchange.connect + addPage + poll
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nlet shards = Index.shards idx\nin let shard = List.head shards\nin let node = shard.node\nin let result_ch = spawn!\nin let u = send node.inbox (fn info ->\n  let data_ch = Shard.open idx shard { match_all: true }\n  in when (data_ch searcher) ->\n    let docs = Shard.consume 10 searcher\n    in let page = Shard.stream searcher docs\n    in let ex = Exchange.open [\"name\", \"age\", \"score\", \"active\"] 8\n    in let snk = Exchange.sink ex\n    in let src = Exchange.connect ex\n    in let u1 = Exchange.addPage snk page\n    in let u2 = Exchange.finish snk\n    in let collect_ch = spawn!\n    in let poll_ch = Exchange.poll src (fn p -> send collect_ch (Page.count p))\n    in when (poll_ch done) & (collect_ch count) ->\n      send result_ch count)\nin when (result_ch count) -> count"
}
```

### 28. ESQL.statsBy with ESQL.top — MV aggregate returns List
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nquery ESQL.from idx\n  |> ESQL.statsBy\n       (fn r -> { top_ages: ESQL.top r.age 3 \"desc\" })\n       (fn r -> { active: r.active })\n  |> ESQL.limit 10;"
}
```

### 29. ESQL.values — unique values as List
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nquery ESQL.from idx\n  |> ESQL.stats (fn r -> { names: ESQL.values r.name });"
}
```

### 30. ESQL.top + List.reduce — user-defined aggregate over MV result
```json
POST _piescript/eval
{
  "program": "use \"piescript-test\" as idx;\nlet raw = query ESQL.from idx\n  |> ESQL.statsBy\n       (fn r -> { top_scores: ESQL.top r.score 5 \"desc\" })\n       (fn r -> { active: r.active });\nin List.map (fn row -> {\n  active: row.active,\n  top_scores: row.top_scores,\n  total: List.reduce (fn acc v -> acc + v) 0 row.top_scores\n}) raw"
}
```

---

**Done!**

You can now run each block in Kibana Dev Tools to perform the same tests as the original multinode debug script.