---
tags: [infrastructure, streaming, es-internals, implemented, distributed, columnar]
refs:
  - adr:D-054
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Exchange Streaming

`Exchange r` is a serializable descriptor (ID + column names + buffer size). `Sink r` and `Source r` are node-local handles instantiated from the descriptor. Backed by ESQL's `ExchangeService`. `Exchange.open`/`sink`/`connect`/`addPage`/`poll`/`finish`. Callback-based `poll` avoids need for union types.

**Depends on**: [[channels.infrastructure]], [[shard-stream.data]]
**Enables**: [[materialization-boundary.data]]
**Connections**:
- related: topology-agnostic — same API for local and remote exchanges
- related: column name mismatch is a known gap (runtime check only)
- related: [[serialization.infrastructure]] — exchange descriptors are serializable for cross-node setup
- related: [[topology.infrastructure]] — remote exchanges require topology knowledge to connect nodes
