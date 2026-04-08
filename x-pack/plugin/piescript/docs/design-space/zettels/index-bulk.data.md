---
tags: [data, implemented]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Index Bulk

`Index.bulk : Keyword → List r → Channel result` writes records via the Bulk API. Handles routing, replication, ingest pipelines, index auto-creation. `RecordVal` → XContent conversion for `IndexRequest` source. High-level complement to shard-level `Shard.write`.

**Depends on**: [[list-type.language]], [[record-type.language]]
**Enables**: (none directly)
**Connections**:
- related: [[two-tier-architecture.data]] — two-tier write architecture mirrors the read side (ESQL vs Shard.open)
- related: [[shard-write.data]] — shard-level complement; two-tier write architecture
- related: [[create-vs-index.data]] — bulk API also faces INDEX vs CREATE semantics
