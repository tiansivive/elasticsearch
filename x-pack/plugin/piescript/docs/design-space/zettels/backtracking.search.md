---
tags: [search, control-flow, theoretical, concept]
refs:
  - doc:references.md
---
# Backtracking

Systematic exploration of alternatives. When a path fails, undo (unwind bindings on a trail) and try next. Implementation options: (1) multi-shot continuations (shift at choice point, reset at handler), (2) explicit choice points with trail stack, (3) compile to iterators/state machines. The choice between these mirrors the continuation compilation choice (CPS vs state machine).

**Depends on**: [[multi-shot-continuations.control]] OR trail-based implementation
**Enables**: [[datalog-fixpoint.search]], [[curry-narrowing.language]]
**Connections**:
- part-of: [[logic-programming.hub]]
- uses: [[multi-shot-continuations.control]] -- continuation-based implementation
- uses: [[logic-variables.search]] -- backtracking unwinds variable bindings
- enables: [[datalog-fixpoint.search]] -- semi-naive evaluation uses backtracking-like search
- enables: [[curry-narrowing.language]] -- narrowing is goal-directed backtracking
- tension-with: [[distributed-continuations.obstacle]] -- distributed backtracking = distributed multi-shot continuations; choice points span nodes
