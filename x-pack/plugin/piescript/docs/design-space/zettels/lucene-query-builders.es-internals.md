---
tags: [es-internals, lucene, data, documentation]
refs: []
---
# Lucene Query Builders

ES wraps Lucene queries in `QueryBuilder` objects (`TermQueryBuilder`, `BoolQueryBuilder`, `RangeQueryBuilder`, etc.) which serialize to/from JSON and compile to Lucene `Query` objects. Piescript's [[queries-as-records.data]] represents queries as plain records and converts to `QueryBuilder` at the shard boundary via [[shard-read.data]].

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- used-by: [[queries-as-records.data]] — piescript records convert to QueryBuilders
- used-by: [[shard-read.data]] — Shard.open compiles QueryBuilder to Lucene Query
- enables: [[push-down-compilation.performance]] — predicate push-down compiles to QueryBuilders
