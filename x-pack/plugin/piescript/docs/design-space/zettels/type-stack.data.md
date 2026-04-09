---
tags: [data, materialization, design-pattern, concept, pattern]
refs:
  - adr:D-054
---
# Type Stack

The three-level runtime representation ladder:
- **RawData** -- a description of data (index name, column types) with no I/O
- **Page/Block** -- columnar, ref-counted, compatible with ESQL's compute engine (see [[page-opaque-typed.data]])
- **Value** -- what piescript code operates on: records, lists, scalars (see [[evaluator.language]])

Moving down the stack (RawData to Page to Value) is materialization; each step trades laziness for accessibility. RawData can describe terabytes; Page holds a bounded batch of rows in columnar form; Value is fully expanded. The [[materialization-boundary.data]] determines where in this stack conversion happens, and who pays the memory cost.

**Depends on**: (none directly)
**Enables**: [[materialization-boundary.data]]
**Connections**:
- part-of: [[block-g.roadmap]]
- uses: [[shard-stream.data]] — shard stream produces Pages from DocRef batches (level 2)
- uses: [[exchange-streaming.infrastructure]] — exchange moves Pages across nodes without materializing to Values
- complements: [[eager-materialization.data]] — eager materialization collapses all three levels immediately; the type stack makes the levels explicit
- uses: [[page-opaque-typed.data]] — Page/Block is the middle tier of the stack
