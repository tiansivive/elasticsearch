---
tags: [roadmap, data, streaming, implemented, ready, now]
refs:
  - adr:D-054
  - plan:compute_engine_streaming_f5db78f2
  - plan:compute_engine_zettels_8b517c82
  - thread:data-completeness
---
# Block G — Streaming Data Access via Compute Engine

Columnar Pages, Exchange protocol, shard-stream, opaque-typed pages, materialization boundary, type stack, and handle/descriptor split.

**Depends on**: [[block-f.roadmap]]
**Enables**: [[block-h.roadmap]]
**Connections**:
- part-of: [[roadmap-hub.roadmap]]
- subsumes: [[compute-engine.es]] — Elasticsearch `org.elasticsearch.compute` design-space hub
- subsumes: [[exchange-streaming.infrastructure]]
- subsumes: [[exchange-orchestration.infrastructure]]
- subsumes: [[shard-stream.data]]
- subsumes: [[page-opaque-typed.data]]
- subsumes: [[materialization-boundary.data]]
- subsumes: [[type-stack.data]]
- subsumes: [[handle-descriptor-split.pattern]]
