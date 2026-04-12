---
tags: [language, syntax, coordination, decided, rejected, concept, pi-calculus]
refs:
  - adr:D-041
---
# Join Keyword Rejection

The keyword `join` was considered and rejected for channel synchronization in piescript. In Join Calculus terminology, a "join pattern" synchronizes on multiple channels simultaneously. The natural keyword would be `join`, but this collides with SQL/ESQL `JOIN` (as in `LOOKUP JOIN`, `CROSS JOIN`, etc.) -- a concept piescript users encounter constantly.

`when` was chosen instead (D-041): `when (ch1 x) & (ch2 y) -> body`. The `when` keyword reads naturally as "when these channels have values, execute the body" and avoids the SQL collision entirely. The `&` operator between channel patterns mirrors the Join Calculus's simultaneous pattern notation.

**Depends on**: [[join-calculus.coordination]]
**Enables**: (none directly)
**Connections**:
- rejected-in-favor-of: [[when-synchronization.coordination]] -- `when` is the keyword chosen for channel synchronization
- implements: D-041 -- the decision is recorded in ADR D-041
- informs: [[join-calculus.coordination]] -- the theoretical model uses "join" but the surface syntax uses "when"
- tension-with: [[join.esql]] -- ESQL LOOKUP JOIN uses "join" in its natural SQL sense; keyword collision was the motivation
- complements: [[antlr-grammar.language]] -- `when` is a reserved keyword in the grammar
- related: [[esql-combinators.esql]] -- ESQL combinators include join operations that use the SQL sense of "join"
