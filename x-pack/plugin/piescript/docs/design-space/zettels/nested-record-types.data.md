---
tags: [data, types, implemented, documentation]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:IndexResolutionPrePass.java
---
# Nested Record Types

`IndexResolutionPrePass` recursively builds [[record-type.language]] `RecordType` for OBJECT fields in index mappings. `use "idx" as idx` now gets nested row types (e.g., `{ host: { name: Keyword, ip: Keyword }, ... }`). Implemented in the [[security-namespace.infrastructure]] fix session (2026-04-07).

- Enables projecting nested fields like `r.host.name` with full type safety
- [[dotted-field-paths.esql]] fix was needed alongside this for [[nbe-compilation.esql]] to emit `host.name` not just `name`

**Depends on**: [[field-caps-resolution.data]], [[row-polymorphism.types]]
**Enables**: [[esql-combinators.esql]]
**Connections**:
- complements: [[dotted-field-paths.esql]] — `ESQL.keep`/`drop` dotted path fix was needed alongside this; NbE needed to emit `host.name` not just `name` for nested fields
- uses: [[record-type.language]] — nested records are recursive record types
- uses: [[index-type.data]] — index mappings define the nested structure
