---
tags: [language, performance, design-pattern, open]
refs:
  - doc:architecture.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Phase Transition Architecture

The evaluator's planned evolution: Block A (eager interpretation via ActionListener CPS) -> Block D+ (partial evaluator produces free monad residual -> optimizer transforms it -> runtime interpreter executes optimized residual). Analogous to GHC Core->STG->Cmm or ESQL LogicalPlan->PhysicalPlan->OperatorPipeline. The Block A runtime interpreter becomes the backend — nothing is thrown away, the lowering pass is additive.

**Depends on**: [[evaluator.language]], [[free-monad.types]], [[lowering-pass.performance]]
**Enables**: [[push-down-compilation.performance]], [[combinator-fusion.performance]]
**Connections**:
- related: [[nbe-dual-pattern.types]] — NbE at both type and value level is the compilation pattern throughout
