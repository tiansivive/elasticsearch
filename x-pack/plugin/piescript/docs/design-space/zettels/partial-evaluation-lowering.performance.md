---
tags: [performance, nbe, compilation, concept]
refs:
  - doc:architecture.md
---
# Partial Evaluation as Lowering

- Pure code (arithmetic, record operations, closures) reduces to values; coordination operations ([[spawn.coordination|spawn]], [[when-synchronization.coordination|when]], query, [[send.coordination|send]]) get stuck because they require runtime effects.
- The stuck residual -- a tree of irreducible effect operations with attached closures and reduced arguments -- IS the lowered IR.
- This bridges the [[free-monad.types|free monad]] as a data structure and the [[lowering-pass.performance|lowering pass]] as an optimization stage: partial evaluation does not merely precede lowering, it *is* the act of lowering.
- The optimizer then transforms the residual ([[push-down-compilation.performance|push-down]], [[combinator-fusion.performance|fusion]], dead-branch elimination) before the runtime interpreter executes it.

**Depends on**: [[free-monad.types]], [[nbe-compilation.esql]]
**Enables**: [[lowering-pass.performance]]
**Connections**:
- implements: [[phase-transition-architecture.language]] — partial evaluation producing a residual is the phase transition from evaluation to optimization
- analogous-to: [[nbe-dual-pattern.types]] — NbE's evaluate-then-readback is the same structure: reduce what you can, quote what you can't
- related: [[symbol-partial-evaluation.esql]] — Symbol partial evaluation is the concrete mechanism for ESQL compilation via this pattern
