---
tags: [data, types, implemented]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Nested Record Types

`IndexResolutionPrePass` recursively builds `RecordType` for OBJECT fields in index mappings. `use "idx" as idx` now gets nested row types (e.g., `{ host: { name: Keyword, ip: Keyword }, ... }`). Implemented in the security namespace fix session (2026-04-07). Enables projecting nested fields like `r.host.name` with full type safety.

**Depends on**: [[field-caps-resolution.data]], [[row-polymorphism.types]]
**Enables**: [[esql-combinators.esql]]
**Connections**:
- related: [[dotted-field-paths.esql]] — `ESQL.keep`/`drop` dotted path fix was needed alongside this; NbE needed to emit `host.name` not just `name` for nested fields
