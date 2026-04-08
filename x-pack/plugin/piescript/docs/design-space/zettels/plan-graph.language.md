---
tags: [language, superseded]
refs:
  - adr:D-012
  - adr:D-040
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Plan Graph

The evaluator builds a plan graph (DAG) of distributed operations — a free monad over π-calculus effects. Optimized then dispatched. Superseded by D-040: direct interpretation via Join Calculus channels. The free monad perspective is preserved theoretically.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- superseded-by: [[join-calculus.coordination]] — optimization benefits required significant compiler engineering not immediately justified
