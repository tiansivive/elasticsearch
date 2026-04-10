---
tags: [types, open, kinds, concept, needs-design, later]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:type-foundations
---
# Label Kind

No `Label` kind for type-safe field projection. [[shard-read.data]] returns full record `r` (wildcard read). Type-safe single-field projection requires `Label` kind with type-level string singletons and a `Project` type family. Documented as D-050 future work. Would extend the [[kind-system.types]] vocabulary with a new kind alongside `Type` and `Row`.

**Depends on**: [[f-omega-lite.types]]
**Enables**: (none directly)
**Connections**:
- unblocks: [[shard-read.data]] — would enable `Shard.read "name" ref` instead of wildcard-only reads
- specializes: [[dependent-types.types]] — Label kind with type-level string singletons is the closest piescript gets to dependent types
- extends: [[row-polymorphism.types]] — Label kind would enable type-safe single-field projection from rows
- extends: [[kind-system.types]] — adds a new kind to the kind vocabulary
