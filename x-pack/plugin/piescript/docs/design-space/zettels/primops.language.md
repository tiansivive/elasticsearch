---
tags: [language, syntax, operator, implemented, concept, evaluation]
refs:
  - adr:D-020
  - code:EvalPrimOps.java
  - code:CorePrimOp.java
  - code:PiescriptAntlrParser.g4
---
# Primitive Operators

Built-in operator set: `+`, `-`, `*`, `/`, `%`, `==`, `!=`, `<`, `>`, `<=`, `>=`, `&&`, `||`,
`!`, unary `-`. Implemented as `CorePrimOp` variants in [[core-ir.language]]. Ad-hoc overloading
via the compiler now; [[typeclasses.types|typeclasses]] later (Num, Eq, Ord). Infix by default
in surface syntax. All arithmetic currently typed as `Double -> Double -> Double` via the
[[unified-double.types]] decision (D-020). Boolean operators are `Boolean -> Boolean -> Boolean`.
Comparison operators return `Boolean`.

Dispatch happens in `EvalPrimOps` -- a flat switch over the `CorePrimOp.Op` enum. No runtime
type information; the [[evaluator-trusts-typechecker.language|evaluator trusts the type checker]]
and casts directly.

**Depends on**: [[core-ir.language]], [[unified-double.types]]
**Enables**: [[prelude.language]]
**Connections**:
- part-of: [[syntax.hub]]
- uses: [[antlr-grammar.language]] -- operator tokens and precedence levels defined in the ANTLR grammar
- informs: [[typeclasses.types]] -- principled Num/Eq/Ord typeclasses will replace current ad-hoc overloading
- implements: [[evaluator.language]] -- EvalPrimOps is a dispatch helper for the main evaluator
- uses: [[unified-double.types]] -- all arithmetic operates on Double (D-020)
- uses: [[precedence.language]] -- operators have defined precedence levels
- informs: [[typeclass-instances.types]] -- Num, Eq, Ord instances will eventually back these operators
- informs: [[scalar-pervasion.data]] -- primops are the scalar functions that will pervasively lift over MV fields
