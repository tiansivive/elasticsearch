# Piescript Use-Case Examples

Real-world piescript programs found across the docs and design space, with verbatim source,
current status, and missing features. Sources cited per example.

Status legend: ✅ implemented / runs today · ◐ partially runnable (subset works) · ❌ blocked on
unimplemented features.

---

## Risk Scoring

The risk-scoring use case exists at three maturity tiers.

### Tier 1 — Implemented MVP ✅

Hybrid pattern: push filtering / grouping / TOP-N into ESQL; pull the user-defined aggregate
(`pseries_weighted_sum`, a p-series `Σ vᵢ / iˢ`) into piescript as a plain `let`-binding over the
`List Double` that `ESQL.top` materializes. Replaces an ESQL query that required adding
`MV_PSERIES_WEIGHTED_SUM` to the ESQL codebase plus a `CONCAT`/`TO_BASE64` JSON-building hack.

Source: `docs/archive/mvp.md:169-246`. Verified end-to-end against real nested data
(`mvp.md:365-396`), producing output like `[{user_name: "alice", alert_count: 2, risk_score: 117.89}, ...]`.
Zettel: `docs/z-piescript/zettels/risk-score-pattern.data.md` (tagged `implemented`).

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

A trimmed, live-in-the-test-suite variant (`debug/test-multinode.sh:349-360`, test 31):

```piescript
use "piescript-test" as idx;
let raw = query ESQL.from idx
  |> ESQL.statsBy
       (fn r -> { top_scores: ESQL.top r.score 5 "desc" })
       (fn r -> { active: r.active });
in List.map (fn row -> {
  active: row.active,
  top_scores: row.top_scores,
  total: List.reduce (fn acc v -> acc + v) 0 row.top_scores
}) raw
```

Missing for the full Tier 1 query (`mvp.md:227-230`):
- `IS NOT NULL` — no null-check syntax; uses `!= 0` workaround.
- `METADATA _index` — metadata fields not in the row type.
- `ESQL.topBy` — correlated TOP with output field; needed to carry multi-field risk inputs without
  the CONCAT hack.

Performance note (`mvp.md:233-246`): `List.reduce` walks boxed `DoubleVal`s through the tree-walking
evaluator (10,000 interpreter steps) vs ESQL's one vectorized native call. The value proposition is
extensibility, not raw speed — the aggregate is a `let`-binding, not a PR to ESQL.

### Tier 2 — Intermediate ◐

No dedicated example file. Exists as the runnable subset of the Tier 3 program: recursive
pagination (composite after_key loop — marked "Works", `mvp.md:231`) + `pseries` scoring +
write-back to ES via `Index.bulk` (implemented), self-looping via recursion. Drop Kafka, SSE, and
named channels from the Tier 3 program and it runs today.

### Tier 3 — Long-term vision ❌

Persistent actor: paginate alerts, score incrementally, fan each batch out to three sinks
simultaneously — ES (`Index.bulk` ✅), Kafka (`Kafka.produce` ❌), and an SSE channel to Kibana
(`send scores_out` ❌). One program replaces a Transform + a Watcher + a Kafka connector +
client-side orchestration.

Source: `docs/z-piescript/zettels/risk-scoring-incremental.example.md`.

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
in let done_out = expose! "done"       -- signals completion

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

    in let u1 = Index.bulk "risk-scores" scored          -- implemented
    in let u2 = Kafka.produce "risk-score-updates" scored -- NOT implemented (plugin SPI)
    in let u3 = send scores_out scored                    -- NOT implemented (named channels)
    in let last = List.head (List.tail raw)
    in self self last.user_name

in process process ""
```

Blocked on (zettel `Depends on`): actor model (`actor-model.lifecycle`), named channels
(`expose!` / `named-channels.lifecycle`), plugin SPI (`Kafka.produce` / `plugin-spi.external`),
SSE streaming (`sse-streaming.external`).

---

## Watchlist Cross-Reference ✅

Real Elastic Security (Entity Analytics) use case. Watchlists sync privileged users from Okta and
AD into an internal index; validating the sync means cross-referencing the watchlist against the
sources in both directions. Today that is 7 manual ESQL queries with IDs eyeballed across outputs.
Piescript does it in one concurrent, type-checked program.

Source: `docs/z-piescript/zettels/watchlist-cross-ref.example.md`.

```piescript
use ".entity_analytics.watchlists.default" as watchlist;
use "logs-entityanalytics_okta.user-default" as okta;
use "logs-entityanalytics_ad.user-default" as ad;

-- Run all 3 queries concurrently
let wl_ch = spawn query ESQL.from watchlist
  |> ESQL.keep (fn r -> { id: r.entity.id, name: r.entity.name });

