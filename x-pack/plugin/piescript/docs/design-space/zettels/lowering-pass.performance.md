---
tags: [performance, theoretical]
refs:
  - doc:architecture.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Lowering Pass

Future: evaluator splits into partial evaluator (produces free monad residual) → optimizer (push-down, fusion, dead-branch elimination) → runtime interpreter. Analogous to GHC Core → STG → Cmm or ESQL Logical Plan → Physical Plan → Operator Pipeline.

**Depends on**: [[free-monad.types]]
**Enables**: [[push-down-compilation.performance]], [[combinator-fusion.performance]]
**Connections**:
- related: [[evaluator.language]] — runtime interpreter from Block A becomes the backend; additive change, nothing thrown away
- related: [[bytecode-compilation.performance]] — bytecode compilation is a possible target after lowering
- related: [[phase-transition-architecture.language]] — the evaluator evolution this lowering pass is part of
