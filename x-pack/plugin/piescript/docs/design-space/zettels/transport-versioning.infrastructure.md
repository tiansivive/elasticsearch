---
tags: [infrastructure, es-internals, tech-debt]
refs:
  - adr:D-055
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transport Versioning

No `TransportVersion` guards on serialization — all Value/CoreExpr/Type serialization writes and reads without version checks. This means piescript cannot be safely used in mixed-version clusters. The fix: add `TransportVersion` fields to serialization methods, gate new variants behind version checks, and handle unknown variants gracefully on older nodes.

**Depends on**: [[serialization.infrastructure]], [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: [[serialization-boundary.infrastructure]] — versioning is part of the wire boundary story
