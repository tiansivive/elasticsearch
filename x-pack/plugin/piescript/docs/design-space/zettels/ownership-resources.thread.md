---
tags: [thread, roadmap, types, mutability, resources]
refs: []
---
# Ownership & Resources

From QTT linearity through ownership semantics to safe mutable shared state and
persistent in-memory resources. This thread covers what QTT multiplicities
unlock beyond channels and session types — the Rust-inspired ownership model
adapted to a functional distributed language. All items depend on QTT linearity
from the Type Foundations thread.

## Sequence

1. **Ownership semantics** [[ownership.types]] — exploration
   Resource tracking via linear types. Exactly-once cleanup, no use-after-free.
   Depends on: [[qtt-linearity.types]] (Type Foundations thread)

2. **Borrow checking** [[borrow-checking.types]] — exploration
   Trade-off analysis: Rust-style lifetimes vs QTT multiplicities.
   Alternative to pure linearity-based approach.
   Depends on: [[ownership.types]]

3. **Mutable shared state** [[mutable-shared-state.types]] — exploration
   Safe mutable references via linear ownership. Persistent counters, caches,
   cross-stream communication.
   Depends on: [[qtt-linearity.types]], [[ownership.types]]

4. **Persistent resources** [[persistent-resources.infrastructure]] — exploration
   Shared in-memory resources that outlive a single script execution.
   Depends on: [[ownership.types]]

5. **Incremental computation** [[incremental-computation.performance]] — exploration
   Incremental aggregation without full recomputation. Monoid-like properties
   for merging partial results.
   Depends on: [[persistent-resources.infrastructure]]

6. **Zero-copy linear transfer** [[zero-copy-linear-transfer.performance]] — exploration
   Move closures instead of cloning when linear. Eliminates allocation overhead
   for large captured environments traveling cross-node.
   Depends on: [[qtt-linearity.types]]

**Depends on**: [[type-foundations.thread]] — QTT linearity is the prerequisite
**Enables**: (none directly)
**Connections**:
- includes: [[ownership.types]]
- includes: [[borrow-checking.types]]
- includes: [[mutable-shared-state.types]]
- includes: [[persistent-resources.infrastructure]]
- includes: [[incremental-computation.performance]]
- includes: [[zero-copy-linear-transfer.performance]]
- related: [[type-foundations.thread]] — QTT linearity is shared prerequisite
- related: [[distributed-coordination.thread]] — persistent resources feed into long-lived computations
