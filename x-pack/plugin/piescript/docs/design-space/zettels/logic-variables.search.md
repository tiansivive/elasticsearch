---
tags: [search, evaluation, theoretical, concept]
refs:
  - doc:references.md
---
# Logic Variables

Unknown values refined during search. Like type metas but at the value level. A logic variable starts unbound, gets constrained by unification, and may be unbound during backtracking (trail-based). Needed for Datalog, Prolog-style search, narrowing.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[logic-programming.hub]]
- analogous-to: [[meta-variables.types]] -- metas at type level = logic variables at value level
- uses: [[logic-unification.search]] -- unification binds logic variables
- uses: [[backtracking.search]] -- backtracking unwinds logic variable bindings on the trail
- tension-with: [[distributed-continuations.obstacle]] -- logic variables across nodes need distributed trail/binding management
- tension-with: [[purity.language]] -- logic variables are mutable state; how does this interact with piescript's purity guarantee?
