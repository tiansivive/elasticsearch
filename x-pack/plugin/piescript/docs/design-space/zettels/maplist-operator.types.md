---
tags: [types, open]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# MapList Operator

`MapList : Row → Row` — a type-level operator that lifts each field in a row to `List`. Proposed for `ESQL.topBy` record variant where `ESQL.topBy r.score 3 "desc" { ids: r.id, times: r.time }` would return a record with each field wrapped in `List`. Would be a new reducible builtin in `force`, following the F-omega extensibility pattern.

**Depends on**: [[f-omega-lite.types]], [[row-operators.types]]
**Enables**: (none directly)
**Connections**:
- related: [[nbe-dual-pattern.types]] — demonstrates F-omega extensibility: add a case to `force`, get a new type-level operator
- related: [[risk-score-pattern.data]] — motivated by the risk score query's need for correlated multi-field TOP-N
