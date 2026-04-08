---
tags: [language, syntax, row-types, implemented]
refs:
  - adr:D-021
  - adr:D-029
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Accessor Sugar

`.field` desugars to `fn $acc -> $acc.field` with an open-row parameter type `{ field: b | r }`. Originally used closed-row constraints (D-021) which only worked on records with exactly the referenced fields. Superseded by open-row sugar in Phase 1d (D-029, D-030) — now works with records containing extra fields.

**Depends on**: [[row-polymorphism.types]]
**Enables**: [[pipe-operator.language]]
**Connections**:
- related: [[pipe-operator.language]] — combined with pipe: `results |> .name` extracts the name field
- related: open-row version was the motivation for pulling row polymorphism forward (D-029)
