---
tags: [coordination, continuation, distributed, obstacle, open]
refs: []
---
# Distributed Continuations

What happens when shift is inside shipped code and reset is on the initiator node? The continuation spans a network boundary. Options: (1) disallow statically -- continuation-capturing operations cannot appear in mobile closures, (2) serialize the continuation -- requires reified frames, adds wire format complexity, (3) restrict to node-local only -- like current `when` locality constraint.

This obstacle applies beyond continuations: distributed backtracking (choice points span nodes), distributed logic variables (unification bindings span nodes), distributed fixpoint (intermediate state spans nodes). ANY mechanism that captures "the rest of the computation" faces this in a distributed environment. The problem is fundamental: a continuation is a snapshot of execution context, and distributing that context across nodes requires solving serialization, consistency, and failure semantics simultaneously.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[delimited-continuations.hub]]
- tension-with: [[code-mobility.coordination]] -- mobile closures + continuations = distributed continuation problem
- tension-with: [[shift-reset.control]] -- the obstacle that makes distributed shift/reset hard
- applies-to: [[backtracking.search]] -- distributed backtracking has the same spanning problem
- applies-to: [[logic-unification.search]] -- distributed unification bindings face same issue
- applies-to: [[logic-variables.search]] -- logic variables across nodes need distributed trail
- applies-to: [[datalog-fixpoint.search]] -- distributed fixpoint intermediate state
- constrains: [[interaction-nets.computation]] -- interaction across node boundaries
- uses: [[non-serializable-types.types]] -- current approach: reject non-serializable values at wire boundary
- uses: [[serialization-boundary.infrastructure]] -- the boundary where this problem surfaces
