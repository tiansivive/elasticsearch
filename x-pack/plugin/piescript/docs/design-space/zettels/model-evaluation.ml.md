---
tags: [ml, data-processing, exploration, concept]
refs: []
---
# Model Evaluation

Compute precision/recall/F1/AUC over predictions vs ground truth. This is a
custom aggregation — ship a fold to each shard, merge partial results on the
coordinator. Exactly the pattern that `Query ShardPlan` with a custom fold
enables. No ML-specific features needed; this falls out of piescript's
general-purpose shard-local computation model.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[ml-workflow-integration.ml]]
- uses: [[query-shardplan.data]] — shard-local folds are the execution mechanism
- related: [[map-reduce.distributed]] — model evaluation is a map-reduce pattern
