---
tags: [data, implemented, documentation]
refs:
  - adr:D-051
  - code:EvalBuiltins.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Index Bulk

`Index.bulk : Keyword -> List r -> Channel result` writes records via the Bulk API.
- Handles routing, replication, ingest pipelines, index auto-creation
- [[recordval-to-xcontent.data]] conversion for `IndexRequest` source
- High-level complement to shard-level [[shard-write.data]]

**Depends on**: [[list-type.language]], [[record-type.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-e.roadmap]]
- part-of: [[two-tier-architecture.data]] — two-tier write architecture mirrors the read side (ESQL vs Shard.open)
- complements: [[shard-write.data]] — shard-level complement; two-tier write architecture
- uses: [[recordval-to-xcontent.data]] — RecordVal to XContent conversion for IndexRequest source
- overlaps: [[create-vs-index.data]] — bulk API also faces INDEX vs CREATE semantics
