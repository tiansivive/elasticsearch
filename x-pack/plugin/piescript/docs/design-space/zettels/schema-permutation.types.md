---
tags: [types, row-types, open, concept, question]
refs:
  - adr:D-030
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Schema Permutation

Field order invariance in [[record-type.language|record type]] [[unification-algorithm.types|unification]]. When unifying two [[row-polymorphism.types|row types]], the order of fields should not matter: `{ a: Int, b: String }` must unify with `{ b: String, a: Int }`. This requires the unification algorithm to handle row permutation, which interacts with how rows are represented and extended.

**Depends on**: [[row-polymorphism.types]], [[unification-algorithm.types]]
**Enables**: (none)
**Connections**:
- tension-with: [[dotted-field-paths.esql]] — ESQL field order may differ from elaboration order
- tension-with: [[recordval-map-inconsistency.runtime]] — Map.of vs LinkedHashMap inconsistency makes field order unpredictable
