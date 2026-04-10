---
tags: [data, lucene, concept, superseded]
refs: []
---
# Shard Interaction Layers

Three-layer evolution path for shard data access:

- **Layer 1**: [[lucene-m.data|LuceneM]] free monad giving full control over every Lucene primitive ([[index-searcher.es-internals|searcher]] acquisition, [[lucene-segments.es-internals|segment]] iteration, [[doc-values.es-internals|DocValues]] reading) with automatic resource management.
- **Layer 2**: pull/push access patterns (`Shard.open`/`Shard.consume`/`Shard.read`) that expose a practical API without requiring the user to manage Lucene internals.
- **Layer 3**: declarative combinators with [[query-typeclass.data|typeclass interpretation]] (`Query a`) where the runtime chooses the optimal execution strategy.

Block D implements Layer 2; Layers 1 and 3 are future work that bracket the current design from below and above.

**Depends on**: [[lucene-m.data]], [[data-access-hierarchy]]
**Enables**: (none directly)
**Connections**:
- extends: [[shard-read.data]] -- Layer 2 is the current shard-read implementation
- prerequisite-for: [[query-typeclass.data]] -- Layer 3 requires typeclass infrastructure to interpret declarative queries
- replaced-by: [[data-access-hierarchy]] — 4-level hierarchy replaced the 3-layer model
