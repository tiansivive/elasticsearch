---
tags: [roadmap, esql, compilation, implemented]
refs:
  - adr:D-052
  - adr:D-053
  - plan:block_f_linq_query_e7171607
  - plan:f-omega_type_system_09acfb27
---
# Block F — T-LINQ ESQL Query Compilation

Typed, composable ESQL via Normalization by Evaluation. Symbol-based partial evaluation, F-omega-lite kind system, row operators, aggregate compilation model.

**Depends on**: [[block-e.roadmap]]
**Enables**: [[block-g.roadmap]]
**Connections**:
- part-of: [[roadmap-hub.roadmap]]
- subsumes: [[esql-compilation.esql]]
- subsumes: [[nbe-compilation.esql]]
- subsumes: [[symbol-partial-evaluation.esql]]
- subsumes: [[compilation-boundary.esql]]
- subsumes: [[f-omega-lite.types]]
- subsumes: [[row-operators.types]]
- subsumes: [[esql-aggregates.esql]]
- subsumes: [[esql-combinators.esql]]
- subsumes: [[type-driven-materialization.esql]]
- subsumes: [[aggregate-compilation-model.esql]]
- subsumes: [[rowtype-as-monotype.types]]
