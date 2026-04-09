---
tags: [performance, open, concept, exploration]
refs: []
---
# Branch Merging Optimization

Merging two parallel branches that query the same index with different filters into one query with a discriminating column.

- A plan-level optimization on the [[free-monad.types]] residual: when two spawned ESQL queries target the same index, the optimizer rewrites them into a single query with a synthetic tag column
- The tag column distinguishes which branch each row belongs to, then the result is demultiplexed
- Reduces shard fan-out at the cost of wider rows

**Depends on**: [[lowering-pass.performance]], [[free-monad.types]]
**Enables**: (none directly)
**Connections**:
- part-of: [[lowering-pass.performance]] — a rewrite rule in the plan-level optimization pass
- related: [[free-monad.types]] — operates on the free monad residual tree
- analogous-to: [[combinator-fusion.performance]] — both are plan-level rewrites that reduce query count
