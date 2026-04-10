---
tags: [infrastructure, distributed, implemented, task]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Exchange Remote Testing

Cross-node Exchange validation and testing. The [[exchange-streaming.infrastructure]] needs dedicated testing for cross-node scenarios:
- Verifying page [[serialization.infrastructure]], backpressure behavior, error propagation, and cleanup across transport boundaries
- This goes beyond single-node integration tests

**Depends on**: [[exchange-streaming.infrastructure]]
**Enables**: (none)
**Connections**:
- uses: [[transport-channels.infrastructure]] — remote exchange uses same transport layer
- validates: [[exchange-orchestration.infrastructure]] — tests the cross-node orchestration paths
