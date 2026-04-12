---
tags: [data, write-path, open, needs-design, later, concept]
refs:
  - adr:D-051
  - thread:error-handling
---
# Write Context

Future abstraction that surfaces ingest pipeline handles as first-class piescript values. Currently, the write path has a gap:

- **Shard.write** ([[shard-write.data]]) writes directly to the primary shard, bypassing ingest pipelines entirely.
- **Index.bulk** ([[index-bulk.data]]) runs through the standard bulk API path which applies default pipelines, but offers no control over pipeline selection.

`WriteContext` would bridge this gap by making ingest pipelines addressable values:
- `WriteContext.withPipeline "my-pipeline" writer` -- attach a pipeline to a writer
- `WriteContext.noPipeline writer` -- explicitly skip ingest (current Shard.write behavior)
- Pipeline results available as typed values, not opaque side effects

This intersects with [[es-conventions-debt.infrastructure]] because the current shard-level write path deliberately bypasses ES conventions (ingest, routing, replication) for performance. `WriteContext` would provide a principled way to opt into those conventions selectively.

**Depends on**: [[shard-write.data]], [[index-bulk.data]]
**Enables**: (none directly -- design unsettled)
**Connections**:
- extends: [[primary-shard-write.data]] -- WriteContext wraps the raw shard write with pipeline awareness
- informs: [[index-bulk.data]] -- bulk writes could use WriteContext for pipeline control
- uses: [[es-conventions-debt.infrastructure]] -- addresses the convention gap for ingest pipelines
- informs: [[mapping-update-failure.obstacle]] -- WriteContext should also handle mapping update strategy
- informs: [[indexing-pressure-bypass.obstacle]] -- WriteContext lifecycle could include pressure tracking
- part-of: [[block-e.roadmap]] -- extension of the write primitives block
- related: [[bracket-patterns.language]] -- WriteContext is a resource that needs lifecycle management
