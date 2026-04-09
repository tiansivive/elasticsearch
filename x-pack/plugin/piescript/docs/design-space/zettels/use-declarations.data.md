---
tags: [data, language, implemented, documentation]
refs:
  - adr:D-050
  - code:IndexResolutionPrePass.java
  - code:piescript.elab
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Use Declarations

`use "index-name" as idx` declares a typed [[index-type.data]] value:
- Row type `r` is resolved from field capabilities at elaboration time via `IndexResolutionPrePass` (see [[field-caps-resolution.data]])
- Elaborates to `CoreLet` with `LitVal.IndexLit`
- The ONLY way to reference an index -- enforces static type safety
- Supports [[nested-record-types.data]] from OBJECT fields

**Depends on**: [[row-polymorphism.types]], [[field-caps-resolution.data]]
**Enables**: [[shard-read.data]], [[shard-write.data]], [[esql-compilation.esql]]
**Connections**:
- part-of: [[block-d.roadmap]]
- contrasts-with: [[dynamic-index-names.data]] — static index resolution gives full type safety; dynamic index names are future work
- uses: [[index-type.data]] — use declarations produce typed Index r values
- uses: [[nested-record-types.data]] — use declarations support nested record types from OBJECT fields
