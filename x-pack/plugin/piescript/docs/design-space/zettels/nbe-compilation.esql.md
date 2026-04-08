---
tags: [esql, implemented, performance, compilation, nbe]
refs:
  - adr:D-052
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# NbE Compilation

NbE-style approach: `Value.Symbol(String esql)` carries compiled ESQL fragments built incrementally during evaluation. Closures partially evaluated with `Symbol("")` as symbolic row. `CoreProject` on `Symbol` produces `Symbol(field)`. `PrimOp` with `Symbol` operands compiles to `Symbol("(left OP right)")`. The evaluator IS the compiler.

**Depends on**: [[evaluator.language]]
**Enables**: [[esql-compilation.esql]]
**Connections**:
- related: [[nbe-dual-pattern.types]] — follows the NbE pattern: evaluate into semantic domain, read back into target syntax
- related: [[free-monad.types]] — `Symbol` accumulates description, interpreted at query boundary
- related: [[esql-combinators.esql]] — combinators use NbE-compiled Symbols
- related: [[t-linq.esql]] — T-LINQ is the theoretical foundation for NbE query compilation
