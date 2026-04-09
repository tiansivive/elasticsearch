---
tags: [data, write-path, implemented, documentation]
refs:
  - code:EvalWrite.java
---
# RecordVal to XContent

Write-path materialization: converting piescript [[record-type.language|RecordVal]] into JSON suitable for `IndexRequest` source.

- Handles doubles, keywords, booleans, nulls, nested records, and lists by walking the record structure and emitting XContent.
- [[non-serializable-types.types|Non-convertible values]] ([[closure-val.language|closures]], [[channels.infrastructure|channels]], opaque runtime handles) throw at the materialization boundary rather than silently producing garbage.
- This is the reverse direction of [[esql-value-converter.esql|EsqlValueConverter]] -- where that bridge converts ES data into piescript values, this one converts piescript values back into ES-ingestible JSON.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[block-e.roadmap]]
- complements: [[esql-value-converter.esql]] -- reverse direction of the materialization boundary (ES->piescript vs piescript->ES)
- part-of: [[shard-write.data]] -- RecordVal-to-XContent is the serialization step inside `Shard.write`
