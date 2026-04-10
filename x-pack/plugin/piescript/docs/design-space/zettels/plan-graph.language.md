---
tags: [language, superseded, concept]
refs:
  - adr:D-012
  - adr:D-040
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Plan Graph

The [[evaluator.language|evaluator]] builds a plan graph (DAG) of distributed operations -- a [[free-monad.types|free monad]] over pi-calculus effects. Optimized then dispatched. Superseded by D-040: direct interpretation via [[join-calculus.coordination|Join Calculus]] [[channels.infrastructure|channels]]. The free monad perspective is preserved theoretically.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- replaced-by: [[join-calculus.coordination]] — optimization benefits required significant compiler engineering not immediately justified
- inspired-by: [[free-monad.types]] — the plan graph was a free monad over pi-calculus effects
- alternative-to: [[cps-evaluation.language]] — direct interpretation via CPS replaced the plan-then-optimize model
