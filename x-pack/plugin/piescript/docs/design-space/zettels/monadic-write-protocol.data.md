---
tags: [data, write-path, effects, open, concept, feature]
refs: []
---
# Monadic Write Protocol

CPS/session-typed write pipeline: open, prepare, index, replicate, checkpoint, refresh. Each step is a typed continuation, and [[qtt-linearity.types]] ensures the protocol is followed to completion without skipping or reordering steps. This gives compile-time guarantees that the write path is correctly sequenced, turning ES's [[es-write-internals.infrastructure]] protocol into a verified state machine.

- Depends on [[session-types.types]] for typed continuation steps
- Supersedes ad-hoc [[primary-shard-write.data]] write sequencing

**Depends on**: [[qtt-linearity.types]], [[session-types.types]]
**Enables**: (none)
**Connections**:
- supersedes: [[primary-shard-write.data]] — replaces ad-hoc write sequencing with a session-typed protocol
- related: [[replication-model.data]] — replication is one of the typed continuation steps
- related: [[monadic-write.data]] — overlapping design space; this zettel focuses on the protocol verification aspect
- related: [[es-write-internals.infrastructure]] — models ES's internal write path as a session-typed protocol
