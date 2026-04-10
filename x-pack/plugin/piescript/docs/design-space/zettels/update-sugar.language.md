---
tags: [syntax, language, row-types, implemented, documentation]
refs:
  - adr:D-021
  - adr:D-029
  - code:Records.java
  - code:piescript.elab
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Update Sugar

`{ r | field = val }` desugars to a lambda that creates a new record with the updated field:
- Originally used closed-row constraints (D-021) -- only worked on records with exactly the referenced fields
- Superseded by open-row sugar in Phase 1d (D-029, D-030)
- Now works with records containing extra fields via open-row parameter type `{ field: a | r }` using [[row-polymorphism.types]]

**Depends on**: [[row-polymorphism.types]]
**Enables**: (none directly)
**Connections**:
- analogous-to: [[accessor-sugar.language]] — same closed->open evolution
- replaces: D-021 closed-row update sugar — open-row sugar now handles records with extra fields
- uses: [[row-polymorphism.types]] — open-row parameter type enables polymorphic update
- extended-by: [[record-spread.language]] — `...` spread syntax extends update with multi-record merging
