---
tags: [infrastructure, distributed, types, theoretical]
refs:
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Replication Protocol

Session-type-encoded shard write replication protocol. The replication protocol for shard writes (primary to replica) can be encoded as a session type, ensuring at the type level that the protocol is followed correctly: primary writes, replicas acknowledge, and the session completes. This replaces eventual ad-hoc replication handling with a typed protocol.

**Depends on**: [[session-types.types]], [[replication-model.data]]
**Enables**: (none)
**Connections**:
- informs: [[shard-write.data]] — currently primary-only; supersedes eventual ad-hoc replication handling
