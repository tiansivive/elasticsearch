# MVP Examples: What Piescript Enables

> **Living doc** — add examples as new capabilities land. Each example should demonstrate
> expressiveness that is either impossible or significantly painful in ESQL alone.
>
> **Revised**: 2026-03-17. Added the distributed vertical slice target (Blocks B–D) and reframed
> the MVP scope around explicit distributed computing. See D-042.
>
> **Created**: 2026-03-16 (Block A complete)

## Guiding Principle

Piescript's value proposition is **not** reimplementing ESQL. ESQL is excellent at what it does —
declarative, Lucene-backed, vectorized query execution. Piescript adds what ESQL deliberately
omits: user-defined functions, typed multi-query coordination, structured composition, and —
crucially — **explicit control over distributed computation**. The user decides what code runs
where. Nodes, shards, and topology are first-class values. Libraries provide higher-level
abstractions when convenience is wanted.

The examples below are ordered by what works **today** versus what requires additional work. Each
identifies the specific ESQL limitation it addresses.

---

## Working Today (Block A)

These examples run end-to-end on the current implementation: parse → type-check → evaluate →
JSON response.

### 1. Concurrent Multi-Index Queries

**ESQL limitation**: one query per request. Combining data from multiple indices requires multiple
HTTP round-trips and client-side orchestration.

```
let a = spawn (query `FROM orders | WHERE @timestamp > now() - 1h`)
in let b = spawn (query `FROM customers`)
in when (a orders) & (b customers) ->
  { order_count: reduce 0 (fn acc _ -> acc + 1) orders,
    customer_count: reduce 0 (fn acc _ -> acc + 1) customers }
```

One request, one response. Both queries fire concurrently on the GENERIC thread pool. The `when`
body fires when both complete. The type checker verifies field compatibility across the entire
pipeline before anything runs.

**Value**: eliminates client-side orchestration for multi-index queries. Wall-clock time is
`max(query_a, query_b)` instead of `sum(query_a, query_b)`.

### 2. Query-Time Cross-Index Enrichment

**ESQL limitation**: ESQL's `ENRICH` command requires pre-configured enrich policies (create
policy → execute policy → reference in query). The enrich index is a stale snapshot, match types
are limited to `match`/`range`/`geo_match`, and there's no way to do arbitrary computation on
the enriched results within the same query.

```
let orders = spawn (query `FROM orders | LIMIT 100`)
in let customers = spawn (query `FROM customers`)
in when (orders o) & (customers c) ->
  o |> map (fn order ->
    let match = c
      |> filter (fn cust -> cust.id == order.customer_id)
      |> reduce { name: "unknown" } (fn _ cust -> { name: cust.name })
    in { order | customer_name: match.name }
  )
```

**Value**: live data (not a snapshot), no infrastructure setup, arbitrary matching predicates,
full functional composition after the join, type-checked across the entire pipeline.

**Caveat**: this is an O(n×m) nested-loop join — no hash table, no index lookup. Fine for
small-to-medium enrichment sets. For large-scale joins, ESQL's `ENRICH` (backed by a Lucene
index) is faster. Push-down compilation (Block D) and Exchange integration (Block E) address
this.

### 3. Structured Record Output (No JSON String Hacks)

**ESQL limitation**: ESQL outputs flat tabular data. When structured nested output is needed,
users resort to `CONCAT`-based JSON string construction with escaped quotes — fragile, untyped,
and difficult to maintain.

In piescript, records are first-class values. Building structured output is just building records:

```
let docs = query `FROM .alerts-security | WHERE kibana.alert.risk_score IS NOT NULL | LIMIT 100`
in docs |> map (fn alert -> {
  risk_score: alert.kibana.alert.risk_score,
  rule_name: alert.kibana.alert.rule.name,
  alert_id: alert.kibana.alert.uuid,
  category: alert.event.kind,
  time: alert.@timestamp
})
```

**Value**: typed, composable, properly serialized as JSON. No string concatenation, no escaped
quotes, no `TO_BASE64` workarounds.

### 4. Named Intermediate Results and Reuse

**ESQL limitation**: ESQL's pipe syntax is linear — you can't name intermediate results and
reuse them without re-querying.

```
let raw = query `FROM events`
in let errors = filter (fn r -> r.level == "ERROR") raw
in let warnings = filter (fn r -> r.level == "WARN") raw
in { error_count: reduce 0 (fn acc _ -> acc + 1) errors,
     warn_count: reduce 0 (fn acc _ -> acc + 1) warnings }
```

The same query result is used twice with different filters — no re-querying, no duplication.
Let-polymorphic bindings ensure type safety across all uses.

**Value**: eliminates redundant queries when the same dataset feeds multiple computations.

---

