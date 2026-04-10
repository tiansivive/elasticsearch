---
tags: [es-internals, lucene, data, performance, documentation, implemented]
refs:
  - code:server/src/main/java/org/elasticsearch/search/query/QueryPhase.java
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/lucene/query/LuceneTopNSourceOperator.java
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/lucene/query/LuceneQueryEvaluator.java
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/lucene/query/LuceneSourceOperator.java
  - resource:https://lucene.apache.org/core/org/apache/lucene/search/Collector.html
---
# Lucene Collectors

Lucene's push-based processing model. A **`Collector`** receives doc IDs as they match a query — no full materialization up front. **`LeafCollector`** scores per-segment work; **`CollectorManager`** (e.g. **`TopScoreDocCollectorManager`**, **`TopFieldCollectorManager`**) coordinates parallel search in modern Lucene.

Elasticsearch's **`QueryPhase`** orchestrates query execution on the search path (see `refs`). The compute engine's **`LuceneTopNSourceOperator`**, **`LuceneQueryEvaluator`**, and **`LuceneSourceOperator`** show ESQL-style **`LeafCollector`** / **`Top*CollectorManager`** usage and operator wiring.

- Push model contrasts with piescript's current pull model ([[shard-stream.data]] `Shard.consume` iterates)
- [[query-shardplan.data]] could compile folds into Collectors
- ESQL uses this stack under [[esql-compilation.esql]] and [[compute-engine.es]] for columnar execution above Lucene

**Depends on**: (none)
**Enables**: [[query-shardplan.data]]
**Connections**:
- contrasts-with: [[doc-id-set-iterator.es-internals]] — push vs pull
- enables: [[query-shardplan.data]] — ShardPlan compiles to Collectors
- used-by: [[esql-compilation.esql]] — ESQL uses Collectors internally
- informs: [[compute-engine.es]] — operators above Lucene collectors / scorers
- contrasts-with: [[shard-stream.data]] — piescript's current pull-based iteration model
