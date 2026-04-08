---
tags: [syntax, language, row-types, implemented]
refs:
  - adr:D-021
  - adr:D-029
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Update Sugar

`{ r | field = val }` desugars to a lambda that creates a new record with the updated field. Originally used closed-row constraints (D-021) — only worked on records with exactly the referenced fields. Superseded by open-row sugar in Phase 1d (D-029, D-030) — now works with records containing extra fields via open-row parameter type `{ field: α | ρ }`.

**Depends on**: [[row-polymorphism.types]]
**Enables**: (none directly)
**Connections**:
- related: [[accessor-sugar.language]] — same closed→open evolution
- supersedes: D-021 closed-row update sugar
