---
tags: [types, infrastructure, serialization, resources, implemented, safety]
refs:
  - adr:D-050
  - adr:D-051
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Non-Serializable Types

`SearcherVal`, `DocRefVal`, and `WriterVal` hold node-local JVM resources (Lucene searchers, shard handles). Serialization attempt throws `IOException`. Enforced at the wire boundary. Attempting to send them across nodes fails cleanly.

**Depends on**: [[shard-read.data]], [[shard-write.data]]
**Enables**: (none directly)
**Connections**:
- related: [[local-kind.types]] — future `Local` kind for type-level serialization prevention; currently enforcement is runtime-only
- related: [[code-mobility.coordination]] — non-serializable values fail at the wire boundary when closures are shipped
