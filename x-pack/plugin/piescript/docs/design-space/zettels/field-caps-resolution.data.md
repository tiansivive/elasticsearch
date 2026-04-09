---
tags: [data, types, implemented, documentation]
refs:
  - adr:D-050
  - code:IndexResolutionPrePass.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Field Caps Resolution

`IndexResolutionPrePass` extracts index names from [[use-declarations.data]], calls field capabilities API, resolves [[row-polymorphism.types]].
- [[nested-record-types.data]] from OBJECT fields built recursively
- Cross-index type conflicts detected via `InvalidMappedField`
- Runs before [[elaboration-architecture.types]]

**Depends on**: [[row-polymorphism.types]]
**Enables**: [[use-declarations.data]]
**Connections**:
- part-of: [[phase-2.roadmap]]
- constrains: [[elaboration-architecture.types]] — static schema at elaboration time; empty mapping produces confusing errors (tech debt: emit diagnostic)
- uses: [[nested-record-types.data]] — field caps recursively builds nested record types for OBJECT fields
- prerequisite-for: [[index-type.data]] — field caps resolution produces the row type r carried by Index r
