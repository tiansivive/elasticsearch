---
tags: [data, types, implemented]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Field Caps Resolution

`IndexResolutionPrePass` extracts index names from `use` declarations, calls field capabilities API, resolves row types. Nested record types from OBJECT fields built recursively. Cross-index type conflicts detected via `InvalidMappedField`. Runs before elaboration.

**Depends on**: [[row-polymorphism.types]]
**Enables**: [[use-declarations.data]]
**Connections**:
- related: static schema at elaboration time — empty mapping produces confusing errors (tech debt: emit diagnostic)
- related: [[nested-record-types.data]] — field caps recursively builds nested record types for OBJECT fields
- related: [[index-type.data]] — field caps resolution produces the row type r carried by Index r
