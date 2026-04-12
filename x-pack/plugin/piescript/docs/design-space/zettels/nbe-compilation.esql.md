---
tags: [esql, implemented, performance, compilation, nbe, decision, concept]
refs:
  - adr:D-052
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Value.java
  - code:EvalBuiltins.java
---
# NbE Compilation

NbE-style approach: `Value.Symbol(String esql)` carries compiled ESQL fragments built incrementally during [[evaluator.language]] evaluation. Closures partially evaluated with `Symbol("")` as symbolic row. [[core-ir.language]] `CoreProject` on `Symbol` produces `Symbol(field)`. `PrimOp` with `Symbol` operands compiles to `Symbol("(left OP right)")`. The evaluator IS the compiler.

- Follows the [[nbe-dual-pattern.types]] -- evaluate into semantic domain, read back into target syntax
- [[symbol-partial-evaluation.esql]] is the concrete implementation mechanism
- [[esql-combinators.esql]] use NbE-compiled Symbols for query construction

**Depends on**: [[evaluator.language]]
**Enables**: [[esql-compilation.esql]]
**Connections**:
- part-of: [[block-f.roadmap]]
- example-of: [[nbe-dual-pattern.types]] — follows the NbE pattern: evaluate into semantic domain, read back into target syntax
- related: [[free-monad.types]] — `Symbol` accumulates description, interpreted at query boundary
- complements: [[esql-combinators.esql]] — combinators use NbE-compiled Symbols
- uses: [[symbol-partial-evaluation.esql]] — Symbol is the concrete carrier for partial evaluation fragments
- implements: [[t-linq.esql]] — T-LINQ is the theoretical foundation for NbE query compilation
- replaces: [[esqlplan-compiler.rejected]] — NbE Symbol-based compilation replaced the EsqlPlan/EsqlCompiler design
