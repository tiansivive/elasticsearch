---
tags: [types, theoretical, coordination, design-pattern, pattern, effects, concept]
refs:
  - adr:D-012
  - adr:D-040
  - doc:architecture.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Free Monad

The coordination primitives form an [[algebraic-effects.types]] signature. The [[evaluator.language]] is an [[effect-handlers.types]].
- The residual of partial evaluation -- a tree of irreducible [[spawn.coordination]]/[[when-synchronization.coordination]]/query operations with attached [[closure-val.language]] -- is `Free JoinF Value`
- In Block A, the evaluator eagerly interprets this via [[cps-evaluation.language]]
- Future [[lowering-pass.performance]] materializes it for optimization

**Depends on**: [[join-calculus.coordination]], [[purity.language]]
**Enables**: [[lowering-pass.performance]]
**Connections**:
- related: [[cps-evaluation.language]] — the evaluator eagerly interprets the free monad via CPS
- complements: [[effect-handlers.types]] — the evaluator IS an effect handler interpreting the free monad
- related: [[effect-systems.types]] — coordination primitives form the effect signature
