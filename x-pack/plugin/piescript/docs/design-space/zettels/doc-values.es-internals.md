---
tags: [es-internals, lucene, columnar, data, documentation]
refs: []
---
# Doc Values

Lucene's column-oriented storage for field values, separate from the inverted index.

- Used for sorting, aggregations, and field retrieval without re-parsing `_source`
- [[shard-read.data]] `Shard.read` reads all doc-value fields from a `DocRef`
- Not all field types have doc values — `text` fields don't

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- used-by: [[shard-read.data]] — Shard.read reads doc-value fields
- used-by: [[shard-stream.data]] — streaming reads via doc values
- used-by: [[blockloader.data]] — BlockLoader abstracts over doc-value access
