---
tags: [thread, roadmap, data, esql]
refs: []
---
# Data Completeness

From finishing Block G tests to handling every ES data type correctly. This thread
covers the gaps between piescript's current data handling and what real-world ES
indices require — timestamps, multi-value fields, proper string handling, ESQL
coverage, and infrastructure hardening.

## Sequence

1. **Block G integration tests** [[block-g.roadmap]] — resolved
   Exchange builtins implemented, integration tests added for local and cross-node streaming.

2. **Empty mapping diagnostics** [[empty-mapping-diagnostics.data]] — ready
   Emit diagnostic when field caps returns no usable fields instead of
   silently producing `List { }`.

3. **Column name derivation** [[column-name-derivation.types]] — later
   Derive column names from row type at elaboration time instead of
   runtime `List Keyword`.

4. **Circuit breaker integration** [[circuit-breaker.infrastructure]] — later
   `Shard.stream` uses `NoopCircuitBreaker`; production needs real breakers.

5. **DateTime type** [[datetime.types]] — needs-design
   `DateTime` type constructor, epoch millis, formatting builtins, temporal
   arithmetic. Most ES indices have timestamp fields.

6. **Block H: MV field semantics** [[block-h.roadmap]] — needs-design
   Multi-value fields as first-class values. Scalar pervasion, `Single a`
   boxing, `MV.*` builtins. Designed but not implemented.
   Subsumes: [[multi-value-fields.data]], [[scalar-pervasion.data]],
   [[single-a-boxing.types]], [[mv-scalar-dispatch.data]]

7. **ESQL.topBy** [[esql-topby.esql]] — needs-design
   Correlated TOP with output fields. Depends on Block H for `MapList`
   row operator.

8. **Keyword/String unification** [[keyword-string.types]] — needs-design
   Whether `text` maps to `Keyword`, are they different types.

9. **KeywordVal BytesRef conversion** [[keyword-bytesref.types]] — later
   Reverse conversion when piescript values flow into ESQL parameters.

10. **ESQL body parser cleanup** [[esql-body-parser.infrastructure]] — later
    Double parse and opaque ESQL_BODY token. Goes away with structured grammar.

11. **ESQL.join** [[join.esql]] — needs-design
    LOOKUP JOIN. Complex cross-index typing.

12. **LogicalPlan compilation** [[logical-plan-compilation.esql]] — needs-design
    Compile to ESQL internal plan IR instead of strings.

13. **Numeric precision** [[numeric-precision.types]] — someday
    Is Double-only still right? Revisit with typeclasses (numeric tower).

**Depends on**: (none — root thread)
**Enables**: (none directly)
**Connections**:
- includes: [[block-g.roadmap]]
- includes: [[empty-mapping-diagnostics.data]]
- includes: [[column-name-derivation.types]]
- includes: [[circuit-breaker.infrastructure]]
- includes: [[datetime.types]]
- includes: [[block-h.roadmap]]
- includes: [[multi-value-fields.data]]
- includes: [[esql-topby.esql]]
- includes: [[keyword-string.types]]
- includes: [[keyword-bytesref.types]]
- includes: [[esql-body-parser.infrastructure]]
- includes: [[join.esql]]
- includes: [[logical-plan-compilation.esql]]
- includes: [[numeric-precision.types]]
