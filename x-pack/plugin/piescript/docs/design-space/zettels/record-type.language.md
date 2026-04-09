---
tags: [language, types, implemented, documentation]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Value.java
  - code:piescript.types
---
# Record Type

- `RecordVal` with named fields backed by `Map<String, Value>`.
- Field projection and record update via open-row [[unification-algorithm.types|unification]].
- `RecordType(MonoType row)` wraps a [[rowtype-as-monotype.types|row type]].
- `RecordVal` -> XContent conversion for writes ([[recordval-to-xcontent.data]]).
- Inconsistent `Map.of` vs `LinkedHashMap` is tech debt ([[recordval-map-inconsistency.runtime]]).

**Depends on**: [[row-polymorphism.types]]
**Enables**: [[shard-read.data]], [[index-bulk.data]]
**Connections**:
- subsumes: [[nested-record-types.data]] — records are piescript's primary structured data type; nested record types from OBJECT fields
- complements: [[accessor-sugar.language]] — `.field` sugar operates on records
- complements: [[update-sugar.language]] — `{ r | field = val }` sugar for record update
- uses: [[rowtype-as-monotype.types]] — RecordType wraps a row-kinded MonoType
- uses: [[recordval-to-xcontent.data]] — XContent conversion for the write path
