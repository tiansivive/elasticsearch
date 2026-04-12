---
tags: [search, types, theoretical, concept]
refs:
  - doc:references.md
---
# Logic Unification

Term-level unification -- binding logic variables to terms and checking consistency. Piescript already has Robinson unification at the type level (Unifier.java). Term-level extends this to runtime values. Same algorithm, different domain. Needed for Datalog, Prolog-style search, and Curry narrowing.

**Depends on**: (none)
**Enables**: [[datalog-fixpoint.search]], [[curry-narrowing.language]]
**Connections**:
- part-of: [[logic-programming.hub]]
- extends: [[unification-algorithm.types]] -- same Robinson algorithm, type level to term level
- uses: [[logic-variables.search]] -- unification binds logic variables
- enables: [[datalog-fixpoint.search]] -- rule matching uses unification
- enables: [[curry-narrowing.language]] -- narrowing uses unification to invert functions
- analogous-to: [[meta-variables.types]] -- type metas are logic variables at the type level; term logic variables are the same at the value level
- tension-with: [[distributed-continuations.obstacle]] -- distributed unification bindings face spanning problem
