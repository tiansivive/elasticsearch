---
tags: [esql, nbe, compilation, implemented, concept, pattern]
refs:
  - adr:D-052
  - code:Value.java
  - code:Evaluator.java
  - code:EvalPrimOps.java
---
# Symbol Partial Evaluation

`Value.Symbol(String)` is the [[nbe-dual-pattern.types|NbE]] neutral form for [[esql-compilation.esql|ESQL compilation]].

- When a [[closure-val.language|closure]] is partially evaluated with a symbolic row, projections produce `Symbol("field_name")`.
- Primops over Symbol operands produce `Symbol("(left OP right)")` -- stuck terms whose string payload IS the compiled ESQL fragment.
- The [[evaluator.language|evaluator]] IS the compiler: no separate compilation pass exists.
- Read-back is trivial -- the Symbol's string is the ESQL output.

**Depends on**: [[nbe-compilation.esql]], [[nbe-dual-pattern.types]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-f.roadmap]]
- implements: [[t-linq.esql]] -- Symbol partial evaluation is piescript's realization of T-LINQ quotation + normalization
- part-of: [[esql-compilation.esql]] -- Symbol is the core mechanism inside the ESQL compilation pipeline
- uses: [[evaluator.language]] -- the standard evaluator drives partial evaluation; no special-purpose compiler needed
