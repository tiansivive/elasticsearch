---
tags: [roadmap, external, lifecycle, designed, superseded]
refs:
  - doc:vision.md
  - vision:external-interaction-model
---
# External Interaction Model

How piescript programs interact with the outside world. Three layers: actor model (script lifecycle, REST skin over channels), plugin SPI (typed builtins from Java via `PiescriptExtension`), FFI (Painless allowlist for ad-hoc JVM access).

**Connections**:
- superseded-by: [[external-interaction.thread]] — thread-based replacement
- part-of: [[vision-hub.roadmap]]
- subsumes: [[actor-model.lifecycle]]
- subsumes: [[named-channels.lifecycle]]
- subsumes: [[sse-streaming.external]]
- subsumes: [[plugin-spi.external]]
- subsumes: [[ffi-painless.external]]
- subsumes: [[token-capability-security.security]]
- subsumes: [[watcher-replacement.external]]
- subsumes: [[transform-unification.external]]
