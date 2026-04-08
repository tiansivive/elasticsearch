---
tags: [types, implemented, polymorphism, inference]
refs:
  - adr:D-005
  - adr:D-036
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Hindley-Milner Type Inference

Bidirectional Hindley-Milner type inference with zonker-based elaboration. The surface language requires no type annotations — types are fully inferred. Checking mode propagates expected types inward for lambdas, records, let/block bodies, and ascription (D-036, partially implemented). The elaborator is a pattern-matching recursive descent over the ANTLR CST producing Core IR.

**Depends on**: (none)
**Enables**: [[system-f-core.types]], [[row-polymorphism.types]], [[zonker.types]], [[deferred-constraints.types]]
**Connections**:
- related: Dunfield & Krishnaswami (2013) is the reference
- related: [[bidir-checking.types]] — checking mode partially implemented (D-036)
- related: [[elaboration-architecture.types]] — immutable context / mutable state split implements HM elaboration
- related: [[meta-variables.types]] — metas are the core mechanism for HM inference
- related: [[binding-levels.types]] — level-based let-generalization
- related: [[type-errors.types]] — unification failures during HM produce TypeError variants
