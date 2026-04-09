---
tags: [es-internals, lucene, data, performance, documentation]
refs: []
---
# Lucene Collectors

Lucene's push-based processing model. A `Collector` receives doc IDs as they match a query -- no materialization needed. Used by ESQL's compute engine for aggregations and top-N.

- Push model contrasts with piescript's current pull model ([[shard-stream.data]] `Shard.consume` iterates)
- [[query-shardplan.data]] could compile folds into Collectors
- ESQL uses Collectors internally for [[esql-aggregates.esql]] and top-N

**Depends on**: (none)
**Enables**: [[query-shardplan.data]]
**Connections**:
- contrasts-with: [[doc-id-set-iterator.es-internals]] — push vs pull
- enables: [[query-shardplan.data]] — ShardPlan compiles to Collectors
- used-by: [[esql-compilation.esql]] — ESQL uses Collectors internally
- contrasts-with: [[shard-stream.data]] — piescript's current pull-based iteration model
