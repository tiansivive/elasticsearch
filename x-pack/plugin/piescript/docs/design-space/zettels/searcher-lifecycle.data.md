---
tags: [data, lucene, resources, implemented, concept]
refs:
  - code:SearcherState.java
  - code:EvalShard.java
---
# Searcher Lifecycle

Resource lifecycle management for [[index-searcher.es-internals]] acquisitions. `Shard.open` acquires a `SearcherVal` (wrapping `IndexSearcher` + `SearcherState`) delivered via [[channels.infrastructure]]. [[non-serializable-types.types|Non-serializable]] -- node-local only. [[lucene-m.data]]'s interpreter would manage acquisition and release automatically; current Block D exposes it as a manual acquire-use pattern. [[bracket-patterns.language]] would provide syntactic support for safe resource lifecycle.

**Depends on**: [[shard-read.data]], [[non-serializable-types.types]], [[channels.infrastructure]]
**Enables**: [[lucene-m.data]]
**Connections**:
- part-of: [[block-d.roadmap]]
- motivates: [[bracket-patterns.language]] — bracket patterns would automate lifecycle
- part-of: [[lucene-m.data]] — LuceneM interpreter manages this automatically
- uses: [[index-searcher.es-internals]] — SearcherVal wraps IndexSearcher
- constrains: [[data-access-hierarchy]] — resource lifecycle is critical at Levels 3-4
