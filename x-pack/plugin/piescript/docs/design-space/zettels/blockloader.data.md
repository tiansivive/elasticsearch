---
tags: [data, performance, lucene, columnar, open]
refs:
  - adr:D-054
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# BlockLoader Integration

`BlockLoader` integration for optimized column-at-a-time Lucene reads. The ESQL compute engine uses `BlockLoader` with `ColumnAtATimeReader` and `RowStrideReader` for efficient columnar data loading from Lucene. Integrating this into piescript's shard stream path would significantly improve read performance over the current approach of building Blocks directly via Block builders.

**Depends on**: [[shard-stream.data]], [[field-caps-resolution.data]]
**Enables**: (none)
**Connections**:
- contrasts-with: [[shard-stream.data]] — current stream uses Block builders directly; `BlockLoader` adds `ColumnAtATimeReader`/`RowStrideReader` optimization
- related: [[materialization-boundary.data]] — BlockLoader optimization affects where the materialization boundary sits
