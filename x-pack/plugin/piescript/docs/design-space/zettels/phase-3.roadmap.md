---
tags: [roadmap, archived, superseded, performance, streaming]
refs:
  - adr:D-012
  - adr:D-040
  - doc:archive/phase3_stream_runtime.plan.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Phase 3 — Stream Runtime & Plan Graph (Archived)

The original Phase 3 design: a plan graph architecture where process nodes (`CoreProcess`) produce a DAG of distributed operations (a free monad over π-calculus effects), optimized and dispatched by a separate executor. Included stream combinators as plan nodes, a v0 local executor, push-down compilation (map→EVAL, filter→WHERE), and mobility checking for closures.

Superseded by D-040 (Join Calculus redesign). The optimization benefits required significant compiler engineering not immediately justified. The plan graph indirection provided hooks for work better scoped as dedicated blocks. Replaced by Blocks A through E with direct interpretation via `SubscribableListener` channels.

**Depends on**: (none)
**Enables**: (none — archived)
**Connections**:
- replaced-by: [[block-a.roadmap]] — local async coordination
- replaced-by: [[block-b.roadmap]] — topology
- replaced-by: [[block-c.roadmap]] — cross-node code execution
- replaced-by: [[block-d.roadmap]] — local data access
- related: [[plan-graph.language]] — the plan graph concept itself
- related: [[free-monad.types]] — the theoretical perspective is preserved