let okta_ch = spawn query ESQL.from okta
  |> ESQL.where (fn r -> r.user.roles == "Super Administrator"
                      || r.user.roles == "Organization Administrator"
                      || r.user.roles == "Group Administrator" 
                      || r.user.roles == "Application Administrator")
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

  let in_wl_not_in_source = List.filter (fn id ->
    not (List.any (fn sid -> sid == id) source_ids)
  ) wl_ids;

  {
    watchlist_count: List.length wl,
    okta_count: List.length okta_users,
    ad_count: List.length ad_users,
    in_source_not_in_watchlist: in_source_not_in_wl,
    in_watchlist_not_in_source: in_wl_not_in_source
  }
```

Status: runs today. The `++` operator is not implemented; substitute `List.concat` (shipped
2026-05-07):

```piescript
let source_ids = List.concat
  (List.map (fn r -> r.id) okta_users)
  (List.map (fn r -> r.id) ad_users);
```

Demonstrates: multi-index `use`, ESQL compilation (`from`/`where`/`statsBy`/`keep`), concurrent
`spawn` + `when`, cross-index join via list ops, structured record output, field-level type safety
from field caps.

---

## Multi-Source Enrichment Pipeline ❌

North-star MVP from `vision.md` — one typed program replacing ES Transforms + enrich policies +
enrich processors + ingest pipeline chains. Orders enriched with customer data, aggregated by tier,
summary written back.

Source: `docs/archive/mvp.md:250-273`.

```piescript
let orders = spawn (query `FROM incoming-orders | WHERE @timestamp > now() - 1h`)
in let customers = spawn (query `FROM customer-database`)
in when (orders o) & (customers c) -> {
  let enriched = o |> map (fn order ->
    let cust = c
      |> filter (fn c -> c.id == order.customer_id)
      |> reduce { name: "", tier: "" } (fn _ c -> { name: c.name, tier: c.tier })
    in { order | customer_name: cust.name, tier: cust.tier })

  in let summary = enriched
    |> groupBy .tier     -- NOT implemented
    |> map (fn group ->
      reduce { count: 0, revenue: 0 } (fn acc row ->
        { count: acc.count + 1, revenue: acc.revenue + row.amount }) group)

  in summary |> writeTo "order-summary-by-tier"  -- NOT implemented
}
```

Blocked on: `groupBy` combinator and `writeTo`. (Uses the old backtick-ESQL syntax; the current
form is `query ESQL.from ... ;`.)

---

## Feature Engineering (ML) — designed, not implemented ❌

ML feature-vector generation: query ES data, compute derived features (cross-index joins,
time-series aggregations, normalized/windowed stats), write feature vectors back. Risk scoring is a
special case of this. Today requires extraction to Python/Spark.

Source: `docs/z-piescript/zettels/feature-engineering.data.md` (tagged `designed`; no code snippet —
a use-case pattern). Blocked on the `query-typeclass`.

---

## Distributed Vertical Slice ✅

Code mobility: discover topology, `send` closures to remote node inboxes, scan local shards,
coordinate results via channels. Verified working on 3-node clusters.

Source: `docs/presentation.md:251-265` (not re-verified line-for-line in this pass).

---

## Feature availability summary

| Feature | Status |
|---------|--------|
| `query ESQL.from ... \|> ESQL.where/keep/statsBy/sort/limit` | ✅ |
| `ESQL.top` / `ESQL.values` (MV aggregates, type-driven materialization) | ✅ |
| `ESQL.count` / `ESQL.max` etc. aggregates | ✅ |
| `List.map` / `List.filter` / `List.reduce` / `List.any` / `List.length` / `List.head` / `List.tail` | ✅ |
| `List.concat` | ✅ |
| `Math.pow` and Math builtins | ✅ |
| `spawn` / `when` (concurrent queries + synchronization) | ✅ |
| `send` to remote node inbox (code mobility) | ✅ |
| Recursion / self-looping (composite after_key pagination) | ✅ |
| `Index.bulk` write-back | ✅ |
| `++` list concat operator | ❌ (use `List.concat`) |
| `IS NOT NULL` syntax | ❌ (use `!= 0`) |
| `METADATA _index` in `ESQL.from` | ❌ |
| `ESQL.topBy` (correlated TOP w/ output field) | ❌ |
| `groupBy` combinator | ❌ |
| `writeTo` | ❌ |
| Named channels (`expose!`) | ❌ |
| Actor model / persistent scripts (`PUT _piescript/run`) | ❌ |
| SSE streaming to clients | ❌ |
| Plugin SPI (`Kafka.produce`) | ❌ |
| `query-typeclass` (push-down) | ❌ |
