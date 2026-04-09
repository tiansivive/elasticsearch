---
tags: [infrastructure, serialization, implemented, safety, documentation]
refs:
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:ValueSerialization.java
  - code:CoreExprSerialization.java
---
# Serialization Boundary

The wire boundary between nodes determines what can travel in [[closure-val.language|closures]].

- 12 `Value` variants are serializable (stable byte tags).
- `SearcherVal`, `DocRefVal`, `WriterVal` throw `IOException`.
- `PageVal`, `ExchangeSinkVal`, `ExchangeSourceVal` are node-local.
- `ExchangeVal` IS serializable ([[handle-descriptor-split.pattern|descriptor]] only).
- No `TransportVersion` guards (tech debt). Deserialized [[core-ir.language|CoreExpr]] nodes use synthetic `WIRE_SOURCE`.

**Depends on**: [[serialization.infrastructure]]
**Enables**: [[code-mobility.coordination]], [[local-kind.types]]
**Connections**:
- motivates: [[local-kind.types]] — serialization boundary is where the type system's guarantees meet the transport layer's constraints; currently runtime-enforced, Local kind would make it compile-time
- part-of: [[transport-versioning.infrastructure]] — versioning is part of the wire boundary story; no version guards is tech debt
- uses: [[non-serializable-types.types]] — non-serializable types are the enforcement mechanism at this boundary
