---
tags: [data, es-internals, distributed, implemented, concept]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Replication Model

[[primary-shard-write.data|Primary-only]] shard writes. Replicas catch up via translog (ES background replication).

- `Shard.globalCheckpoint` monitors replication progress -- same system Transforms use.
- The user can synchronize on replication via `globalCheckpoint` polling.
- Future: [[session-types.types|session types]] could encode the write->replicate->checkpoint protocol as a typed [[channels.infrastructure|channel]] interaction.

**Depends on**: [[primary-shard-write.data]]
**Enables**: (none directly)
**Connections**:
- related: [[channels.infrastructure]] — cross-shard coordination (saga-style) is possible with existing channel primitives
- complements: [[shard-write.data]] — replication follows shard-level writes
- evolved-into: [[monadic-write.data]] — future: session types encode the write->replicate->checkpoint protocol
- informs: [[replication-protocol.infrastructure]] — session-type-encoded replication protocol builds on this model
