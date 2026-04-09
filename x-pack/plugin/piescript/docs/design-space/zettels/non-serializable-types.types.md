---
tags: [types, infrastructure, serialization, resources, implemented, safety, concept]
refs:
  - adr:D-050
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:ValueSerialization.java
  - code:Value.java
---
# Non-Serializable Types

`SearcherVal`, `DocRefVal`, and `WriterVal` hold node-local JVM resources (Lucene searchers, shard handles). Serialization attempt throws `IOException` at the [[serialization-boundary.infrastructure]] wire boundary. Attempting to send them across nodes via [[code-mobility.coordination]] fails cleanly.

- `SearcherVal` holds [[searcher-lifecycle.data]] Lucene searcher references
- `WriterVal` holds shard write handles from [[shard-write.data]]
- `DocRefVal` holds document references from [[shard-read.data]]

**Depends on**: [[shard-read.data]], [[shard-write.data]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-d.roadmap]]
- motivates: [[local-kind.types]] — future `Local` kind for type-level serialization prevention; currently enforcement is runtime-only
- constrains: [[code-mobility.coordination]] — non-serializable values fail at the wire boundary when closures are shipped
- uses: [[serialization-boundary.infrastructure]] — IOException thrown at the serialization boundary
