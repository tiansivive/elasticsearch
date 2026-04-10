---
tags: [types, esql, theoretical, compilation, implemented, nbe, design-pattern, pattern, concept]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:ElaborationState.java
  - code:Value.java
---
# NbE Dual Pattern

NbE (Normalization by Evaluation) appears at two levels in piescript, following the exact same evaluate-then-read-back structure:

1. **Type-level**: `ElaborationState.force` evaluates types into a semantic domain (normal forms + stuck terms), reducing built-in [[row-operators.types]] (`&`, `Pick`, `Omit`) inline
2. **Value-level**: `Symbol(String)` partial evaluation for [[esql-compilation.esql]] -- closures evaluated with symbolic rows, projections and primops read back as ESQL fragments

Both use the same pattern: evaluate into a domain, get stuck on unknowns, read back into the target syntax. The pattern is infinitely extensible without changing the framework.

**Depends on**: [[f-omega-lite.types]], [[nbe-compilation.esql]]
**Enables**: (none directly)
**Connections**:
- informs: [[f-omega-lite.types]] — new type operators add cases to `force`
- informs: [[nbe-compilation.esql]] — new ESQL commands add cases to `Symbol` handling
- complements: [[force-threading.types]] — force is the type-level NbE; threaded to evaluator via [[eval-dependencies.language]]
- subsumes: [[row-operators.types]] — &, Pick, Omit are reducible builtins in force
- uses: [[symbol-partial-evaluation.esql]] — value-level NbE implemented via Symbol partial evaluation
- analogous-to: [[type-level-matching.types]] — value-level `CoreMatch` and type-level `force` cases are the same NbE structure at different levels
