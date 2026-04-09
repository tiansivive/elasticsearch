---
tags: [esql, types, runtime, materialization, implemented, concept]
refs:
  - adr:D-053
  - adr:D-054
  - code:EsqlValueConverter.java
  - code:EvalDependencies.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Type-Driven Materialization

The [[evaluator.language]] `force`s the elaborated row type from `CoreQueryExec.type()` to identify `List`-typed columns in ESQL results:
- [[esql-value-converter.esql]] materializes those as `ListVal` (all elements preserved); other columns use scalar conversion (first element)
- Requires the `force` function threaded to evaluator via [[eval-dependencies.language]]
- Enabled the [[risk-score-pattern.data]]: ESQL handles TOP-N aggregation, piescript handles user-defined computation over the resulting lists
- First case where the evaluator needs type information at runtime, bridging the [[elaboration-architecture.types]]/evaluation boundary

**Depends on**: [[f-omega-lite.types]], [[esql-aggregates.esql]], [[nbe-compilation.esql]]
**Enables**: [[multi-value-fields.data]]
**Connections**:
- part-of: [[block-f.roadmap]]
- uses: [[force-threading.types]] — `force` function threaded from `ElaborationState` to evaluator, no zonking pass
- informs: [[elaboration-architecture.types]] — first case where the evaluator needs type information at runtime, bridging the elaboration/evaluation boundary
- informs: [[esql-value-converter.esql]] — converter uses type-driven materialization to decide ListVal vs scalar
- uses: [[eval-dependencies.language]] — force function threaded via EvalDependencies
- validates: [[risk-score-pattern.data]] — risk score pattern is the motivating use case
