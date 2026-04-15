---
tags: [language, control-flow, recursion, iteration, implementation, now]
refs:
  - plan:recursion_phase1
  - session:a4c44992-3966-4627-a399-19f52f7da836
  - thread:language-expressiveness
---
# Recursion Phase 1 Implementation

Implementation of implicit recursion (tying the knot + guarded recursion) and fused
loop-match (`loop`/`repeat` with `Repeat a` TCon).

**Depends on**: [[pattern-matching.hub]], [[recursion.hub]]
**Enables**: [[composite-paging.data]]
**Connections**:
- implements: [[implicit-recursion.design]] — tying the knot + guarded recursion check
- implements: [[fused-loop-match.language]] — loop/repeat with Repeat a TCon
- implements: [[tying-the-knot.technique]] — mutable slot + backpatch in evaluator
- implements: [[guarded-recursion.technique]] — static rejection of unguarded self-references
- implements: [[repeat-tcon.types]] — Repeat a builtin TCon for type-level enforcement
- uses: [[core-match.language]] — loop arms reuse Alternative/Pattern from match
- uses: [[cps-evaluation.language]] — RepeatVal flows through CPS listener chain
- tracked-by: [[recursion-phase1.queue]]
