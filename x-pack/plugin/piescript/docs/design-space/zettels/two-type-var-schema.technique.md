---
tags: [types, esql, row-types, technique, implemented, concept, inference, nbe]
refs:
  - adr:D-052
  - adr:D-053
  - code:EvalBuiltins.java
  - code:Prelude.java
---
# Two-Type-Variable Schema Pattern

A recurring pattern in schema-changing ESQL combinators: `forall (r : Row) (s : Row). ...` where `r` is the input row and `s` is a fresh output row, constrained not by the combinator's signature but by downstream usage. The combinator's type says "input has shape r, output has shape s" without specifying the relationship between r and s -- that relationship emerges from how the closure body uses r to produce s.

Used by:
- `ESQL.eval : (Record r -> Record s) -> ESQL r -> ESQL s` -- eval adds computed columns; s is r plus new fields
- `ESQL.keep : (Record r -> Record s) -> ESQL r -> ESQL s` -- keep selects columns; s is `Pick r fields`
- `ESQL.drop : (Record r -> Record s) -> ESQL r -> ESQL s` -- drop removes columns; s is `Omit r fields`

The key insight: s starts as an unsolved meta. When the closure body projects fields from r (producing Symbols) and constructs a new record, unification solves s to the concrete output row type. For keep/drop, the `Pick`/`Omit` row operators in `force` compute s from r and the projected fields. No explicit constraint between r and s is needed -- the closure body IS the constraint.

Contrast with schema-preserving combinators (where, sort, limit) which use a single row variable: `forall (r : Row). ... ESQL r -> ESQL r`.

**Depends on**: [[row-polymorphism.types]], [[deferred-constraints.types]], [[f-omega-lite.types]]
**Enables**: (none directly)
**Connections**:
- implements: [[esql-combinators.esql]] -- the schema-changing combinators (eval, keep, drop) use this pattern
- uses: [[row-polymorphism.types]] -- both r and s are row-kinded type variables
- uses: [[deferred-constraints.types]] -- s is solved by deferred unification, not eagerly
- informs: [[f-omega-lite.types]] -- the pattern motivated Pick/Omit as reducible row operators in force
- complements: [[closure-vs-string-columns.decision]] -- the two-var pattern is what makes closure-based keep/drop work
- uses: [[nbe-compilation.esql]] -- symbolic evaluation of the closure body determines the output row
- uses: [[symbol-partial-evaluation.esql]] -- projecting Symbol(r) fields in the closure body produces Symbol(field) values that constrain s
- uses: [[binding-levels.types]] -- s is generalized at the combinator's binding level; downstream usage solves it
- related: [[row-operators.types]] -- Pick/Omit reduce in force to compute the concrete s from r
