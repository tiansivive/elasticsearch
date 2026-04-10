---
tags: [coordination, channels, fault-tolerance, implemented, decision, concept, later]
refs:
  - adr:D-047
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:error-handling
---
# Fire-and-Forget Send Semantics

[[send.coordination]] semantics: transport response sent as soon as the message is accepted, before [[closure-val.language]] evaluation.
- Two error classes: delivery errors (initiator's concern) and closure evaluation errors (target node's concern, logged locally)
- Future: `send` returns a [[result-types.types]] value once sum types land

**Depends on**: [[send.coordination]]
**Enables**: [[inbox.infrastructure]]
**Connections**:
- part-of: [[block-c.roadmap]]
- inspired-by: [[join-calculus.coordination]] — matches pi-calculus asynchronous output semantics; decouples transport response from evaluation lifecycle
- prerequisite-for: [[result-types.types]] — future: `send` returns a `Result` value once sum types land, surfacing delivery errors to the caller
