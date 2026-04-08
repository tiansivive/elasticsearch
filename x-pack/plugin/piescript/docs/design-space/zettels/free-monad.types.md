---
tags: [types, theoretical, coordination, design-pattern, effects]
refs:
  - adr:D-012
  - adr:D-040
  - doc:architecture.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Free Monad

The coordination primitives form an algebraic effect signature. The evaluator is an effect handler. The residual of partial evaluation — a tree of irreducible spawn/when/query operations with attached closures — is `Free JoinF Value`. In Block A, the evaluator eagerly interprets this (CPS). Future lowering pass materializes it for optimization.

**Depends on**: [[join-calculus.coordination]], [[purity.language]]
**Enables**: [[lowering-pass.performance]]
**Connections**:
- related: not eliminated by D-040 — preserved as the theoretical perspective; the free monad arises naturally from partial evaluation
- related: [[effect-handlers.types]] — the evaluator IS an effect handler interpreting the free monad
- related: [[effect-systems.types]] — coordination primitives form the effect signature
