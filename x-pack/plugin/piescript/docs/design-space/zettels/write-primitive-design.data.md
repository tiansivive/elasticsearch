---
tags: [data, write-path, implemented, decision, concept]
refs:
  - adr:D-051
  - code:EvalWrite.java
  - code:WriterState.java
---
# Write Primitive Design

Piescript's `Shard.writer`/`Shard.write` bypass design:
- Primary-only writes with no replication, no ingest pipelines, and no indexing pressure
- The user monitors replication progress via `globalCheckpoint`
- Deliberate set of tradeoffs for direct Engine access
- Writes go through `IndexShard.applyIndexOperationOnPrimary()` with INDEX (upsert) semantics, skipping the entire replication and ingest machinery
- Replicas catch up asynchronously via the translog
- See [[replication-model.data]] for the replication tradeoff and [[es-write-internals.infrastructure]] for the bypassed machinery

**Depends on**: [[primary-shard-write.data]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-e.roadmap]]
- extends: [[primary-shard-write.data]] — builds on the primary-shard write primitive
- constrains: [[es-write-internals.infrastructure]] — bypasses standard ES write path by design
- tradeoff-with: [[replication-model.data]] — no synchronous replication; user must monitor globalCheckpoint
