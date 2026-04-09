---
tags: [es-internals, compute-engine, columnar, infrastructure, documentation, implemented]
refs:
  - adr:D-054
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/data/Page.java
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/data/Block.java
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/data/BlockFactory.java
  - code:EvalPage.java
---
# Compute Data: Page, Block, BlockFactory

The **`org.elasticsearch.compute.data`** package defines the columnar representation: a **`Page`** holds a batch of rows as parallel **`Block`** columns (e.g. `LongBlock`, `DoubleBlock`, `BytesRefBlock`, `BooleanBlock`). **`BlockFactory`** allocates blocks with correct memory accounting (circuit breaker integration is expected at call sites).

Piescript builds compatible **`Page`** instances in [[shard-stream.data]] using typed block builders; `EvalPage` handles materialization to piescript records.

**Depends on**: [[compute-engine.es]]

**Enables**: [[shard-stream.data]]

**Connections**:
- part-of: [[compute-engine.es]]
- used-by: [[shard-stream.data]] — `Shard.stream` produces `Page r`
- used-by: [[exchange-streaming.infrastructure]] — `Exchange.addPage` moves `Page`s
- implements: [[page-opaque-typed.data]] — piescript’s opaque `Page r` value model
