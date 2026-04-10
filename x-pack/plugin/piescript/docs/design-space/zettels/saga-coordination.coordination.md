---
tags: [coordination, distributed, write-path, fault-tolerance, open, concept, someday]
refs:
  - thread:error-handling
---
# Saga Coordination

Cross-shard [[shard-write.data|write]] coordination via [[channels.infrastructure|channels]] using the saga pattern.

- Multi-shard writes with compensating actions on failure.
- Each step publishes to a channel and a coordinator listens for completion or failure, triggering rollback compensations as needed.
- Already expressible with piescript channels but without built-in saga orchestration or compensation guarantees.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- related: [[channels.infrastructure]] — sagas are orchestrated through channel messaging
- related: [[send.coordination]] — each saga step communicates via send
- related: [[otp-supervision.coordination]] — supervision trees handle failure recovery; sagas handle transactional rollback
- related: [[monadic-write-protocol.data]] — monadic writes could formalize saga steps as typed effects
