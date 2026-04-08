---
tags: [lifecycle, external, designed]
refs:
  - vision:external-interaction-model
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Actor Model Lifecycle

A piescript program as a persistent actor with identity. Submit via PUT, poll result via GET, send input via POST inbox, cancel via DELETE. Named channels exposed as HTTP endpoints. Token-based capability access. REST layer is HTTP skin over send/when.

**Depends on**: [[scheduled-execution.lifecycle]], [[channels.infrastructure]]
**Enables**: [[named-channels.lifecycle]], [[sse-streaming.external]]
**Connections**:
- related: Layer 1 of the external interaction model — scripts persist beyond single request/response
- related: [[inbox.infrastructure]] — inbox is the persistent per-node channel that receives actor messages
- related: [[otp-supervision.coordination]] — supervision trees manage actor fault tolerance and restarts
