# Piescript — Presentation Source Material

> External-facing pitch and example portfolio. Source material for talks, demos, and
> docs aimed at audiences outside the piescript team. For internal context see
> [vision.md](vision.md), the design-space knowledge base, and
> [archive/mvp.md](archive/mvp.md).

## One-liner

Piescript is a **typed functional language for distributed computation in
Elasticsearch**. It uses Join Calculus primitives (`spawn`, `when`, `send`, channels)
to coordinate asynchronous data pipelines that run where the data lives, with full
ESQL compilation underneath.

## The two motivating problems

### 1. The extraction cliff

The moment a user's needs exceed what ESQL can express, they leave Elasticsearch
entirely — Spark, Python, Airflow. That extraction is expensive, slow, operationally
complex, and untyped at boundaries. Piescript pushes the cliff back: more workflows
stay inside ES, typed and distributed.

### 2. The feature constellation

The pattern *query → compute → output* is implemented inside Elasticsearch as
**6+ separate features** with overlapping capabilities, different APIs, different
execution models, no composition:

| Feature | When it runs | What it does |
|--------|-------------|-------------|
| Ingest pipelines | write time | transform docs |
| Enrich processors | write time | lookup join |
| Transforms | scheduled batch | query → aggregate → write |
| Watcher | scheduled/triggered | query → condition → action |
| Runtime fields | query time | derived columns |
| Painless scripts | various | imperative escape hatch |

Combining capabilities (enrich + aggregate + alert) means wiring fragments together
with glue logic. Piescript subsumes all of these as a single typed program.

## The three differentiators

- **Composition** — pipe operator, currying, higher-order functions, named
  intermediate results. Operations chain naturally.
- **Types** — row-polymorphic records, type inference from real index field caps,
  compile-time safety across multi-query pipelines.
- **Distribution** — Join Calculus coordination, code mobility, topology as
  first-class typed values. *Code goes to data, not data to code.*

