---
tags: [coordination, channels, fault-tolerance, implemented]
refs:
  - adr:D-047
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Fire-and-Forget Send Semantics

Send semantics: transport response sent as soon as the message is accepted, before closure evaluation. Two error classes: delivery errors (initiator's concern) and closure evaluation errors (target node's concern, logged locally). Future: `send` returns a `Result` value once sum types land.

**Depends on**: [[send.coordination]]
**Enables**: [[inbox.infrastructure]]
**Connections**:
- related: matches pi-calculus asynchronous output semantics — decouples transport response from evaluation lifecycle
- related: [[result-types.types]] — future: `send` returns a `Result` value once sum types land, surfacing delivery errors to the caller
