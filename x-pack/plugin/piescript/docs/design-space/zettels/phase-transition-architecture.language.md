---
tags: [language, performance, design-pattern, pattern, open, concept]
refs:
  - doc:architecture.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Phase Transition Architecture

The [[evaluator.language|evaluator's]] planned evolution:

- **Block A**: eager interpretation via `ActionListener` [[cps-evaluation.language|CPS]].
- **Block D+**: partial evaluator produces [[free-monad.types|free monad]] residual -> optimizer transforms it -> runtime interpreter executes optimized residual.
- Analogous to GHC Core->STG->Cmm or ESQL LogicalPlan->PhysicalPlan->OperatorPipeline.
- The Block A runtime interpreter becomes the backend -- nothing is thrown away, the [[lowering-pass.performance|lowering pass]] is additive.

**Depends on**: [[evaluator.language]], [[free-monad.types]], [[lowering-pass.performance]]
**Enables**: [[push-down-compilation.performance]], [[combinator-fusion.performance]]
**Connections**:
- uses: [[nbe-dual-pattern.types]] — NbE at both type and value level is the compilation pattern throughout
- uses: [[cps-evaluation.language]] — Block A's CPS interpreter is the runtime backend
- implements: [[partial-evaluation-lowering.performance]] — partial evaluation producing a residual is the phase transition itself
