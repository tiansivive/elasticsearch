---
tags: [language, syntax, implementation, decision]
refs:
  - adr:D-010
---
# If as Match Sugar

`if/then/else` is syntactic sugar over a Boolean `match` expression.

`if c then a else b` desugars to:
`CoreMatch(c, [Alternative(LitPat(true), a), Alternative(LitPat(false), b)])`

**Depends on**: [[pattern-matching.hub]]
**Enables**: (none directly)
**Connections**:
- part-of: [[pattern-matching.hub]]
- implements: D-010
