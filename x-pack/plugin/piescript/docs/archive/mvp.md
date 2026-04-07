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
use ".alerts-security" as idx;
let pseries_weighted_sum = fn s values ->
  let state = List.reduce (fn acc v ->
    { sum: acc.sum + v / Math.pow acc.i s, i: acc.i + 1 }
  ) { sum: 0.0, i: 1 } values
  in state.sum

in let raw = query ESQL.from idx
  |> ESQL.where (fn r -> r.kibana.alert.risk_score != 0)
  |> ESQL.statsBy
       (fn r -> {
         alert_count: ESQL.count "*",
         top_scores: ESQL.top r.kibana.alert.risk_score 10000 "desc",
         top_ids: ESQL.top r.kibana.alert.uuid 10 "desc"
       })
       (fn r -> { user_name: r.user.name })
  |> ESQL.sort (fn r -> r.alert_count)
  |> ESQL.limit 3000;

in List.map (fn user -> {
  user_name: user.user_name,
  alert_count: user.alert_count,
  score: pseries_weighted_sum 1.5 user.top_scores,
  risk_inputs: List.map (fn id -> { alert_id: id }) user.top_ids
}) raw
```

### What This Demonstrates

- **`MV_PSERIES_WEIGHTED_SUM` becomes a let-binding** — a user-defined function, not an ESQL
  built-in. Any domain-specific aggregate can be expressed this way without modifying the ESQL
  codebase.
- **No JSON string hacks** — `risk_inputs` is a list of records, properly serialized as JSON.
  No `CONCAT`, no `TO_BASE64`, no escaped quotes.
- **Composable** — the scoring function, the query, and the output formatting are separate,
  testable, reusable pieces. `pseries_weighted_sum` can be used across different queries.
- **Type-safe** — the type checker verifies that `user.top_scores` is `List Double`, that
  `user.user_name` exists, etc. `ESQL.top r.field N "order"` returns `List a` where `a` is
  inferred from the field's type.
- **T-LINQ compilation** — `ESQL.from`, `ESQL.where`, `ESQL.statsBy`, `ESQL.top`, `ESQL.sort`,
  `ESQL.limit` compile to ESQL via NbE (Symbol-based partial evaluation). The generated ESQL
  runs natively on the cluster.

### What Works Now vs What's Missing

| Capability | Status |
|-----------|--------|
| Double/float arithmetic | :white_check_mark: Done (D-020) |
| `Math.pow` function | :white_check_mark: Done (Prelude builtin) |
| `ESQL.top` / `ESQL.values` (MV aggregates) | :white_check_mark: Done (type-driven materialization) |
| T-LINQ ESQL compilation (`query ... ;`) | :white_check_mark: Done (Block F) |
| `List.map` / `List.reduce` | :white_check_mark: Done |
| `ESQL.where` with `IS NOT NULL` | :memo: Requires null-check syntax or `!= 0` workaround |
| `METADATA _index` support in ESQL.from | :memo: Not yet — metadata fields not in row type |
| `ESQL.sortDesc` for descending sort | :white_check_mark: Done |
| `ESQL.topBy` — correlated TOP with outputField | :memo: Needed for multi-field risk inputs (avoids CONCAT hack) |
| Composite aggregation paging (after_key loop) | :white_check_mark: Works — each `query ;` compiles a fresh ESQL string with the updated filter |

### Performance Consideration

The pure ESQL version uses `MV_PSERIES_WEIGHTED_SUM` — a native Java function operating directly
on Lucene's columnar data (tight loop over primitive arrays, no boxing). Piescript's
`List.reduce` walks boxed `DoubleVal`s through the tree-walking evaluator. For 10,000 scores,
this is 10,000 interpreter steps vs one vectorized call.

The value proposition is not raw performance — it's **extensibility**: `pseries_weighted_sum` is
a user-defined let-binding, not a PR to the ESQL codebase. Every new domain-specific aggregate
is a function, not a feature request.

Long-term, pure piescript lambdas are candidates for bytecode compilation over typed arrays (the
kind system separates pure from effectful code). A fold over `List Double` could lower to a tight
`double[]` loop with zero boxing — matching native ESQL performance. See Block G plan for details.

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
let topo = topology "my-index"
in let target = head topo.shards
in let ch = spawn!
in send target.node.inbox (fn () ->
  let data = scan target |> filter (fn r -> r.status == "active")
  in send ch data
)
in when (ch results) ->
  results |> map (fn r -> { id: r.id, ran_on: local_node, status: r.status })
```

