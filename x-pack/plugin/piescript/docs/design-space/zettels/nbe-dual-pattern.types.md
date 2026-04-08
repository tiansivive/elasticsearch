---
tags: [types, esql, theoretical, compilation, implemented, nbe, design-pattern]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# NbE Dual Pattern

NbE (Normalization by Evaluation) appears at two levels in piescript, following the exact same evaluate-then-read-back structure: (1) **Type-level**: `ElaborationState.force` evaluates types into a semantic domain (normal forms + stuck terms), reducing built-in operators (`&`, `Pick`, `Omit`) inline. (2) **Value-level**: `Symbol(String)` partial evaluation for ESQL compilation — closures evaluated with symbolic rows, projections and primops read back as ESQL fragments. Both use the same pattern: evaluate into a domain, get stuck on unknowns, read back into the target syntax.

**Depends on**: [[f-omega-lite.types]], [[nbe-compilation.esql]]
**Enables**: (none directly)
**Connections**:
- related: [[f-omega-lite.types]] — new type operators add cases to `force`
- related: [[nbe-compilation.esql]] — new ESQL commands add cases to `Symbol` handling
- related: core architectural insight — the pattern is infinitely extensible without changing the framework
- related: [[force-threading.types]] — force is the type-level NbE; threaded to evaluator via EvalDependencies
- related: [[row-operators.types]] — &, Pick, Omit are reducible builtins in force
