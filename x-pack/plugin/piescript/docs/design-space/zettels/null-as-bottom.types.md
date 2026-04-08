---
tags: [types, implemented, tech-debt, unification]
refs:
  - adr:D-007
  - adr:D-027
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Null as Bottom

v0: `Null` unifies with every type. Known unsound — a `NullVal` can appear where a `Double` is expected. `NullVal` in arithmetic throws `EvaluationException` at runtime (D-027). Proper fix requires Option/Maybe type which requires ADTs and pattern matching.

**Depends on**: [[hindley-milner.types]]
**Enables**: (none directly)
**Connections**:
- related: [[adts.types]] — blocks on ADTs for proper resolution
- related: [[pattern-matching.language]] — blocks on pattern matching for proper resolution
- related: [[unification-algorithm.types]] — Null unifies with every type as a special case in the unifier
- related: [[result-types.types]] — Option/Maybe type is the proper fix for nullable values
