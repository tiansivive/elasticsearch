---
tags: [distributed, performance, known-issue, problem]
refs:
  - adr:D-039
---
# Coordinator-Bound Compute

All map/filter/reduce currently runs on the coordinator node.

- Data travels from data nodes (via [[shard-read.data]] or ESQL query), gets materialized into Values, and then the coordinator's [[evaluator.language]] processes it
- The coordinator bears both the network cost of receiving all data and the compute cost of transforming it
- For large datasets this is a bottleneck: the coordinator becomes a single-threaded funnel
- The [[data-locality.distributed]] pattern (ship closures to data nodes) is the designed solution, but today no computation is pushed to data nodes — only data acquisition happens there

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- motivates: [[data-locality.distributed]] — shipping closures to data nodes is the direct response
- motivates: [[push-down-compilation.performance]] — push-down moves filter/project to ESQL or Lucene, avoiding coordinator processing
- tradeoff-with: [[eager-materialization.data]] — eager materialization compounds the problem by expanding all data to Values before processing
