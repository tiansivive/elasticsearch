---
tags: [data, performance, lucene, columnar, open, concept, compute-engine, es-internals]
refs:
  - adr:D-054
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/lucene/query/LuceneSourceOperator.java
  - code:server/src/main/java/org/elasticsearch/index/mapper/BlockLoader.java
---
# BlockLoader Integration

`BlockLoader` integration for optimized column-at-a-time Lucene reads. The ESQL compute engine uses `BlockLoader` with `ColumnAtATimeReader` and `RowStrideReader` for efficient columnar data loading from [[doc-values.es-internals]] and [[lucene-segments.es-internals]]. Integrating this into piescript's [[shard-stream.data]] path would significantly improve read performance over the current approach of building Blocks directly via Block builders. Would eventually be exposed as an optimization within [[lucene-m.data]] programs operating at [[shard-read.data]] level. See [[compute-engine.es]] for the columnar stack; `BlockLoader` is the ES bridge from Lucene to [[compute-data-page-block.es]].

**Depends on**: [[shard-stream.data]], [[field-caps-resolution.data]], [[doc-values.es-internals]], [[lucene-segments.es-internals]]
**Enables**: (none directly — optimization layer)
**Connections**:
- informs: [[compute-engine.es]] — future integration sits in the same compute columnar stack
- informs: [[compute-data-page-block.es]] — BlockLoader feeds `Page`/`Block` producers
- contrasts-with: [[shard-stream.data]] — current stream uses Block builders directly; `BlockLoader` adds `ColumnAtATimeReader`/`RowStrideReader` optimization
- informs: [[materialization-boundary.data]] — BlockLoader optimization affects where the materialization boundary sits
- optimizes: [[shard-read.data]] — more efficient read path for shard-level data access
- related: [[lucene-m.data]] — LuceneM could expose BlockLoader as an optimized read primitive
- uses: [[doc-values.es-internals]] — BlockLoader reads DocValues in columnar batches
- uses: [[lucene-segments.es-internals]] — segment-level iteration for column-at-a-time reads
