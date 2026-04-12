---
tags: [queue, implementation]
refs:
  - plan:pattern_matching_phase1
---
# Pattern Matching Phase 1 Queue

Queue tracking the implementation steps for Pattern Matching Phase 1.

## Steps

- [x] 1. **Zettels first** -- create the implementation and queue zettels to track progress
- [x] 2. **IR layer** (Pattern + CoreMatch + Alternative) -- pure data, immediately testable
- [x] 3. **Evaluator** (EvalMatch) -- can test with hand-built CoreMatch nodes before parser/elaborator
- [x] 4. **Grammar** -- add ANTLR rules, regenerate
- [x] 5. **Elaborator** (Matches.java + desugarIf) -- full pipeline testable
- [x] 6. **Serialization** -- needed for cross-node match expressions
- [x] 7. **CorePrinter** -- dev endpoint support
- [x] 8. **Tests** -- unit tests alongside each step; integration tests at the end
- [x] 10. **Docs and zettels** -- close the loop



**Depends on**: [[pattern-matching.hub]]
**Enables**: (none directly)
**Connections**:
- part-of: [[pattern-matching.hub]]
- implements: [[pattern-matching-phase1.implementation]]

