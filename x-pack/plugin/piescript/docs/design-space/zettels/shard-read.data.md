---
tags: [data, implemented, lucene, documentation]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:EvalShard.java
  - code:SearcherState.java
---
# Shard Read

Three pull-based primitives for Level 4 of the [[data-access-hierarchy]]:

- `Shard.open` acquires an [[index-searcher.es-internals]] asynchronously via [[channels.infrastructure]].
- `Shard.consume` iterates a [[doc-id-set-iterator.es-internals]] for up to N docs (synchronous).
- `Shard.read` reads all [[doc-values.es-internals]] fields from a `DocRef` returning a [[record-type.language|record]] of type `r`.

`SearcherVal` and `DocRefVal` are [[non-serializable-types.types]] -- node-local only.

**Depends on**: [[index-type.data]], [[channels.infrastructure]], [[non-serializable-types.types]]
**Enables**: [[shard-stream.data]], [[code-mobility.coordination]], [[lucene-m.data]]
**Connections**:
- part-of: [[block-d.roadmap]]
- prerequisite-for: [[lucene-m.data]] — maps directly to Lucene's DocIdSetIterator model; foundation for future LuceneM free monad
- part-of: [[data-access-hierarchy]] — Level 4 in the data access hierarchy
- part-of: [[two-tier-architecture.data]] — shard-level read tier in the two-tier architecture
- optimized-by: [[blockloader.data]] — BlockLoader optimization for shard-level reads
- complements: [[searcher-lifecycle.data]] — Shard.open is the acquisition side of searcher lifecycle
- uses: [[index-searcher.es-internals]] — SearcherVal wraps IndexSearcher
- uses: [[doc-values.es-internals]] — Shard.read uses DocValues for field access
- uses: [[doc-id-set-iterator.es-internals]] — Shard.consume iterates via DocIdSetIterator
