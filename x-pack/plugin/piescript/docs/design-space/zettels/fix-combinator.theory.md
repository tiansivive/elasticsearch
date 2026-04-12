---
tags: [language, control-flow, theoretical, concept, rejected, recursion, fixpoint]
refs: []
---
# Fix Combinator

`fix : (a -> a) -> a`. The standard fixed-point combinator for encoding recursion. In
strict languages needs the "strict fix" wrapper: `fix f = f (\x. fix f x)` — the eta
expansion delays the recursive call.

Stack safety comes from the evaluator, not from fix itself — a trampolined evaluator makes
fix stack-safe. Without trampolining, `fix` in a strict language overflows just like any
other unbounded recursion.

Rejected as surface syntax — too abstract for the target audience (security engineers
writing detection rules). Valid as an internal elaboration target for whatever surface
syntax is chosen.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[recursion.hub]]
- rejected-in-favor-of: [[implicit-recursion.design]] — for surface syntax
- rejected-in-favor-of: [[fused-loop-match.language]] — for iteration
- tension-with: [[stack-depth.language]] — stack safety depends on evaluator, not on fix itself
- uses: [[trampolining.technique]] — trampolining makes fix stack-safe
