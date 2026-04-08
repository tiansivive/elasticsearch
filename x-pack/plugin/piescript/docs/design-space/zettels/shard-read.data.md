---
tags: [data, implemented, lucene]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Shard Read

Three pull-based primitives: `Shard.open` acquires `IndexSearcher` asynchronously via channel. `Shard.consume` iterates `DocIdSetIterator` for up to N docs (synchronous). `Shard.read` reads all doc-value fields from a `DocRef` returning record of type `r`. `SearcherVal` and `DocRefVal` are non-serializable node-local types.

**Depends on**: [[index-type.data]], [[channels.infrastructure]]
**Enables**: [[shard-stream.data]], [[code-mobility.coordination]]
**Connections**:
- related: [[lucene-m.data]] — maps directly to Lucene's `DocIdSetIterator` model; foundation for future `LuceneM` free monad
- related: [[two-tier-architecture.data]] — shard-level read tier in the two-tier architecture
- related: [[blockloader.data]] — BlockLoader optimization for shard-level reads
