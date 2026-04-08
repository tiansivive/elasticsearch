---
tags: [types, theoretical, effects, pi-calculus]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Algebraic Effects

Plotkin & Pretnar (2009): effects modeled through equational theories with primitive operations. Handlers yield models of these theories. The separation of effect description from effect interpretation is exactly piescript's architecture: coordination primitives describe effects, the evaluator (or future optimizer) interprets them.

**Depends on**: (none)
**Enables**: [[free-monad.types]], [[effect-systems.types]]
**Connections**:
- related: Wu & Schrijvers (2015) on fusing effect handlers — relevant to optimizer fusing coordination operations
