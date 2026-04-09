---
tags: [data, lucene, concurrency, open, concept, exploration]
refs: []
---
# Segment Parallelism

Per-segment [[spawn.coordination|spawn]]: fan out piescript computations per [[lucene-segments.es-internals|Lucene segment]] for user-controlled parallelism within a shard. Each segment gets its own [[index-searcher.es-internals|searcher]]; results merge via [[channels.infrastructure|channels]]. Finer-grained than shard-level parallelism.

**Depends on**: [[lucene-m.data]], [[spawn.coordination]]
**Enables**: (none directly)
**Connections**:
- extends: [[shard-read.data]] — adds per-segment granularity to shard reads
- related: [[channels.infrastructure]] — results merge via channel synchronization
- related: [[nested-data-parallelism.performance]] — per-segment parallelism is a form of nested data parallelism
- related: [[lucene-segments.es-internals]] — each segment is an independent unit of parallelism
