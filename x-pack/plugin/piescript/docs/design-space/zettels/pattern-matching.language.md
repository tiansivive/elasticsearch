---
tags: [language, control-flow, open, feature, task, concept]
refs:
  - adr:D-010
  - adr:D-029
  - roadmap:phase-1e
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Pattern Matching

- Match expressions as the primary control-flow mechanism.
- `if`/`then`/`else` desugars to match on Boolean.
- Requires: match syntax, pattern types (literal, variable, wildcard, constructor), exhaustiveness checking.
- Deferred twice -- not blocking MVP path.

**Depends on**: [[adts.types]]
**Enables**: [[result-types.types]]
**Connections**:
- part-of: [[future-type-system.roadmap]]
- implements: D-010 established match-first philosophy
- implements: D-029 reordered phases ([[row-polymorphism.types|open rows]] before pattern matching)
- complements: [[type-narrowing.types]] — pattern matching is the primary mechanism for type refinement
- prerequisite-for: [[result-types.types]] — Result/Option require exhaustive pattern matching
