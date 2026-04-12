---
tags: [types, theoretical, effects, pi-calculus]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Algebraic Effects

Plotkin & Pretnar (2009): effects modeled through equational theories with primitive operations. Handlers yield models of these theories. The separation of effect description from effect interpretation is exactly piescript's architecture: coordination primitives describe effects, the [[evaluator.language]] (or future optimizer) interprets them.

**Depends on**: (none)
**Enables**: [[free-monad.types]], [[effect-systems.types]]
**Connections**:
- part-of: [[future-type-system.roadmap]]
- informs: [[evaluator.language]] — piescript's evaluator is an effect handler: coordination primitives are effect operations, the evaluator interprets them
- related: [[combinator-fusion.performance]] — Wu & Schrijvers (2015) on fusing effect handlers is relevant to optimizer fusing coordination operations
- specializes: [[delimited-continuations.hub]] — effects are structured sugar over shift/reset
- uses: [[answer-type-polymorphism.types]] — effect handler typing needs answer-type polymorphism
- cites: [[plotkin-pretnar-handlers.paper]], [[wu-schrijvers-fusion.paper]]
