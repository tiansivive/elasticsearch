---
tags: [types, open, concept]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# MapList Operator

`MapList : Row -> Row` -- a type-level operator that lifts each field in a row to [[list-type.language]] `List`. Proposed for `ESQL.topBy` record variant where `ESQL.topBy r.score 3 "desc" { ids: r.id, times: r.time }` would return a record with each field wrapped in `List`. Would be a new reducible builtin in [[force-threading.types]] `force`, following the [[f-omega-lite.types]] extensibility pattern.

**Depends on**: [[f-omega-lite.types]], [[row-operators.types]]
**Enables**: (none directly)
**Connections**:
- example-of: [[nbe-dual-pattern.types]] — demonstrates F-omega extensibility: add a case to `force`, get a new type-level operator
- motivated-by: [[risk-score-pattern.data]] — motivated by the risk score query's need for correlated multi-field TOP-N
- extends: [[kind-system.types]] — MapList is a Row-kinded type operator
