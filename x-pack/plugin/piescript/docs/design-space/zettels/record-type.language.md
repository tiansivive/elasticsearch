---
tags: [language, types, implemented]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Record Type

`RecordVal` with named fields backed by `Map<String, Value>`. Field projection and record update via open-row unification. `RecordType(MonoType row)` wraps a row type. `RecordVal` -> XContent conversion for writes. Inconsistent `Map.of` vs `LinkedHashMap` is tech debt.

**Depends on**: [[row-polymorphism.types]]
**Enables**: [[shard-read.data]], [[index-bulk.data]]
**Connections**:
- related: [[nested-record-types.data]] — records are piescript's primary structured data type; nested record types from OBJECT fields
- related: [[accessor-sugar.language]] — `.field` sugar operates on records
- related: [[update-sugar.language]] — `{ r | field = val }` sugar for record update
- related: [[rowtype-as-monotype.types]] — RecordType wraps a row-kinded MonoType
