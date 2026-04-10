---
tags: [types, implemented, tech-debt, unification, decision, task, concept, known-issue, later]
refs:
  - adr:D-007
  - adr:D-027
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Unifier.java
  - code:Value.java
  - thread:error-handling
---
# Null as Bottom

v0: `Null` unifies with every type. Known unsound -- a `NullVal` can appear where a `Double` is expected. `NullVal` in arithmetic throws `EvaluationException` at runtime (D-027). Proper fix requires Option/Maybe type which requires [[adts.types]] and [[pattern-matching.hub]].

- [[unification-algorithm.types]] has a special case for Null unifying with any type
- [[result-types.types]] `Option`/`Maybe` is the proper fix for nullable values
- Runtime failures surface as `EvaluationException` in [[evaluator.language]]

**Depends on**: [[hindley-milner.types]]
**Enables**: (none directly)
**Connections**:
- motivates: [[adts.types]] — proper resolution requires ADTs (Option/Maybe type)
- motivates: [[pattern-matching.hub]] — proper resolution requires pattern matching for Option/Maybe
- uses: [[unification-algorithm.types]] — Null unifies with every type as a special case in the unifier
- motivates: [[result-types.types]] — Option/Maybe type is the proper fix for nullable values
