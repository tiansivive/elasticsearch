---
tags: [infrastructure, es-internals, tech-debt, task, known-issue]
refs:
  - adr:D-055
  - code:server/src/main/java/org/elasticsearch/TransportVersion.java
  - code:ValueSerialization.java
  - code:CoreExprSerialization.java
  - code:TypeSerialization.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transport Versioning

No `TransportVersion` guards on [[serialization.infrastructure]] — all Value/CoreExpr/Type serialization writes and reads without version checks. Elasticsearch’s **`TransportVersion`** type (see `refs`) is what mixed-version clusters use elsewhere; piescript serialization does not participate yet.
- Piescript cannot be safely used in mixed-version clusters
- Fix: add `TransportVersion` fields to serialization methods, gate new variants behind version checks, handle unknown variants gracefully on older nodes
- Affects [[code-mobility.coordination]] since closures are serialized across nodes

**Depends on**: [[serialization.infrastructure]], [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- constrains: [[transport-layer.es]] — same wire path as internal send / eval responses
- part-of: [[serialization-boundary.infrastructure]] — versioning is part of the wire boundary story
- constrains: [[code-mobility.coordination]] — closures sent across nodes need version-safe serialization
