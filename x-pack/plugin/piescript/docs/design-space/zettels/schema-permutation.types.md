---
tags: [types, row-types, open]
refs:
  - adr:D-030
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Schema Permutation

Field order invariance in record type unification. When unifying two row types, the order of fields should not matter: `{ a: Int, b: String }` must unify with `{ b: String, a: Int }`. This requires the unification algorithm to handle row permutation, which interacts with how rows are represented and extended.

**Depends on**: [[row-polymorphism.types]], [[unification-algorithm.types]]
**Enables**: (none)
**Connections**:
- related: [[dotted-field-paths.esql]] — ESQL field order may differ from elaboration order
