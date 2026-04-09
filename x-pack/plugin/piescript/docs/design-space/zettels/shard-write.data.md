---
tags: [data, implemented, documentation]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:EvalWrite.java
  - code:WriterState.java
---
# Shard Write

- `Shard.writer` acquires write context on primary shard.
- `Shard.write` writes single doc via `IndexShard.applyIndexOperationOnPrimary()` -- INDEX (upsert) semantics.
- `Shard.refresh` triggers refresh. `Shard.globalCheckpoint` monitors [[replication-model.data|replication]].
- Primary-only: no replication, no ingest, no routing.

**Depends on**: [[index-type.data]], [[channels.infrastructure]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-e.roadmap]]
- constrains: [[rowtype-as-monotype.types]] — `_id` as separate argument was a workaround for `RowType` not being row-kinded (D-050 S5)
- uses: [[non-serializable-types.types]] — `WriterVal` is non-serializable
- implements: [[replication-model.data]] — primary-only writes; replicas catch up via translog
- part-of: [[two-tier-architecture.data]] — shard-level write tier in the two-tier architecture
- tradeoff-with: [[create-vs-index.data]] — INDEX vs CREATE semantics for shard-level writes
- uses: [[recordval-to-xcontent.data]] — RecordVal-to-XContent conversion for the write path
