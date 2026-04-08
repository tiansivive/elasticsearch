---
tags: [types, unification, implemented]
refs:
  - adr:D-005
  - adr:D-019
  - code:Unifier.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Unification Algorithm

Robinson unification with occurs check. Null-as-bottom special case (D-007). Leijen-style open-row unification (D-030) with tail solving. Returns `Optional<TypeError>`. Kind unification reuses the same algorithm (D-053). First-order only -- higher-order unification deliberately avoided.

**Depends on**: [[hindley-milner.types]]
**Enables**: [[row-polymorphism.types]], [[kind-system.types]], [[deferred-constraints.types]]
**Connections**:
- related: [[kind-system.types]] — same unifier solves type constraints AND kind constraints (D-053)
- contrasts-with: [[higher-order-unification.types]] — deliberately NOT chosen
- related: [[meta-variables.types]] — metas are the holes that unification solves
- related: [[rigid-variables.types]] — unification rejects rigid-vs-anything-else mismatches
- related: [[null-as-bottom.types]] — Null unifies with every type (special case, D-007)
- related: [[recursive-types.types]] — occurs check in the unifier blocks recursive types
- related: [[schema-permutation.types]] — row unification must handle field order invariance
