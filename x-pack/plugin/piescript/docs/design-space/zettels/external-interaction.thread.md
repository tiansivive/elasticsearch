---
tags: [thread, roadmap, external, lifecycle]
refs: []
---
# External Interaction

How piescript programs interact with the outside world — other services, users,
and systems beyond Elasticsearch. Three layers: plugin SPI (typed builtins from
Java), FFI (Painless allowlist for ad-hoc JVM access), and the actor model
(persistent script identity with REST-exposed channels). Culminates in unifying
Transforms, Watcher, and ingest pipelines under a single typed language.

## Sequence

1. **Plugin SPI** [[plugin-spi.external]] — needs-design
   `PiescriptExtension` interface for typed builtins from Java plugins.
   Kafka, HTTP, ML inference, custom connectors.

2. **FFI via Painless** [[ffi-painless.external]] — needs-design
   `Java.call` gated by Painless allowlist. Quick prototyping, math/crypto.

3. **Actor model** [[actor-model.lifecycle]] — needs-design
   Persistent script identity. PUT/GET/DELETE lifecycle.
   _Shared with: distributed-coordination_

4. **Named channels** [[named-channels.lifecycle]] — needs-design
   Exposed channels as HTTP endpoints. `expose!` primitive.
   Depends on: [[actor-model.lifecycle]]
   _Shared with: distributed-coordination_

5. **SSE streaming** [[sse-streaming.external]] — needs-design
   Server-Sent Events for incremental output to clients (e.g. Kibana).
   Depends on: [[named-channels.lifecycle]], [[multi-value-channels.coordination]]

6. **Token capability security** [[token-capability-security.security]] — needs-design
   Object-capability access to running scripts via opaque tokens.
   Depends on: [[actor-model.lifecycle]]

7. **Transform unification** [[transform-unification.external]] — exploration
   Replace Transforms with typed piescript programs.
   Depends on: [[scheduled-execution.lifecycle]]

8. **Watcher replacement** [[watcher-replacement.external]] — exploration
   Replace Watcher with piescript `when` + `send` + scheduling.
   Depends on: [[scheduled-execution.lifecycle]]

9. **Ingest-time execution** [[ingest-time-execution.lifecycle]] — exploration
   Run piescript at ingest time per-document. Replace ingest pipelines.

10. **Enrich unification** [[enrich-unification.external]] — exploration
    Replace enrich processors with piescript cross-index queries.

11. **Feature constellation** [[feature-constellation.external]] — exploration
    The overarching fragmentation problem that this thread solves.

**Depends on**: (none — root thread)
**Enables**: (none directly)
**Connections**:
- includes: [[plugin-spi.external]]
- includes: [[ffi-painless.external]]
- includes: [[actor-model.lifecycle]]
- includes: [[named-channels.lifecycle]]
- includes: [[sse-streaming.external]]
- includes: [[token-capability-security.security]]
- includes: [[transform-unification.external]]
- includes: [[watcher-replacement.external]]
- includes: [[ingest-time-execution.lifecycle]]
- includes: [[enrich-unification.external]]
- includes: [[feature-constellation.external]]
- replaces: [[external-interaction-model.roadmap]] — thread-based replacement of the old roadmap hub
- related: [[distributed-coordination.thread]] — shares actor model, named channels
