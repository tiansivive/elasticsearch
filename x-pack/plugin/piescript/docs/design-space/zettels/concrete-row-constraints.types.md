---
tags: [types, data, row-types, implemented, concept]
refs:
  - code:Unifier.java
  - code:RowType.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Concrete-Row Constraints

Novel mechanism for cross-index type conflict detection.

- When [[use-declarations.data]] resolves [[field-caps-resolution.data]], the resulting [[row-polymorphism.types]] is "concrete" — it represents actual index mappings, not arbitrary type expressions
- During [[unification-algorithm.types]], the constraint solver detects when two concrete rows from different indices have conflicting field types for the same field name (via `InvalidMappedField` from field caps)
- Implementation simplification: uses merged mapping directly with conflicts surfacing as `TypeError.MissingFields` rather than a dedicated `IndexConflict` variant
- Per-index conflict diagnostics (telling the user *which* index has the conflicting type) are deferred

**Depends on**: [[field-caps-resolution.data]], [[row-polymorphism.types]]
**Enables**: [[use-declarations.data]]
**Connections**:
- part-of: [[phase-2.roadmap]]
- tension-with: [[type-errors.types]] — "missing field" errors when the actual issue is type conflict are confusing; better diagnostics require tracking provenance of row fields through unification
- uses: [[unification-algorithm.types]] — conflicts detected during row unification
- contrasts-with: [[lacks-constraint.types]] — both constrain rows; Lacks ensures absence, concrete-row detects mapping conflicts
