---
tags: [infrastructure, serialization, implemented, safety]
refs:
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Serialization Boundary

The wire boundary between nodes determines what can travel in closures. 12 `Value` variants are serializable (stable byte tags). `SearcherVal`, `DocRefVal`, `WriterVal` throw `IOException`. `PageVal`, `ExchangeSinkVal`, `ExchangeSourceVal` are node-local. `ExchangeVal` IS serializable (descriptor only). No `TransportVersion` guards (tech debt). Deserialized `CoreExpr` nodes use synthetic `WIRE_SOURCE`.

**Depends on**: [[serialization.infrastructure]]
**Enables**: [[code-mobility.coordination]], [[local-kind.types]]
**Connections**:
- related: [[local-kind.types]] — serialization boundary is where the type system's guarantees meet the transport layer's constraints; currently runtime-enforced, Local kind would make it compile-time
- related: [[transport-versioning.infrastructure]] — versioning is part of the wire boundary story; no version guards is tech debt
