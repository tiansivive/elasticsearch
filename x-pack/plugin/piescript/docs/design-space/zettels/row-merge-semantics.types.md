---
tags: [types, row-types, implemented]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Row Merge Semantics

The `&` row merge operator uses right-biased overlap: when both rows have the same field label, the right operand's type wins. Consistent with TypeScript intersection semantics. Implemented in `force` as `mergeRows`. The choice affects `ESQL.statsBy` output types where group-by keys and aggregate results may share field names — right-biased means the aggregate definition takes precedence.

**Depends on**: [[row-operators.types]], [[f-omega-lite.types]]
**Enables**: [[esql-aggregates.esql]]
**Connections**:
- contrasts-with: left-biased merge would give group keys precedence
- related: [[esql-aggregates.esql]] — statsBy output is `ESQL (s & t)` where s=aggregates, t=keys
- related: [[row-polymorphism.types]] — merge builds on open-row unification
- related: [[nbe-dual-pattern.types]] — merge reduction happens in the force normalizer
