---
tags: [types, language, control-flow, pattern-matching, needs-design, later, concept]
refs:
  - thread:language-expressiveness
  - thread:error-handling
---
# Exhaustiveness Checking

Compile-time verification that match arms cover all possible values of the scrutinee type. Without it, an unmatched value falls through to a runtime `EvaluationException` -- the type checker can't guarantee totality.

Coverage analysis depends on knowing the value space:
- **Boolean**: exactly 2 values (`true`, `false`). Trivial.
- **ADTs**: finite set of constructors. Standard algorithm (Maranget 2007).
- **Records**: always matched by a single wildcard or record pattern (open rows complicate this -- the tail is unknown).
- **Keyword/Double**: infinite domains. Only literal patterns + wildcard. Wildcard required for totality.
- **Lists**: `[]` and `x :: xs` cover all cases (with recursive descent).

Without [[adts.types]], exhaustiveness is limited to Boolean two-case checks and wildcard requirements. ADTs unlock the full algorithm. Related to [[result-types.types]] because `Option` and `Result` need exhaustive matching to be ergonomic -- forcing users to handle `None`/`Err` at the type level.

**Depends on**: [[pattern-matching.hub]], [[adts.types]]
**Enables**: (none directly -- quality-of-life improvement)
**Connections**:
- part-of: [[pattern-matching.hub]] -- exhaustiveness is a property of the match expression
- uses: [[adts.types]] -- constructor coverage needs ADT declaration info to enumerate constructors
- informs: [[result-types.types]] -- Option/Result need exhaustive matching to enforce error handling
- informs: [[type-narrowing.types]] -- exhaustiveness analysis and type narrowing share the same value-space reasoning
- complements: [[match-type-checking.language]] -- match type checking assigns types to arms; exhaustiveness checks coverage
- tension-with: [[row-polymorphism.types]] -- open row tails make record exhaustiveness undecidable (tail could contain anything)
- analogous-to: [[branch-merging-optimization.performance]] -- both analyze match arms; exhaustiveness for correctness, merging for performance
