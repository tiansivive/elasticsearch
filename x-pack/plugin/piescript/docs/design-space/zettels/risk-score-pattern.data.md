---
tags: [data, esql, implemented, aggregation, concept, example]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Risk Score Pattern

The risk score query pattern:

- [[esql-aggregates.esql|ESQL]] handles grouping + TOP-N aggregation (`ESQL.statsBy` with `ESQL.top`).
- Piescript handles user-defined computation over the resulting multi-value lists (weighted sum via `List.reduce`).
- Validates the [[t-linq.esql|T-LINQ]] + MV [[type-driven-materialization.esql|materialization]] approach end-to-end.
- The [[composite-paging.data|composite aggregation paging]] pattern (after_key + KQL range filters) maps to recursive piescript loops where each iteration recompiles the ESQL query with a captured after_key.

**Depends on**: [[esql-aggregates.esql]], [[type-driven-materialization.esql]]
**Enables**: (none directly)
**Connections**:
- motivates: [[esql-topby.esql]] — proposed `ESQL.topBy` (multi-field TOP) would need a `MapList` type operator
- motivates: [[maplist-operator.types]] — lifting record fields to `List`s ties into F-omega extensibility
- validates: [[nbe-compilation.esql]] — the motivating real-world example
- uses: [[composite-paging.data]] — composite aggregation paging is part of the risk score pattern
- example-of: [[feature-engineering.data]] — risk scoring is a specific instance of feature engineering
