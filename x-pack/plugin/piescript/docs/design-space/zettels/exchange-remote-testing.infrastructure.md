---
tags: [infrastructure, distributed, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Exchange Remote Testing

Cross-node Exchange validation and testing. The Exchange streaming infrastructure needs dedicated testing for cross-node scenarios: verifying page serialization, backpressure behavior, error propagation, and cleanup across transport boundaries. This goes beyond single-node integration tests.

**Depends on**: [[exchange-streaming.infrastructure]]
**Enables**: (none)
**Connections**:
- related: [[transport-channels.infrastructure]] — remote exchange uses same transport layer
