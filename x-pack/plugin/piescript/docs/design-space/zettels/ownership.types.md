---
tags: [types, resources, theoretical]
refs:
  - vision:speculative
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Ownership Semantics

Ownership semantics for resource tracking (Rust-inspired). An ownership system tracks which binding owns a resource, ensuring exactly-once cleanup and preventing use-after-free at the type level. This enables safe mutable shared references and persistent in-memory resources without garbage collection overhead.

**Depends on**: [[qtt-linearity.types]]
**Enables**: safe mutable shared references, persistent in-memory resources
**Connections**:
- related: [[borrow-checking.types]] — alternative enforcement mechanism
- informs: [[local-kind.types]] — ownership subsumes Local kind
