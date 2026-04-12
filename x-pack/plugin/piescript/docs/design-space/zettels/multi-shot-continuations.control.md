---
tags: [control-flow, continuation, search, concept]
refs: []
---
# Multi-Shot Continuations

Continuations invoked more than once. Required for: backtracking (retry from a choice point), nondeterminism (explore multiple paths), coroutine cloning. Requires heap-allocated frames because the continuation must survive past its first use. Open questions: heap allocation strategy, escape analysis (can the compiler prove single-use?), copyability of captured state, replayability semantics.

Multi-shot is the expensive case. Each invocation beyond the first requires either copying the continuation frame or using a persistent data structure for the environment. This interacts badly with mutable state (which copy gets which mutation?) and with distribution (copying a continuation that spans nodes).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[delimited-continuations.hub]]
- uses: [[backtracking.search]] -- backtracking = multi-shot continuation invocation
- tension-with: [[qtt-linearity.types]] -- multi-shot = unrestricted, multiplicity omega
- contrasts-with: [[one-shot-continuations.control]] -- one vs many invocations
- uses: [[stackful-continuations.control]] -- multi-shot typically needs copyable stack frames
- tension-with: [[distributed-continuations.obstacle]] -- copying distributed continuations compounds the serialization problem
