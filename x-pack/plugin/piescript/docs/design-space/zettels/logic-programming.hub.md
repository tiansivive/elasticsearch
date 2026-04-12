---
tags: [language, search, hub, theoretical, exploration, someday]
refs:
  - doc:references.md
---
# Logic Programming

The cluster around logic variables, backtracking, and term-level unification. Implementing any one piece (Datalog, functional patterns) pulls in the others. Shared infrastructure: unification (have at type level via Robinson, need at term level), backtracking (don't have), logic variables (don't have). Highly relevant for ES graph pattern use cases -- entities linked by IDs across indices, transitive closure, reachability.

**Includes**: [[backtracking.search]], [[logic-unification.search]], [[logic-variables.search]], [[datalog-fixpoint.search]], [[stratified-negation.search]]

**Depends on**: [[hindley-milner.types]]
**Enables**: [[curry-narrowing.language]], [[cham-patterns.coordination]]
**Connections**:
- complements: [[join-calculus.coordination]] -- Datalog = coordination-free computation, Join Calculus = coordination; together they cover the CALM spectrum
- extends: [[unification-algorithm.types]] -- type-level Robinson unification extends to term-level
- uses: [[calm-theorem.types]] -- CALM determines what needs coordination vs what Datalog handles
- tension-with: [[distributed-continuations.obstacle]] -- distributed backtracking, unification, fixpoint all face the same spanning problem
- enables: [[curry-narrowing.language]] -- narrowing needs backtracking + unification
- enables: [[cham-patterns.coordination]] -- CHAM needs logic-style matching over message stores
