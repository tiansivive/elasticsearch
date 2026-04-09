---
tags: [data, lucene, concept, decision, implemented]
refs:
  - code:EvalShard.java
---
# Queries as Records

Lucene queries are represented as plain piescript [[record-type.language|records]] rather than [[adts.types|ADTs]] or `NamedWriteableRegistry` types. A query is just a `RecordVal` with fields like `{ type: "term", field: "status", value: "active" }`. Conversion to actual `QueryBuilder` instances happens inside `Shard.open`, at the shard boundary. This keeps the [[core-ir.language|Core IR]] and the [[evaluator.language|evaluator]] completely free of Lucene/ES cluster-state dependencies -- queries are ordinary values that can be [[serialization.infrastructure|serialized]], sent across nodes, and manipulated with standard record operations before they ever touch Lucene.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[block-d.roadmap]]
- part-of: [[shard-read.data]] -- record-based queries are the input to Shard.open
- contrasts-with: [[adts.types]] -- records used instead of ADTs for query representation; avoids needing sum types in the type system
- uses: [[record-type.language]] -- queries are plain RecordVal instances
- complements: [[lucene-query-builders.es-internals]] -- RecordVal is converted to QueryBuilder at the shard boundary
