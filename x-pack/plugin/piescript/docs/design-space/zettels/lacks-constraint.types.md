---
tags: [types, row-types, typeclasses, open]
refs:
  - adr:D-053
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Lacks Constraint

Row constraint Lacks for precise Pick/Omit encoding (ensures field absence). A `Lacks r l` constraint asserts that row `r` does not contain label `l`, enabling type-safe record extension: you can only add a field if it's guaranteed absent. This is the missing piece for fully precise row-polymorphic record operations.

**Depends on**: [[row-operators.types]], [[typeclasses.types]]
**Enables**: type-safe record extension guarantees
**Connections**:
- informs: [[row-polymorphism.types]] — Lacks is a refinement of open-row unification
- related: [[concrete-row-constraints.types]] — both mechanisms constrain rows; Lacks is about absence, concrete-row is about conflict detection