## Aspirational: Risk Score Calculation (Real-World Case Study)

This is an actual Elasticsearch Security query used for entity risk scoring. It demonstrates the
kind of complex, domain-specific computation that pushes ESQL to its limits — and where piescript
could provide significant value.

### The Current ESQL Query

```sql
FROM .alerts-security METADATA _index
| WHERE kibana.alert.risk_score IS NOT NULL AND KQL("@timestamp >= now-1h")
| RENAME kibana.alert.risk_score as risk_score,
         kibana.alert.rule.name as rule_name,
         kibana.alert.rule.uuid as rule_id,
         kibana.alert.uuid as alert_id,
         event.kind as category,
         @timestamp as time
| EVAL rule_name_b64 = TO_BASE64(rule_name),
       category_b64 = TO_BASE64(category)
| EVAL input = CONCAT(
    """ {"risk_score": """", risk_score::keyword, """",
    "time": """", time::keyword, """",
    "index": """", _index, """",
    "rule_name_b64": """", rule_name_b64, """\",
    "category_b64": """", category_b64, """\",
    "id": \"""", alert_id, """\" } """)
| STATS
    alert_count = count(risk_score),
    scores = MV_PSERIES_WEIGHTED_SUM(TOP(risk_score, 10000, "desc"), 1.5),
    risk_inputs = TOP(input, 10, "desc")
  BY user.name
| SORT scores DESC
| LIMIT 3000
```

### Pain Points

1. **`MV_PSERIES_WEIGHTED_SUM` had to be added to ESQL as a built-in** — a p-series weighted sum
   (`Σ values[i] / i^s`) is a general mathematical operation, but ESQL has no user-defined
   functions, so it required modifying the ESQL codebase. Every domain-specific aggregate needs a
   new built-in.

2. **The `CONCAT`-based JSON construction is a hack** — building JSON strings with escaped quotes
   and `TO_BASE64` workarounds because ESQL can't output structured nested data. This is fragile,
   untyped, and painful to maintain.

3. **The query is monolithic** — filtering, renaming, encoding, aggregating, and formatting are
   all fused into a single ESQL statement. There's no way to test, reuse, or compose individual
   parts.

### The Piescript Vision (Hybrid Approach)

Push what ESQL is efficient at (filtering, grouping, TOP-N via Lucene) into ESQL. Pull what ESQL
is bad at (user-defined aggregates, structured output) into piescript:

```
let pseries_weighted_sum = fn s values ->
  let state = reduce { sum: 0.0, i: 1 } (fn acc v ->
    { sum: acc.sum + v / pow acc.i s, i: acc.i + 1 }
  ) values
  in state.sum

in let raw = query `FROM .alerts-security METADATA _index
  | WHERE kibana.alert.risk_score IS NOT NULL
  | STATS alert_count = count(kibana.alert.risk_score),
          top_scores = TOP(kibana.alert.risk_score, 10000, "desc"),
          top_ids = TOP(kibana.alert.uuid, 10, "desc")
    BY user.name
  | SORT alert_count DESC
  | LIMIT 3000`

in raw |> map (fn user -> {
  user_name: user.user.name,
  alert_count: user.alert_count,
  score: pseries_weighted_sum 1.5 user.top_scores,
  risk_inputs: map (fn id -> { alert_id: id }) user.top_ids
})
```

### What This Demonstrates

- **`MV_PSERIES_WEIGHTED_SUM` becomes a let-binding** — a user-defined function, not an ESQL
  built-in. Any domain-specific aggregate can be expressed this way without modifying the ESQL
  codebase.
- **No JSON string hacks** — `risk_inputs` is a list of records, properly serialized as JSON.
  No `CONCAT`, no `TO_BASE64`, no escaped quotes.
- **Composable** — the scoring function, the query, and the output formatting are separate,
  testable, reusable pieces. `pseries_weighted_sum` can be used across different queries.
- **Type-safe** — the type checker verifies that `user.top_scores` is a stream of numbers, that
  `user.user.name` exists, etc.

### What's Missing to Make This Work

| Gap | What it blocks | Effort | Phase |
|-----|---------------|--------|-------|
| Double/float arithmetic | Weighted sum computation | Small — extend `EvalPrimOps` | Phase 1 tech debt (D-020) |
| `pow` function | Exponentiation for p-series | Small — new primop or prelude built-in | Phase 1 tech debt |
| Multi-value field handling | ESQL `TOP` returns multi-value arrays | Medium — extend `EsqlValueConverter` | Block A+ |
| `groupBy` combinator | Per-user aggregation in piescript | Medium — new prelude built-in | Block C |
| `sort` / `take` combinators | Top-N selection, sorted output | Small-medium — new prelude built-ins | Block C |

