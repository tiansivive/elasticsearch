---
tags: [types, implemented, documentation]
refs:
  - adr:D-005
  - code:TypeError.java
  - code:ElaborationException.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Type Errors

`TypeError` sealed interface with four variants:
- `Mismatch` -- structural incompatibility
- `InfiniteType` -- occurs check failure
- `FieldMismatch` -- row field type conflict
- `MissingFields` -- row missing required fields

`ElaborationException` wraps `TypeError` with source location. Errors reported at the exact point where [[unification-algorithm.types]] fails.

**Depends on**: [[hindley-milner.types]], [[row-polymorphism.types]]
**Enables**: (none directly)
**Connections**:
- tension-with: [[concrete-row-constraints.types]] — concrete-row constraints produce `MissingFields` where the real issue is cross-index type conflict
- tension-with: [[kind-system.types]] — kind errors surface as type mismatches (confusing); error quality is a known area for improvement
- uses: [[unification-algorithm.types]] — errors produced at the point where unification fails
- complements: [[error-provenance.language]] — source location available at elaboration time but lost at evaluation
