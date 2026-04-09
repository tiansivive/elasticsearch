---
tags: [infrastructure, es-internals, tech-debt, columnar, task, known-issue]
refs:
  - adr:D-054
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Circuit Breaker Integration

Real circuit breaker integration for [[shard-stream.data]] `BlockFactory` (replaces `NoopCircuitBreaker`).

- The current implementation uses a `NoopCircuitBreaker` when building Blocks in the shard stream path
- This needs to be replaced with proper circuit breaker accounting to prevent OOM under large result sets or concurrent queries

**Depends on**: [[shard-stream.data]]
**Enables**: (none)
**Connections**:
- constrains: [[exchange-streaming.infrastructure]] — Exchange also needs memory accounting
- constrains: [[shard-stream.data]] — current shard stream path uses `NoopCircuitBreaker`
