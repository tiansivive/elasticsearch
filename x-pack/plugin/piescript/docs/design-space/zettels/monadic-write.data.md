---
tags: [data, types, open, effects, concept]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Monadic Write

Future CPS/session-typed write pipeline: open, prepare, index, replicate, checkpoint, refresh. Each step produces a linear value consumed by the next, encoding the write protocol as a [[session-types.types]]. Gated on [[qtt-linearity.types]] (Phase 6). Would replace the current [[primary-shard-write.data]] "primary-only, user monitors replication" model with a typed protocol.

- Each continuation step is linear -- cannot skip or reorder
- Compile-time guarantee that the [[replication-model.data]] protocol is followed to completion

**Depends on**: [[qtt-linearity.types]], [[session-types.types]], [[shard-write.data]]
**Enables**: (none directly)
**Connections**:
- motivates: [[session-types.types]] — write protocol is the most concrete motivation for session types
- supersedes: [[primary-shard-write.data]] — current primary-only model that the monadic write protocol would replace
- related: [[replication-model.data]] — replication is a step in the write protocol session type
- related: [[monadic-write-protocol.data]] — overlapping design space; this zettel focuses on the type-theoretic foundation
