---
tags: [types, theoretical, someday]
refs:
  - doc:references.md
  - roadmap:phase-6
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:type-foundations
---
# Session Types

Type-checked communication protocols on [[channels.infrastructure|channels]].

- Binary session types (Honda et al. 1998).
- Multiparty session types for multi-process coordination.
- Wadler's "Propositions as Sessions" -- Curry-Howard for concurrency provides deadlock-freedom from the type system.

**Depends on**: [[qtt-linearity.types]], [[channels.infrastructure]]
**Enables**: (none directly)
**Connections**:
- part-of: [[future-type-system.roadmap]]
- part-of: [[phase-6.roadmap]]
- related: [[qtt-linearity.types]] — requires linearity (channel endpoints used exactly once per protocol step); Phase 6
- enables: [[replication-protocol.infrastructure]] — session types encode the replication protocol
- motivates: [[primary-shard-write.data]] — write->replicate protocol could be enforced via session types
- cites: [[wadler-propositions-as-sessions.paper]], [[honda-session-types.paper]], [[honda-multiparty-sessions.paper]]
