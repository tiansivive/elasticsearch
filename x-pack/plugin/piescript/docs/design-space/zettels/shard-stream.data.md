---
tags: [data, streaming, materialization, implemented, lucene, columnar]
refs:
  - adr:D-054
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Shard Stream

`Shard.stream` converts `DocRef` batches to columnar `Page` via compute engine Block builders (`LongBlock.Builder`, `DoubleBlock.Builder`, `BytesRefBlock.Builder`, `BooleanBlock.Builder`). Produces real compute Pages compatible with Exchange system. `Page.toList` materializes back to `RecordVal`s. `Page.count` returns row count.

**Depends on**: [[shard-read.data]]
**Enables**: [[exchange-streaming.infrastructure]]
**Connections**:
- contrasts-with: [[blockloader.data]] — uses direct Block builders, not full `BlockLoader` infrastructure
- related: [[boolean-block-fix.data]] — `BooleanBlock` fix for boolean fields
- related: [[materialization-boundary.data]] — shard stream is where the materialization boundary decisions apply
- related: [[eager-materialization.data]] — Page.toList materializes eagerly; stream + exchange defers it
- related: [[circuit-breaker.infrastructure]] — current Shard.stream uses NoopCircuitBreaker (tech debt)
