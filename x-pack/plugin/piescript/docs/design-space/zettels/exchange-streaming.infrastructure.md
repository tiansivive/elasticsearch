---
tags: [infrastructure, streaming, es-internals, implemented, distributed, columnar, documentation, compute-engine]
refs:
  - adr:D-054
  - code:EvalExchange.java
  - code:EvalPage.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Exchange Streaming

`Exchange r` is a serializable descriptor (ID + column names + buffer size) following the [[handle-descriptor-split.pattern]]. `Sink r` and `Source r` are node-local handles instantiated from the descriptor.
- Backed by ESQL's `ExchangeService`
- `Exchange.open`/`sink`/`connect`/`addPage`/`poll`/`finish`
- Callback-based `poll` avoids need for union types

**Depends on**: [[channels.infrastructure]], [[shard-stream.data]]
**Enables**: [[materialization-boundary.data]]
**Connections**:
- part-of: [[block-g.roadmap]]
- part-of: [[deferred-exchange.roadmap]]
- makes-redundant: [[eager-materialization.data]] — exchange streaming eliminates the need for full eager materialization on the data plane; Page batches stream with backpressure instead of collecting into `List<Value>`
- uses: [[compute-engine.es]] — ES `org.elasticsearch.compute` hub
- uses: [[compute-exchange-service.es]] — `ExchangeService` backend
- implements: [[handle-descriptor-split.pattern]] — Exchange r (descriptor) vs Sink/Source (handles) is the canonical instance
- constrains: [[column-name-derivation.types]] — column name mismatch between Exchange and Page is a known gap (runtime check only)
- uses: [[serialization.infrastructure]] — exchange descriptors are serializable for cross-node setup
- uses: [[topology.infrastructure]] — remote exchanges require topology knowledge to connect nodes
- tension-with: [[stream-a.language]] — Exchange is explicit plumbing; the original Stream a design was abstract codata. Reconciliation between these abstraction levels is an open question.
