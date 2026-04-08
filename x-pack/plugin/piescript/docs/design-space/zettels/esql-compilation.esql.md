---
tags: [esql, implemented, performance, compilation]
refs:
  - adr:D-052
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# ESQL Compilation

T-LINQ-style ESQL query compilation. `query expr ;` syntax replaces old backtick ESQL. `ESQL r` type constructor where `r` is row-kinded. Piescript closures serve as implicit quotations — the ESQL compiler inspects `ClosureVal(body, env)` to produce ESQL strings. Environment-based compilation, no substitutions.

**Depends on**: [[row-polymorphism.types]], [[rowtype-as-monotype.types]], [[nbe-compilation.esql]], [[use-declarations.data]]
**Enables**: [[esql-combinators.esql]], [[esql-aggregates.esql]]
**Connections**:
- related: [[t-linq.esql]] — draws from Cheney, Lindley & Wadler's T-LINQ (ICFP 2013)
- related: [[logical-plan-compilation.esql]] — string compilation is MVP; LogicalPlan compilation is the next step
- supersedes: [[query-syntax-evolution.esql]] — the T-LINQ approach replaced backtick syntax
