---
tags: [types, mutability, open, exploration, concept, someday]
refs:
  - thread:ownership-resources
---
# Mutable Shared State

Safe mutable references via [[qtt-linearity.types]] multiplicities. Owned linear values can be exclusively mutated by one process at a time, enabling:

- Persistent counters, caches
- [[incremental-computation.performance]]
- Cross-stream communication

Approaches Rust-like [[ownership.types]] guarantees from a functional starting point, using linearity to enforce exclusive access rather than a [[borrow-checking.types]] with lifetime annotations.

**Depends on**: [[qtt-linearity.types]], [[ownership.types]]
**Enables**: [[incremental-computation.performance]]
**Connections**:
- part-of: [[future-coordination.roadmap]]
- related: [[borrow-checking.types]] — linearity-based mutation is a functional alternative to lifetime-based borrowing
- prerequisite-for: [[long-lived-computations.lifecycle]] — long-lived computations need safe mutable state for cross-invocation persistence
