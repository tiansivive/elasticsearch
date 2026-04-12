---
tags: [example, external, esql, coordination, lifecycle]
refs:
  - thread:external-interaction
  - doc:vision.md
---
# Incremental Risk Scoring Example

End-to-end example combining all three external interaction layers: a persistent
piescript actor paginates through alerts, computes risk scores incrementally,
and pushes each batch to three destinations simultaneously — ES (persistence),
Kafka (downstream systems), and an SSE channel (Kibana real-time display).

```
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

    in let u1 = Index.bulk "risk-scores" scored
    in let u2 = Kafka.produce "risk-score-updates" scored
    in let u3 = send scores_out scored
    in let last = List.head (List.tail raw)
    in self self last.user_name

in process process ""
```

One program replaces: a Transform (pagination + writes), a Watcher
(scheduling), a Kafka connector (event publishing), and client-side
orchestration (progress tracking).

**Depends on**: [[actor-model.lifecycle]], [[named-channels.lifecycle]], [[plugin-spi.external]], [[sse-streaming.external]], [[recursion.hub]]
**Enables**: (none directly)
**Connections**:
- example-of: [[external-interaction.thread]] — the motivating end-to-end example
- example-of: [[risk-score-pattern.data]] — extends the basic risk score pattern with actor lifecycle
- example-of: [[transform-unification.external]] — demonstrates replacing Transforms + Watcher + Kafka connector
- uses: [[esql-aggregates.esql]] — ESQL.statsBy with ESQL.top for aggregation
- uses: [[composite-paging.data]] — after_key pagination pattern
