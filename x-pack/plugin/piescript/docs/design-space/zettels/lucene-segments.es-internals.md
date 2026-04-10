---
tags: [es-internals, lucene, data, documentation, implemented]
refs:
  - code:server/src/main/java/org/elasticsearch/index/engine/Engine.java
  - resource:https://lucene.apache.org/core/org/apache/lucene/index/SegmentInfos.html
---
# Lucene Segments

A Lucene index is composed of immutable segments, each a self-contained mini-index. Searches fan out across segments. New documents go to new segments; merging consolidates old ones. Per-segment operations are embarrassingly parallel — piescript's [[segment-parallelism.data]] exploits this.

Elasticsearch does not vendor Lucene sources; segment APIs (`SegmentInfos`, `SegmentReader`, `SegmentCommitInfo`) are used from **`Engine`** (see `refs`) for commits, stats (`segmentsStats`), and per-segment field accounting. The Lucene **`SegmentInfos`** Javadoc is linked via `resource:` for the on-disk segment list representation.

- [[index-searcher.es-internals]] — `IndexReader` exposes leaves that correspond to segment views
- [[doc-values.es-internals]] — doc values are stored per segment for columnar field access
- Immutability enables lock-free parallel reads

**Depends on**: (none)
**Enables**: [[segment-parallelism.data]]
**Connections**:
- part-of: [[index-searcher.es-internals]] — `IndexReader` exposes the segment structure
- informs: [[shard-stream.data]] — streaming iterates within segments
- used-by: [[lucene-m.data]] — LuceneM exposes segment-level iteration for fine-grained control
- informs: [[doc-values.es-internals]] — per-segment columnar storage
- informs: [[block-d.roadmap]] — local read path is segment-aware via the engine
