---
tags: [esql, implemented, performance, compilation, concept]
refs:
  - adr:D-052
  - code:EvalBuiltins.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL Compilation

[[t-linq.esql]]-style ESQL query compilation.
- `query expr ;` syntax replaces old backtick ESQL
- `ESQL r` type constructor where `r` is row-kinded via [[row-polymorphism.types]]
- Piescript [[closure-val.language]] serve as implicit quotations -- the ESQL compiler inspects `ClosureVal(body, env)` to produce ESQL strings
- Environment-based compilation via [[nbe-compilation.esql]], no substitutions

**Depends on**: [[row-polymorphism.types]], [[rowtype-as-monotype.types]], [[nbe-compilation.esql]], [[use-declarations.data]], [[closure-val.language]]
**Enables**: [[esql-combinators.esql]], [[esql-aggregates.esql]]
**Connections**:
- part-of: [[block-f.roadmap]]
- inspired-by: [[t-linq.esql]] — draws from Cheney, Lindley & Wadler's T-LINQ (ICFP 2013)
- prerequisite-for: [[logical-plan-compilation.esql]] — string compilation is MVP; LogicalPlan compilation is the next step
- replaces: [[query-syntax-evolution.esql]] — the T-LINQ approach replaced backtick syntax
