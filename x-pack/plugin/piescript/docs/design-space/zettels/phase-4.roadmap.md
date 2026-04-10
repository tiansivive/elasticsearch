---
tags: [roadmap, archived, superseded, coordination]
refs:
  - adr:D-040
  - doc:archive/phase4_process_primitives.plan.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Phase 4 — Process Primitives & Par Composition (Archived)

The original Phase 4 design: `par` blocks as the coordination primitive for independent concurrent bindings. Included `CoreProcess` IR hierarchy separate from `CoreExpr`, implicit channels via `par` scope, and a `ParPlanNode` in the plan graph. Design questions covered channel typing (implicit vs explicit), input-guarded choice, and process composition patterns.

Superseded by D-040 (Join Calculus redesign). `spawn` + `when` are strictly more expressive than `par` — they support multi-way synchronization, reaction rules, and streaming coordination that `par` cannot express.

**Depends on**: (none)
**Enables**: (none — archived)
**Connections**:
- replaced-by: [[spawn.coordination]] — spawn subsumes par's fork semantics
- replaced-by: [[when-synchronization.coordination]] — when subsumes par's synchronization
- replaced-by: [[join-calculus.coordination]] — the theoretical model that replaced the par approach
- related: [[par-blocks.coordination]] — the par primitive concept itself
