---
tags: [data, esql, implemented, aggregation]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Risk Score Pattern

The risk score query pattern: ESQL handles grouping + TOP-N aggregation (`ESQL.statsBy` with `ESQL.top`), piescript handles user-defined computation over the resulting multi-value lists (weighted sum via `List.reduce`). Validates the T-LINQ + MV materialization approach end-to-end. The composite aggregation paging pattern (after_key + KQL range filters) maps to recursive piescript loops where each iteration recompiles the ESQL query with a captured after_key.

**Depends on**: [[esql-aggregates.esql]], [[type-driven-materialization.esql]]
**Enables**: (none directly)
**Connections**:
- related: [[esql-topby.esql]] — proposed `ESQL.topBy` (multi-field TOP) would need a `MapList` type operator
- related: [[maplist-operator.types]] — lifting record fields to `List`s ties into F-omega extensibility
- related: the motivating real-world example
- related: [[composite-paging.data]] — composite aggregation paging is part of the risk score pattern
- related: [[feature-engineering.data]] — risk scoring is a specific instance of feature engineering
