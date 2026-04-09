---
tags: [es-internals, lucene, data, documentation]
refs:
  - code:EvalShard.java
---
# Index Searcher

Lucene's `IndexSearcher` is the entry point for all search operations on an index. It wraps an `IndexReader` which provides access to [[lucene-segments.es-internals]].
- In piescript, [[shard-read.data]] `Shard.open` acquires an IndexSearcher asynchronously and wraps it in `SearcherVal`
- The searcher must remain open for the duration of reads -- it provides a point-in-time snapshot of the index (see [[searcher-lifecycle.data]])

**Depends on**: (none)
**Enables**: [[searcher-lifecycle.data]]
**Connections**:
- uses: [[lucene-segments.es-internals]] -- IndexReader exposes the segment structure
- prerequisite-for: [[shard-read.data]] -- Shard.open acquires an IndexSearcher
- prerequisite-for: [[searcher-lifecycle.data]] -- searcher lifecycle management depends on IndexSearcher
