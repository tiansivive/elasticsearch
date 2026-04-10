---
tags: [esql, open, concept, needs-design, later]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:data-completeness
---
# ESQL TopBy

Proposed `ESQL.topBy` for correlated multi-field TOP-N: `ESQL.topBy r.score 3 "desc" { ids: r.id, times: r.time }`.
- Would compile to multiple TOP() calls with same sort/N/order but different output fields
- Return type needs [[maplist-operator.types]] to lift each field to `List`
- The record variant is aspirational -- current `ESQL.top` (see [[esql-aggregates.esql]]) handles single-field case

**Depends on**: [[esql-aggregates.esql]], [[maplist-operator.types]]
**Enables**: (none directly)
**Connections**:
- example-of: [[risk-score-pattern.data]] — motivated by risk score query needing correlated alert IDs, timestamps, and scores from TOP-N
