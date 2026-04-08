---
tags: [types, open, kinds]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Label Kind

No `Label` kind for type-safe field projection. `Shard.read` returns full record `r` (wildcard read). Type-safe single-field projection requires `Label` kind with type-level string singletons and a `Project` type family. Documented as D-050 future work.

**Depends on**: [[f-omega-lite.types]]
**Enables**: (none directly)
**Connections**:
- related: [[shard-read.data]] — would enable `Shard.read "name" ref` instead of wildcard-only reads
- related: [[dependent-types.types]] — Label kind with type-level string singletons is the closest piescript gets to dependent types
- related: [[row-polymorphism.types]] — Label kind would enable type-safe single-field projection from rows
