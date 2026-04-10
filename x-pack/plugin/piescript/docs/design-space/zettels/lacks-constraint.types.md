---
tags: [types, row-types, typeclasses, open, concept, needs-design, later]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:type-foundations
---
# Lacks Constraint

Row constraint `Lacks` for precise Pick/Omit encoding (ensures field absence). A `Lacks r l` constraint asserts that row `r` does not contain label `l`, enabling type-safe record extension: you can only add a field if it's guaranteed absent. This is the missing piece for fully precise [[row-polymorphism.types]] record operations.

- Requires [[typeclasses.types]] for constraint dispatch
- Refines [[row-operators.types]] `&`, `Pick`, `Omit` with field-absence proofs

**Depends on**: [[row-operators.types]], [[typeclasses.types]]
**Enables**: type-safe record extension guarantees
**Connections**:
- refines: [[row-polymorphism.types]] — Lacks is a refinement of open-row unification
- complements: [[concrete-row-constraints.types]] — both mechanisms constrain rows; Lacks is about absence, concrete-row is about conflict detection
