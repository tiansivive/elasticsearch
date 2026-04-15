---
tags: [language, types, control-flow, pattern-matching, open, needs-design, later]
refs:
  - thread:language-expressiveness
---
# Variant-Based Arm Typing

Internal mechanism: wrap match/loop arm codomains in variant tags `#return` and `#repeat`.
The arm body type becomes `< #return: R | #repeat: S >` (closed row variant). Both normal
returns and repeat have the SAME type, so standard match codomain unification works.

The user never sees this variant — it's internal elaboration plumbing. The loop evaluator
unwraps: `#return v` → done, `#repeat s` → loop again with state s.

**Key insight**: `CoreLoop(init, matchExpr)` — loop desugars to match. The variant mechanism
is implemented ONCE in match elaboration; loop gets it for free. Pattern matching and loop
arms collapse into one grammar/IR construct.

Requires row-based Variants ([[adts.types]]) as a prerequisite — can't implement without
sum types in the type system. Lower priority than [[pattern-guards.language]] which solves
the same problem without type system changes.

**Depends on**: [[adts.types]], [[pattern-matching.hub]], [[row-polymorphism.types]]
**Enables**: (none directly)
**Connections**:
- solves: [[mixed-type-branches.obstacle]] — variant type encompasses both R and Repeat S
- extends: [[core-match.language]] — CoreLoop becomes CoreLoop(init, CoreMatch) with variant-tagged arms
- extends: [[fused-loop-match.language]] — enables mixed-type branches in loop arms
- uses: [[row-polymorphism.types]] — variant row `< #return: R | #repeat: S >` is a closed row
- contrasts-with: [[pattern-guards.language]] — variants solve at type level; guards solve at syntax level
- contrasts-with: [[repeat-tcon.types]] — variants replace the Repeat TCon with internal tagging
