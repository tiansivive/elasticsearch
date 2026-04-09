---
tags: [types, unification, implemented, concept]
refs:
  - adr:D-005
  - adr:D-019
  - code:Unifier.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Unification Algorithm

Robinson unification with occurs check:
- [[null-as-bottom.types]] special case (D-007)
- Leijen-style open-[[row-polymorphism.types]] unification (D-030) with tail solving
- Returns `Optional<TypeError>` (see [[type-errors.types]])
- [[kind-system.types]] unification reuses the same algorithm (D-053)
- First-order only -- [[higher-order-unification.types]] deliberately avoided

**Depends on**: [[hindley-milner.types]]
**Enables**: [[row-polymorphism.types]], [[kind-system.types]], [[deferred-constraints.types]]
**Connections**:
- uses: [[kind-system.types]] — same unifier solves type constraints AND kind constraints (D-053)
- contrasts-with: [[higher-order-unification.types]] — deliberately NOT chosen
- uses: [[meta-variables.types]] — metas are the holes that unification solves
- constrains: [[rigid-variables.types]] — unification rejects rigid-vs-anything-else mismatches
- specializes: [[null-as-bottom.types]] — Null unifies with every type (special case, D-007)
- constrains: [[recursive-types.types]] — occurs check in the unifier blocks recursive types
- extends: [[schema-permutation.types]] — row unification must handle field order invariance
- produces: [[type-errors.types]] — unification failures surface as TypeError variants
- uses: [[row-polymorphism.types]] — Leijen-style open-row unification with tail solving
