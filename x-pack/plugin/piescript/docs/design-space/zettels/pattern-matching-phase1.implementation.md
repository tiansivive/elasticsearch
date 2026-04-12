---
tags: [language, control-flow, implementation, implemented]
refs:
  - plan:pattern_matching_phase1
  - thread:error-handling
  - thread:language-expressiveness
---
# Pattern Matching Phase 1 Implementation

Implementation of Phase 1 of pattern matching: `match` expressions as the primary control-flow primitive, `if/then/else` as sugar over Boolean match, with literal/variable/wildcard/record/list patterns.

**Scope:**
- Covers basic pattern matching: literal, variable, wildcard, record (open-row), exact-length list, and cons-list (`[h | t]`).
- Defers ADTs, exhaustiveness checking, and pattern destructuring in lambda/when/let.

**Depends on**: [[pattern-matching.hub]]
**Enables**: (none directly)
**Connections**:
- implements: [[pattern-matching.hub]]
- implements: [[core-match.language]]
- implements: [[match-syntax.language]]
- implements: [[pattern-types.language]]
- implements: [[match-type-checking.language]]
