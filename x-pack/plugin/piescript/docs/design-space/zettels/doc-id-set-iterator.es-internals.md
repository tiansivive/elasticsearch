---
tags: [es-internals, lucene, data, documentation]
refs: []
---
# Doc ID Set Iterator

Lucene's pull-based iteration primitive.

- `DocIdSetIterator.nextDoc()` returns the next matching doc ID, or `NO_MORE_DOCS` when exhausted
- [[shard-read.data]] `Shard.consume` wraps this pattern — iterate up to N docs synchronously
- The iterator is the foundation of Lucene's search execution

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- used-by: [[shard-read.data]] — Shard.consume wraps DISI iteration
- part-of: [[index-searcher.es-internals]] — queries produce DocIdSetIterators
