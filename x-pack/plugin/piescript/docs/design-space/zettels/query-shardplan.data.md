---
tags: [data, types, typeclasses, push-down, open, concept, feature]
refs: []
---
# Query ShardPlan

Level 2 of the [[data-access-hierarchy]]: declarative but shard-local query combinators executed inside shipped closures via [[code-mobility.coordination]]. [[query-typeclass.data]] instances decide execution strategy: push predicates into [[lucene-query-builders.es-internals]], compile folds into [[lucene-collectors.es-internals]], fuse map/filter chains. The user writes declarative combinators that execute on a specific shard, with [[push-down-compilation.performance]] handled by [[typeclasses.types|typeclass]] resolution.

**Depends on**: [[query-typeclass.data]], [[lucene-m.data]], [[code-mobility.coordination]]
**Enables**: [[push-down-compilation.performance]]
**Connections**:
- part-of: [[data-access-architecture.roadmap]]
- part-of: [[data-access-hierarchy]] — Level 2 in the data access hierarchy
- contrasts-with: [[esql-compilation.esql]] — shard-local vs cluster-wide
- related: [[code-mobility.coordination]] — runs inside shipped closures
- optimized-by: [[lucene-query-builders.es-internals]] — predicates push down to Lucene queries
- optimized-by: [[lucene-collectors.es-internals]] — folds compile to Collectors
- related: [[t-linq.esql]] — T-LINQ normalization determines valid expressions per backend