Piescript is **not** a replacement for ESQL or Painless. It *wraps* ESQL via T-LINQ
compilation; it *coexists with* Painless (single-document imperative scope is
Painless's niche; piescript handles multi-document and distributed).

Target audience: security analysts, observability engineers, data engineers — the
people who today write Painless scripts, Watcher JSON, Transform configs, and ESQL
queries. Not PL researchers.

## What runs today

Phases 0–2 + Blocks A–G are complete. The language supports:

- Concurrent multi-index queries (`spawn`/`when`)
- Cross-node code execution (`send` to remote inboxes)
- Local data access (`use`, `Shard.open`, `Shard.consume`, `Shard.read`)
- Writes (`Shard.writer`/`write`/`refresh`, `Index.bulk`, list literals)
- T-LINQ ESQL compilation (`query ESQL.from idx |> ESQL.where ... ;`)
- F-omega-lite type system (`force` normalizer, `&`/`Pick`/`Omit` row operators,
  `ESQL.stats`/`ESQL.statsBy` + aggregate builtins)
- Pattern matching, recursion, fused `loop`/`repeat`
- Compute-engine streaming (Pages, Exchange, columnar materialization)

## Example portfolio

### A. Watchlist cross-reference *(runs today)*

Real Elastic Security workflow during Watchlists feature dev. Validating sync
correctness today requires **7 manual ESQL queries** with results compared by
eyeballing entity IDs across query outputs.

```piescript
use ".entity_analytics.watchlists.default" as watchlist;
use "logs-entityanalytics_okta.user-default" as okta;
use "logs-entityanalytics_ad.user-default" as ad;

-- Run all 3 queries concurrently
let wl_ch = spawn query ESQL.from watchlist
  |> ESQL.keep (fn r -> { id: r.entity.id, name: r.entity.name });

let okta_ch = spawn query ESQL.from okta
  |> ESQL.where (fn r -> r.user.roles == "Super Administrator"
                      || r.user.roles == "Organization Administrator")
  |> ESQL.statsBy
       (fn r -> { latest: ESQL.max r.@timestamp })
       (fn r -> { name: r.user.name, id: r.user.id });

let ad_ch = spawn query ESQL.from ad
  |> ESQL.where (fn r -> r.entityanalytics_ad.user.privileged_group_member == true)
  |> ESQL.statsBy
       (fn r -> { latest: ESQL.max r.@timestamp })
       (fn r -> { name: r.user.name, id: r.user.id });

-- Synchronize and cross-reference
when (wl_ch wl) & (okta_ch okta_users) & (ad_ch ad_users) ->
  let source_ids = List.map (fn r -> r.id) okta_users
                ++ List.map (fn r -> r.id) ad_users;
  let wl_ids = List.map (fn r -> r.id) wl;

  let in_source_not_in_wl = List.filter (fn id ->
    not (List.any (fn wid -> wid == id) wl_ids)
  ) source_ids;

  {
    watchlist_count: List.length wl,
    okta_count: List.length okta_users,
    ad_count: List.length ad_users,
    in_source_not_in_watchlist: in_source_not_in_wl
  }
```

| Concern | ESQL only | Piescript |
|---------|-----------|-----------|
| Queries | 7 separate, run manually | 3 queries in one program |
| Concurrency | Sequential | `spawn` runs all 3 in parallel, `when` synchronizes |
| Cross-referencing | Manual eyeballing of IDs | Programmatic set difference via `List.filter` |
| Result | 7 separate result tables | One structured record |
| Type safety | None across queries | All 3 indices type-checked from field caps |

This is the most immediately tangible "ESQL alone is hard, piescript is simple"
story for an Elasticsearch audience.

### B. Risk score pattern *(runs today)*

Real Kibana entity-risk-scoring query. Currently in production:

```sql
FROM .alerts-security METADATA _index
| WHERE kibana.alert.risk_score IS NOT NULL AND KQL("@timestamp >= now-1h")
| EVAL input = CONCAT(
    """ {"risk_score": """", risk_score::keyword, """",
    "time": """", time::keyword, """", ... )
| STATS
    alert_count = count(risk_score),
    scores = MV_PSERIES_WEIGHTED_SUM(TOP(risk_score, 10000, "desc"), 1.5),
    risk_inputs = TOP(input, 10, "desc")
  BY user.name
```

Two pain points: `MV_PSERIES_WEIGHTED_SUM` had to be added to ESQL as a built-in
because ESQL has no UDFs. The `CONCAT`-based JSON construction with escaped quotes
is a fragile, untyped workaround for ESQL's flat output.

In piescript, `MV_PSERIES_WEIGHTED_SUM` is a **let-binding**, not an ESQL PR:

```piescript
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

ESQL handles `STATS BY user.name | TOP(risk_score, 10000)` (Lucene-backed,
vectorized). Piescript handles the user-defined weighted sum and the structured
output. No `CONCAT`, no `TO_BASE64`, no escaped quotes. Type-checked end to end.

**Key story**: every domain-specific aggregate becomes a function, not a JIRA
ticket against the ESQL team.

### C. The four "Working Today" pillar examples

Each addresses a specific ESQL limitation:

**1. Concurrent multi-index queries.** Wall-clock `max(a, b)` instead of
`sum(a, b)`:

```piescript
let a = spawn (query `FROM orders | WHERE @timestamp > now() - 1h`)
in let b = spawn (query `FROM customers`)
in when (a orders) & (b customers) ->
  { order_count: List.length orders, customer_count: List.length customers }
```

One request, one response. Both queries fire on the GENERIC thread pool. The type
checker verifies field compatibility across the entire pipeline before anything
runs.

**2. Query-time cross-index enrichment.** No enrich policy, live data:

```piescript
when (orders o) & (customers c) ->
  o |> List.map (fn order ->
    let match = c
      |> List.filter (fn cust -> cust.id == order.customer_id)
      |> List.reduce (fn _ cust -> { name: cust.name }) { name: "unknown" }
    in { order | customer_name: match.name }
  )
```

Live data (not a stale snapshot like ESQL `ENRICH`), no infrastructure setup,
arbitrary matching predicates, full functional composition after the join.

**3. Structured record output.** No JSON-string hacks:

```piescript
docs |> List.map (fn alert -> {
  risk_score: alert.kibana.alert.risk_score,
  rule_name: alert.kibana.alert.rule.name,
  alert_id: alert.kibana.alert.uuid,
  category: alert.event.kind,
  time: alert.@timestamp
})
```

Records are first-class. Properly serialized as JSON. No `CONCAT`, no escaped
quotes, no `TO_BASE64`.

**4. Named intermediate results and reuse.** DRY queries:

```piescript
let raw = query `FROM events`
in let errors = List.filter (fn r -> r.level == "ERROR") raw
in let warnings = List.filter (fn r -> r.level == "WARN") raw
in { error_count: List.length errors, warn_count: List.length warnings }
```

One query, two derived computations. Let-polymorphic bindings ensure type safety
across all uses.

### D. Distributed vertical slice — the architectural moat *(runs today)*

This is what no other Elasticsearch feature provides at the language level:

```piescript
let topo = topology "my-index"
in let target = List.head topo.shards
in let ch = spawn!
in send target.node.inbox (fn () ->
  let data = scan target |> List.filter (fn r -> r.status == "active")
  in send ch data
)
in when (ch results) ->
  results |> List.map (fn r -> { id: r.id, ran_on: local_node, status: r.status })
```

What this shows:

- **Topology discovery**: `topology` returns cluster topology as typed records.
- **Bare channel creation**: `spawn!` creates a channel without executing a body.
- **Code mobility**: the closure `(fn () -> ...)` captures the channel reference
  and travels to the data node — purity makes this safe.
- **Local data access**: `scan target` runs on the data node's shard.
- **Explicit send**: `send ch data` routes the result back to the coordinator
  (Join Calculus locality property).
- **Verification**: `local_node` in the response proves the code ran on a remote
  node.

The competitive moat against Spark/Flink: piescript distributes by design and runs
inside the cluster the data already lives in. No data movement, no cluster
duplication, no language fragmentation.

### E. Incremental risk scoring — the vision shot *(needs actor model + named channels + plugin SPI)*

Speculative end-to-end example combining all three external-interaction layers:

```piescript
-- Submitted via PUT _piescript/run
-- Kibana connects via GET _piescript/{id}/channels/scores/stream (SSE)

use ".alerts-security" as idx;

let pseries_weighted_sum = fn s values ->
  List.reduce (fn acc v ->
    { sum: acc.sum + v / Math.pow acc.i s, i: acc.i + 1 }
  ) { sum: 0.0, i: 1.0 } values
  |> (fn state -> state.sum)

in let scores_out = expose! "scores"   -- Kibana subscribes here via SSE
in let done_out = expose! "done"

in let process = fn self after_key ->
  let raw = query ESQL.from idx
    |> ESQL.where (fn r -> r.user.name > after_key)
    |> ESQL.statsBy
         (fn r -> {
           top_scores: ESQL.top r.kibana.alert.risk_score 10000 "desc",
           alert_count: ESQL.count "*"
         })
         (fn r -> { user_name: r.user.name })
    |> ESQL.limit 1000;

  in if List.isEmpty raw then send done_out { status: "complete" }
  else
    let scored = List.map (fn user -> {
      user_name: user.user_name,
      score: pseries_weighted_sum 1.5 user.top_scores,
      alert_count: user.alert_count
    }) raw

    in let u1 = Index.bulk "risk-scores" scored          -- ES persistence
    in let u2 = Kafka.produce "risk-score-updates" scored -- downstream systems
    in let u3 = send scores_out scored                    -- Kibana real-time
    in let last = List.head (List.tail raw)
    in self self last.user_name

in process process ""
```

A persistent piescript actor paginates alerts, computes risk scores incrementally,
and pushes each batch to **three destinations simultaneously**:

- ES (persistence via `Index.bulk`)
- Kafka (downstream systems via plugin SPI)
- SSE channel (Kibana real-time display via named channels)

**One program replaces**: a Transform (pagination + writes), a Watcher (scheduling),
a Kafka connector (event publishing), and client-side orchestration (progress
tracking).

This is the flagship demo for "what is piescript for, in one slide."

## Suggested narrative arc

For a 30–45 minute talk:

1. **Hook (5 min)** — open with the watchlist 7-queries-to-1-program story. It is
   concrete, real, and ships today. Show the pain side-by-side.

2. **The two problems (5 min)** — the extraction cliff and the feature
   constellation. The audience already lives in this pain.

3. **The three differentiators (5 min)** — composition, types, distribution. One
   tiny snippet for each, drawn from the four pillar examples.

4. **The architectural moat (10 min)** — code goes to data. Walk through the
   distributed vertical slice example. Topology, `send` to inbox, `scan`, channel
   coordination. Emphasise that purity is what makes shipping closures safe.

5. **The vision shot (10 min)** — incremental risk scoring as one program
   replacing Transform+Watcher+Kafka+SSE. Even if framed as "what's coming
   next," the program fits on one screen and lands the unification thesis.

6. **What runs today vs. what's next (5 min)** — close with status: phases 0–2 +
   Blocks A–G shipped, threads in flight (error handling, language
   expressiveness, distributed coordination, type foundations, external
   interaction, data completeness).

## What to develop next for demo impact

Ranked by demo "wow per investment":

1. **Polish the watchlist demo into a screencast.** Just needs `++` (list concat)
   to ship cleanly. Smallest gap-to-shipping for the highest-resonance story.
2. **Risk score side-by-side in slide form.** Already runnable. Need a clean
   before/after layout.
3. **Distributed vertical slice as an architecture explainer.** Already runnable
   end-to-end. Recording one demo would make the architectural moat tangible.
4. **Push toward incremental risk scoring as flagship.** Requires actor model +
   named channels + SSE + plugin SPI (Kafka). Most ambitious; biggest payoff.
5. **Feature-engineering pipeline for ML.** The canonical extraction-cliff use
   case. Cross-index joins, time-series aggregations, normalised scores written
   back as feature vectors — without leaving ES. Hits the ML-adjacent audience
   that today reaches for Spark.

## Reference

- [vision.md](vision.md) — the long-form positioning
- [current-state.md](current-state.md) — what's implemented now
- [archive/mvp.md](archive/mvp.md) — the original MVP examples document
- [design-space/zettels/value-proposition.principle.md](design-space/zettels/value-proposition.principle.md)
- [design-space/zettels/extraction-cliff.external.md](design-space/zettels/extraction-cliff.external.md)
- [design-space/zettels/feature-constellation.external.md](design-space/zettels/feature-constellation.external.md)
- [design-space/zettels/watchlist-cross-ref.example.md](design-space/zettels/watchlist-cross-ref.example.md)
- [design-space/zettels/risk-score-pattern.data.md](design-space/zettels/risk-score-pattern.data.md)
- [design-space/zettels/risk-scoring-incremental.example.md](design-space/zettels/risk-scoring-incremental.example.md)
- [design-space/zettels/target-users.principle.md](design-space/zettels/target-users.principle.md)
