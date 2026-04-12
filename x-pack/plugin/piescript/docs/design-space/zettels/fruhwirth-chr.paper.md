---
tags: [paper, search, coordination, theoretical, concurrency, pi-calculus, scheduling, reference]
refs:
  - doc:references.md
---
# Fruhwirth -- Theory and Practice of Constraint Handling Rules

Thom Fruhwirth. "Theory and Practice of Constraint Handling Rules." *Journal of Logic Programming*, 1998.

Defines Constraint Handling Rules (CHR): a committed-choice language where multi-headed rules match on a constraint store and fire when all heads are present. CHR rules are declarative, and the runtime discovers and executes matching reactions -- the same pattern as piescript's `when` synchronization on multiple channels.

The connection to piescript is structural: CHR's multi-headed rules correspond to join patterns. A rule `r1(X), r2(Y) ==> body(X,Y)` fires when both `r1` and `r2` are present in the store, just as `when(ch1, ch2, fn (v1, v2) -> body)` fires when both channels have values. CHR's committed-choice semantics (no backtracking after firing) matches piescript's `when` semantics. The scheduling and confluence analysis from CHR theory informs how piescript could optimize multi-way joins.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- informs: [[cham-patterns.coordination]] -- CHAM reaction rules generalize CHR-style multi-headed matching
- informs: [[when-synchronization.coordination]] -- multi-way `when` is a join pattern, like a CHR multi-headed rule
- informs: [[join-calculus.coordination]] -- join calculus reaction rules and CHR rules share the multi-headed matching structure
- informs: [[logic-programming.hub]] -- CHR is a constraint logic programming technique
- part-of: [[papers.hub]]
- related: [[backtracking.search]] -- CHR is committed-choice (no backtracking), contrasting with general search
