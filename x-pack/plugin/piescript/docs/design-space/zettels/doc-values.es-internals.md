---
tags: [es-internals, lucene, columnar, data, documentation]
refs:
  - adr:D-050
  - code:x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/EvalShard.java
  - code:server/src/main/java/org/elasticsearch/index/engine/Engine.java
  - resource:https://lucene.apache.org/core/org/apache/lucene/index/DocValues.html
---
# Doc Values

Lucene's column-oriented storage for field values, separate from the inverted index. The concrete API is `org.apache.lucene.index.DocValues` (per-segment accessors obtained from `LeafReader`); Elasticsearch source in this repo does not vendor Lucene sources, so `resource:` points at Lucene Javadoc. Piescript reaches doc values through [[EvalShard.java]] (`SortedNumericDocValues`, `SortedSetDocValues`, etc.) and [[compute-data-page-block.es]] for columnar `Page` assembly.

- Used for sorting, aggregations, and field retrieval without re-parsing `_source`
- [[shard-read.data]] `Shard.read` reads all doc-value fields from a `DocRef`
- Not all field types have doc values — `text` fields don't

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- used-by: [[shard-read.data]] — Shard.read reads doc-value fields
- used-by: [[shard-stream.data]] — streaming reads via doc values
- used-by: [[blockloader.data]] — BlockLoader abstracts over doc-value access
- informs: [[compute-engine.es]] — columnar `Page`/`Block` tier above raw Lucene doc-value iteration
- informs: [[block-d.roadmap]] — local read path uses doc values
