---
tags: [esql, implemented, aggregation]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL Aggregates

`ESQL.stats`/`statsBy` for `STATS` command. `statsBy` output type is `ESQL (s & t)` — merge of aggregates and group keys via `&` row operator. Aggregate builtins: `ESQL.count`, `countOf`, `avg`, `sum`, `max`, `min`, `bucket`. `ESQL.top` and `ESQL.values` return `List a` (MV-returning). Type-driven materialization identifies `List`-typed columns.

**Depends on**: [[esql-compilation.esql]], [[row-operators.types]], [[f-omega-lite.types]]
**Enables**: (none directly)
**Connections**:
- related: [[esql-expression-wrapper.types]] — aggregate builtins produce `Symbol` where type says `Double` (a type-level lie); future ESQL expression wrapper type
- related: [[risk-score-pattern.data]] — risk score pattern is the motivating use case for ESQL.stats/top
- related: [[type-driven-materialization.esql]] — List-typed aggregate columns require type-driven materialization
