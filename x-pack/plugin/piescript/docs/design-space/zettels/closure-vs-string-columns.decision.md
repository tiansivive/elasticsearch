---
tags: [esql, types, row-types, decided, nbe, concept, inference]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Closure vs String Columns for ESQL Keep/Drop

A design tension existed between two approaches for `ESQL.keep` and `ESQL.drop`:

1. **List Keyword** -- pass column names as runtime strings (`ESQL.keep ["name", "age"] pipeline`). Simple but no type checking: the type system cannot verify that named columns exist in the input row, and the output row type is unknown until runtime.

2. **Closure-based** -- pass a record projection function (`ESQL.keep (fn r -> { name: r.name, age: r.age }) pipeline`). Type-safe but requires the output row type to be expressible. Initially blocked because the output row variable `s` was unconstrained.

Resolved by the F-omega-lite type system (D-053): `Pick` and `Omit` row operators compute the output row type from the input row and the projection. `ESQL.keep` now takes a closure `(Record r -> Record s)` where `s` is constrained to `Pick r fields` by the row operator in `force`. The closure approach gives full type safety; NbE compilation extracts field names from the symbolic evaluation of the closure body.

**Depends on**: [[row-operators.types]], [[f-omega-lite.types]]
**Enables**: (none directly)
**Connections**:
- resolved-by: [[row-operators.types]] -- Pick/Omit operators compute output row types
- resolved-by: [[f-omega-lite.types]] -- force normalizer reduces Pick/Omit when rows are concrete
- implements: [[esql-combinators.esql]] -- keep/drop design using closure-based approach with row operators
- informs: [[force-threading.types]] -- force must reduce Pick/Omit for keep/drop to type-check
- uses: [[nbe-compilation.esql]] -- NbE extracts field names from symbolic closure evaluation
- uses: [[two-type-var-schema.technique]] -- keep/drop use the two-row-variable pattern (input r, output s)
- related: [[symbol-partial-evaluation.esql]] -- symbolic evaluation of the closure body produces the ESQL KEEP/DROP column list