### What This Demonstrates

- **Topology discovery**: `topology` returns cluster topology as typed records (D-044).
- **Bare channel creation**: `spawn!` creates a channel without executing a body.
- **Code mobility**: the closure `(fn () -> ...)` captures the channel reference and travels to
  the data node.
- **Local data access**: `scan target` runs a Lucene query on the data node's shard.
- **Explicit send**: `send ch data` routes the result back to the coordinator (locality property).
- **Verification**: `local_node` in the response proves the code ran on a remote node.

### What's Needed

| Gap | Block | Effort |
|-----|-------|--------|
| ~~Topology builtin (`topology`)~~ | ~~B~~ | ~~Complete (D-044)~~ |
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

---

## MVP Closure (2026-04-06)

The MVP is complete. Everything described in this document either works today or has been
superseded by a better approach.

### What was achieved

**Distributed vertical slice (Blocks A–E)**: Topology discovery, cross-node code execution via
channels, local shard data access, write primitives, and ESQL query compilation — all working
end-to-end on a 3-node cluster with security enabled.

**Risk score calculation**: The aspirational case study from this document now runs against real
data. The piescript version uses T-LINQ ESQL compilation (`ESQL.from` → `ESQL.statsBy` with
`ESQL.top` → `ESQL.limit`), type-driven MV materialization, and a user-defined `pseries_weighted_sum`
function via `List.reduce` + `Math.pow`. Tested successfully against a nested test index with
alert-like data structure:

```
use "nested-test" as idx;
let pseries = fn s values ->
  let state = List.reduce (fn acc v ->
    { sum: acc.sum + v / Math.pow acc.i s, i: acc.i + 1 }
  ) { sum: 0.0, i: 1.0 } values
  in state.sum

in let raw = query ESQL.from idx
  |> ESQL.where (fn r -> r.alert.risk_score > 0)
  |> ESQL.statsBy
       (fn r -> {
         alert_count: ESQL.count "*",
         top_scores: ESQL.top r.alert.risk_score 10 "desc"
       })
       (fn r -> { user_name: r.user.name })
  |> ESQL.limit 100;

in List.map (fn user -> {
  user_name: user.user_name,
  alert_count: user.alert_count,
  risk_score: pseries 1.5 user.top_scores
}) raw
```

Result: `[{user_name: "alice", alert_count: 2, risk_score: 117.89}, {user_name: "bob", ...}]`

**Additional capabilities beyond original MVP scope:**
- Block F: T-LINQ ESQL compilation with 20+ combinators (NbE Symbol-based)
- D-053: F-omega-lite type system (kinds-as-types, row operators, `force` normalizer)
- Block G: Columnar streaming (`Shard.stream`, `Page.toList`, Exchange via `ExchangeService`)
- MV aggregates (`ESQL.top`, `ESQL.values`) with type-driven materialization
- Nested record types from field caps (OBJECT fields → nested `RecordType`)
- Security: `cluster:compute/piescript` namespace, works with auth enabled

### What was learned

- **AI-assisted prototyping works** for bridging domain expertise gaps (PLT → ES internals)
- **The type system pays for itself** — row-typed indices, kind-checked type constructors, and
  NbE compilation caught real bugs at elaboration time
- **ES's plugin/transport infrastructure is powerful** — `ExchangeService`, `IndicesService`,
  `ClusterService`, Guice injection, transport actions all compose well once understood
- **The Join Calculus coordination model scales** — channels, send, when, spawn work cleanly
  for distributed multi-node orchestration

### What comes next

This MVP doc is now archived. The next phase focuses on language fundamentals (recursion, error
handling, pattern matching, primitives), the external interaction model (actor lifecycle, plugin
SPI, FFI), and a refined roadmap. See `docs/roadmap.md` for the active development plan.
