---
tags: [es-internals, compute-engine, documentation, concept, implemented]
refs:
  - adr:D-054
  - plan:compute_engine_streaming_f5db78f2
  - plan:compute_engine_zettels_8b517c82
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/data/Page.java
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/operator/Driver.java
---
# Elasticsearch Compute Engine

Elasticsearch’s **columnar compute runtime** lives in **`org.elasticsearch.compute`**. It processes analytics-style workloads using **ref-counted columnar batches** (`Page` of `Block`s), **single-threaded driver** loops that chain **`Operator`** instances and pass **`Page`s** from source to sink, and **exchange** machinery to move pages between concurrent tasks (same node or remote) with backpressure. ESQL’s query execution builds on this stack; see the `Driver` class Javadoc and the `org.elasticsearch.compute` package documentation (refs below).

Piescript touches this stack where it builds or moves **`Page`s** (e.g. [[shard-stream.data]]) and where it wraps **`ExchangeService`** for streaming ([[exchange-streaming.infrastructure]]). The [[type-stack.data]] places Page/Block between raw Lucene access and piescript `Value`s.

**Depends on**: (none)

**Enables**: [[compute-data-page-block.es]], [[compute-exchange-service.es]], [[compute-driver.es]]

**Connections**:
- subsumes: [[compute-data-page-block.es]] — columnar data types
- subsumes: [[compute-exchange-service.es]] — exchange protocol
- subsumes: [[compute-driver.es]] — operator-chain execution
- informs: [[block-g.roadmap]] — Block G streaming and piescript builtins
- documents: [[exchange-streaming.infrastructure]] — piescript side of exchange
- documents: [[shard-stream.data]] — DocRef batches to `Page`
- complements: [[type-stack.data]] — Page/Block tier
- related: [[blockloader.data]] — optional optimized column reads (not yet wired in piescript)
- constrains: [[circuit-breaker.infrastructure]] — `BlockFactory` / breaker policy on shard stream
- related: [[doc-values.es-internals]] — Lucene doc values feed columnar materialization
- related: [[lucene-collectors.es-internals]] — `LeafCollector` / `CollectorManager` usage in ESQL Lucene operators under the driver
