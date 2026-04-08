---
tags: [data, types, open, effects]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Monadic Write

Future CPS/session-typed write pipeline: open -> prepare -> index -> replicate -> checkpoint -> refresh. Each step produces a linear value consumed by the next, encoding the write protocol as a session type. Gated on linearity (Phase 6). Would replace the current "primary-only, user monitors replication" model with a typed protocol.

**Depends on**: [[qtt-linearity.types]], [[session-types.types]], [[shard-write.data]]
**Enables**: (none directly)
**Connections**:
- related: [[session-types.types]] — write protocol is the most concrete motivation for session types
- related: D-051 S5 deferred items
- related: [[primary-shard-write.data]] — current primary-only model that the monadic write protocol would replace
- related: [[replication-model.data]] — replication is a step in the write protocol session type
