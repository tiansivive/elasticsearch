---
tags: [search, theoretical, concept]
refs:
  - doc:references.md
---
# Stratified Negation

Datalog extension allowing negation in rule bodies, stratified so negated predicates are fully computed before being negated. Ensures termination and well-defined semantics for non-monotone operations. Without it, Datalog is monotone-only (no "NOT in set X" queries). Stratification = a topological ordering of predicates by negation dependencies.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[logic-programming.hub]]
- extends: [[datalog-fixpoint.search]] -- adds safe negation to fixpoint
- tension-with: [[calm-theorem.types]] -- negation is non-monotone; requires coordination in distributed setting
- uses: [[termination-analysis.theory]] -- stratification is a termination guarantee for non-monotone rules
