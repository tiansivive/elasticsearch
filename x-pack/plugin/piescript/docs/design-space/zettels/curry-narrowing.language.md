---
tags: [language, theoretical, control-flow, pi-calculus]
refs:
  - doc:references.md
  - vision:functional-pattern-matching
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Curry Narrowing

Curry language narrowing-based functional-logic patterns (functions as patterns).

- In Curry, functions can run "backwards" — given a result, the runtime narrows the input space to find values that produce it
- This unifies [[pattern-matching.hub]] and function application: a function definition is simultaneously a pattern that can be matched against
- Combined with [[cham-patterns.coordination]], this enables declarative concurrent reactions

**Depends on**: [[multi-value-channels.coordination]]
**Enables**: (none)
**Connections**:
- part-of: [[future-coordination.roadmap]]
- part-of: [[logic-programming.hub]]
- related: [[cham-patterns.coordination]] — CHAM + Curry = declarative concurrent reactions
- subsumes: [[pattern-matching.hub]] — narrowing generalizes pattern matching
- uses: [[backtracking.search]] — narrowing is goal-directed backtracking
- uses: [[logic-unification.search]] — narrowing inverts functions via unification
