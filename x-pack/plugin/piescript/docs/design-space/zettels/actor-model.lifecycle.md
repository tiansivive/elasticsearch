---
tags: [lifecycle, external, designed, feature, concept, needs-design, someday]
refs:
  - vision:external-interaction-model
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:distributed-coordination
  - thread:external-interaction
---
# Actor Model Lifecycle

A piescript program as a persistent actor with identity:

- Submit via PUT, poll result via GET, send input via POST [[inbox.infrastructure]], cancel via DELETE
- [[named-channels.lifecycle]] exposed as HTTP endpoints
- [[token-capability-security.security]]-based access
- REST layer is HTTP skin over [[send.coordination]]/[[when-synchronization.coordination]]

**Depends on**: [[scheduled-execution.lifecycle]], [[channels.infrastructure]]
**Enables**: [[named-channels.lifecycle]], [[sse-streaming.external]]
**Connections**:
- part-of: [[external-interaction-model.roadmap]]
- part-of: external interaction model (Layer 1) — scripts persist beyond single request/response
- related: [[inbox.infrastructure]] — inbox is the persistent per-node channel that receives actor messages
- related: [[send.coordination]], [[when-synchronization.coordination]] — REST verbs map to send/when primitives
- complements: [[otp-supervision.coordination]] — supervision trees manage actor fault tolerance and restarts
- related: [[token-capability-security.security]] — token-based capability access for actor endpoints
- extends: [[eval-endpoint.infrastructure]] — evolves the single request/response eval endpoint into persistent actor lifecycle
