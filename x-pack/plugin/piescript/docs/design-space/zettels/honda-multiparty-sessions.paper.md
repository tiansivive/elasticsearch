---
tags: [paper, types, pi-calculus, distributed, coordination, theoretical, concurrency, channels, safety, protocol, reference]
refs:
  - doc:references.md
  - resource:https://dl.acm.org/doi/10.1145/1328438.1328472
---
# Honda, Yoshida, Carbone -- Multiparty Asynchronous Session Types

Kohei Honda, Nobuko Yoshida, and Marco Carbone. "Multiparty Asynchronous Session Types." *POPL 2008*.

Extends binary session types (Honda et al. 1998) to multi-party protocols with asynchronous communication. A global type describes the entire interaction pattern among all participants; local types are projected from the global type for each participant. The type system ensures that each participant's local behavior conforms to the global protocol, preventing communication mismatches and deadlocks even with multiple parties.

For piescript, multiparty session types are relevant when `spawn` creates more than two communicating processes -- a coordinator and multiple data-node workers, for instance. The global/local type projection model aligns with piescript's topology-aware execution: the global type describes the full distributed computation, and each node's local behavior is a projection. However, the distributed setting introduces challenges around asynchrony and failure that binary session types avoid.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- extends: [[honda-session-types.paper]] -- multiparty extension of binary session types
- informs: [[session-types.types]] -- multiparty protocols for multi-process coordination
- informs: [[distributed-continuations.obstacle]] -- distributed sessions face the same continuation-spanning-network-boundary problem
- informs: [[topology-routing.coordination]] -- global types map onto topology-aware routing
- part-of: [[papers.hub]]
- related: [[wadler-propositions-as-sessions.paper]] -- Wadler's logical foundation applies to binary; multiparty needs richer logic
- related: [[join-calculus.coordination]] -- join patterns can encode multi-party synchronization
