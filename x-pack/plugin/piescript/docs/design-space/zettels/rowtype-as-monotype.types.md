---
tags: [types, row-types, implemented, kinds, concept]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# RowType as MonoType

`RowType` implements `MonoType`. `RecordType(MonoType row)` with row-kindedness assertion.

- [[zonker.types|Zonker]] unified to `Map<Integer, MonoType>`.
- Row type variables `r` in `Index r`, `Searcher r`, `DocRef r`, `Writer r`, `ESQL r` are properly row-kinded.
- Previously rows were separate from `MonoType` -- `r` had `Kind.TYPE`, making `DocRef Double` well-kinded (unsound).

**Depends on**: [[row-polymorphism.types]], [[f-omega-lite.types]]
**Enables**: [[esql-compilation.esql]], [[shard-read.data]]
**Connections**:
- part-of: [[block-f.roadmap]]
- solves: was a high-priority tech debt item (D-050) — completed as Block F prerequisite
- uses: [[kind-system.types]] — row-kindedness assertion prevents unsound type applications like `DocRef Double`
- complements: [[record-type.language]] — RecordType wraps a row-kinded MonoType
