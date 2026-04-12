---
tags: [performance, theoretical, lowering]
refs:
  - doc:architecture.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Lowering Pass

Future: [[evaluator.language]] splits into partial evaluator (produces [[free-monad.types]] residual) then optimizer ([[push-down-compilation.performance]], [[combinator-fusion.performance]], dead-branch elimination) then runtime interpreter. Analogous to GHC Core to STG to Cmm or ESQL Logical Plan to Physical Plan to Operator Pipeline.

**Depends on**: [[free-monad.types]]
**Enables**: [[push-down-compilation.performance]], [[combinator-fusion.performance]]
**Connections**:
- related: [[evaluator.language]] — runtime interpreter from Block A becomes the backend; additive change, nothing thrown away
- prerequisite-for: [[bytecode-compilation.performance]] — bytecode compilation is a possible target after lowering
- part-of: [[phase-transition-architecture.language]] — the evaluator evolution this lowering pass is part of
