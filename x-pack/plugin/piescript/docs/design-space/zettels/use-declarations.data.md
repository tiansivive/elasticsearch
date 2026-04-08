---
tags: [data, language, implemented]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Use Declarations

`use "index-name" as idx` declares a typed `Index r` value. The row type `r` is resolved from field capabilities at elaboration time via `IndexResolutionPrePass`. Elaborates to `CoreLet` with `LitVal.IndexLit`. The ONLY way to reference an index. Supports nested record types from OBJECT fields.

**Depends on**: [[row-polymorphism.types]], [[field-caps-resolution.data]]
**Enables**: [[shard-read.data]], [[shard-write.data]], [[esql-compilation.esql]]
**Connections**:
- contrasts-with: [[dynamic-index-names.data]] — static index resolution gives full type safety; dynamic index names are future work
- related: [[index-type.data]] — use declarations produce typed Index r values
- related: [[nested-record-types.data]] — use declarations support nested record types from OBJECT fields
