---
tags: [data, streaming, materialization, implemented, lucene, columnar, documentation, compute-engine]
refs:
  - adr:D-054
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:EvalPage.java
  - code:EvalShard.java
---
# Shard Stream

`Shard.stream` converts `DocRef` batches to columnar [[page-opaque-typed.data]] via compute engine Block builders (`LongBlock.Builder`, `DoubleBlock.Builder`, `BytesRefBlock.Builder`, `BooleanBlock.Builder`).

- Reads [[doc-values.es-internals]] fields from [[shard-read.data]] results.
- Produces real compute Pages compatible with the [[exchange-streaming.infrastructure]] system.
- `Page.toList` materializes back to [[record-type.language|RecordVals]]. `Page.count` returns row count.
- The [[type-stack.data]] determines which fields are materialized into Blocks.

**Depends on**: [[shard-read.data]], [[doc-values.es-internals]], [[page-opaque-typed.data]]
**Enables**: [[exchange-streaming.infrastructure]]
**Connections**:
- part-of: [[block-g.roadmap]]
- uses: [[compute-engine.es]] — ES `org.elasticsearch.compute` hub
- uses: [[compute-data-page-block.es]] — `Page`/`Block`/`BlockFactory`
- contrasts-with: [[blockloader.data]] — uses direct Block builders, not full `BlockLoader` infrastructure
- uses: [[boolean-block-fix.data]] — `BooleanBlock` fix for boolean fields
- implements: [[materialization-boundary.data]] — shard stream is where the materialization boundary decisions apply
- tension-with: [[eager-materialization.data]] — Page.toList materializes eagerly; stream + exchange defers it
- constrains: [[circuit-breaker.infrastructure]] — current Shard.stream uses NoopCircuitBreaker (tech debt)
- uses: [[type-stack.data]] — type stack determines field layout in Pages
- enables: [[exchange-streaming.infrastructure]] — Pages flow into Exchange for cross-node transfer
