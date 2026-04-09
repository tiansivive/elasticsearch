---
tags: [es-internals, lucene, data, documentation]
refs: []
---
# Lucene Segments

A Lucene index is composed of immutable segments, each a self-contained mini-index. Searches fan out across segments. New documents go to new segments; merging consolidates old ones. Per-segment operations are embarrassingly parallel -- piescript's [[segment-parallelism.data]] exploits this.

- [[index-searcher.es-internals]] `IndexReader` provides access to segments
- [[doc-values.es-internals]] are stored per-segment for columnar field access
- Immutability enables lock-free parallel reads

**Depends on**: (none)
**Enables**: [[segment-parallelism.data]]
**Connections**:
- part-of: [[index-searcher.es-internals]] — IndexReader provides access to segments
- informs: [[shard-stream.data]] — streaming iterates within segments
- used-by: [[lucene-m.data]] — LuceneM exposes segment-level iteration for fine-grained control