The double arithmetic and `pow` gaps are the most immediately actionable — they're straightforward
extensions that unblock the core value demonstration (user-defined aggregate functions).

---

## Aspirational: Multi-Source Enrichment Pipeline

The MVP north star from [vision.md](vision.md) — a single typed program replacing the
combination of ES Transforms, enrich policies, enrich processors, and ingest pipeline chains:

```
let orders = spawn (query `FROM incoming-orders | WHERE @timestamp > now() - 1h`)
in let customers = spawn (query `FROM customer-database`)
in when (orders o) & (customers c) -> {
  let enriched = o |> map (fn order ->
    let cust = c
      |> filter (fn c -> c.id == order.customer_id)
      |> reduce { name: "", tier: "" } (fn _ c -> { name: c.name, tier: c.tier })
    in { order | customer_name: cust.name, tier: cust.tier })

  in let summary = enriched
    |> groupBy .tier
    |> map (fn group ->
      reduce { count: 0, revenue: 0 } (fn acc row ->
        { count: acc.count + 1, revenue: acc.revenue + row.amount }) group)

  in summary |> writeTo "order-summary-by-tier"
}
```

**Requires**: Block A (done) + `groupBy` (old Block C, now deferred) + `writeTo` (Block E) +
double arithmetic (D-020).

---

## MVP Target: Distributed Vertical Slice (Blocks B–D)

> **Added**: 2026-03-17. See D-042 for the full design rationale.

The primary MVP target is now the **distributed vertical slice**: a piescript program that
discovers cluster topology, ships code to data nodes, accesses local data, and coordinates
results via channels. This is the capability that no other Elasticsearch feature provides at the
language level.

### The Target Program

```
let topo = index_topology "my-index"
in let ch = spawn!
in let target = head topo
in send target.node.inbox (fn () ->
  let data = scan target |> filter (fn r -> r.status == "active")
  in send ch data
)
in when (ch results) ->
  results |> map (fn r -> { id: r.id, ran_on: local_node, status: r.status })
```

### What This Demonstrates

- **Topology discovery**: `index_topology` returns cluster topology as typed records.
- **Bare channel creation**: `spawn!` creates a channel without executing a body.
- **Code mobility**: the closure `(fn () -> ...)` captures the channel reference and travels to
  the data node.
- **Local data access**: `scan target` runs a Lucene query on the data node's shard.
- **Explicit send**: `send ch data` routes the result back to the coordinator (locality property).
- **Verification**: `local_node` in the response proves the code ran on a remote node.

### What's Needed

| Gap | Block | Effort |
|-----|-------|--------|
| Topology builtin (`index_topology`) | B | Small — reads ClusterState, converts to records |
| `spawn!` (bare channel creation) | C | Small — `new SubscribableListener<>()` in SpawnVal |
| `send` primitive (local + cross-node) | C | Medium — local is trivial, cross-node needs transport |
| `Value` serialization (`Writeable`) | C | Medium — recursive, but well-defined |
| `CoreExpr` serialization (Core IR) | C | Medium — tree of records |
| Channel registry | C | Small — `ConcurrentHashMap` per node |
| Transport handlers | C | Medium — two actions, standard ES pattern |
| Remote evaluator | C | Medium — evaluate closure on data node |
| `scan` builtin | D | Medium — Lucene queries on local shard |
| `local_node` builtin | C | Small — returns node name |

---

## Scale Limitations (Honest Assessment)

All examples above run correctly today (for the "Working Today" section) or are blocked only by
specific missing features (for the aspirational ones). However, the current implementation has
scale ceilings:

| Limitation | Impact | Resolution |
|-----------|--------|------------|
| Full materialization | Entire ESQL result loaded into `List<Value>` on coordinator | Exchange streaming (orchestrated explicitly by piescript via channels) |
| Coordinator-bound compute | All `map`/`filter`/`reduce` runs on coordinator node | Distributed execution (Blocks B–D: ship compute to data nodes) |
| O(n×m) joins | Cross-index enrichment is nested loop | Distributed joins or typeclass push-down to Lucene |
| No push-down | `filter` after query fetches everything, then filters in piescript | Typeclass-driven push-down: `Filterable RawData` → Lucene query |
| Sequential stream processing | `map`/`filter`/`reduce` are single-threaded | Exchange streaming for parallelism |

The concurrent query coordination (`spawn`/`when`) does scale — wall-clock time is
`max(queries)` not `sum(queries)`. But per-query result processing is coordinator-bound.

The distributed vertical slice (Blocks B–D) directly addresses coordinator-bound compute by
shipping code to data nodes. Scale optimizations (Exchange streaming, typeclass push-down) are
post-MVP enhancements that piescript orchestrates explicitly via channels — the Exchange is ES
infrastructure, not hidden runtime magic.
