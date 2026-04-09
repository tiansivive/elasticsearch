---
tags: [types, implemented, polymorphism, inference, decision, concept]
refs:
  - adr:D-005
  - adr:D-036
  - code:piescript.elab
  - code:Elaborator.java
  - code:Unifier.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Hindley-Milner Type Inference

Bidirectional Hindley-Milner type inference with [[zonker.types]]-based [[elaboration-architecture.types]]. The surface language requires no [[type-annotations.types]] -- types are fully inferred.
- Checking mode propagates expected types inward for lambdas, records, let/block bodies, and ascription (D-036, partially implemented) -- see [[bidir-checking.types]]
- The elaborator is a pattern-matching recursive descent over the [[antlr-grammar.language]] CST producing [[core-ir.language]]

**Depends on**: (none)
**Enables**: [[system-f-core.types]], [[row-polymorphism.types]], [[zonker.types]], [[deferred-constraints.types]]
**Connections**:
- part-of: [[phase-1.roadmap]]
- inspired-by: Dunfield & Krishnaswami (2013) is the reference
- complements: [[bidir-checking.types]] — checking mode partially implemented (D-036)
- informs: [[elaboration-architecture.types]] — immutable context / mutable state split implements HM elaboration
- uses: [[meta-variables.types]] — metas are the core mechanism for HM inference
- uses: [[binding-levels.types]] — level-based let-generalization
- uses: [[type-errors.types]] — unification failures during HM produce TypeError variants
