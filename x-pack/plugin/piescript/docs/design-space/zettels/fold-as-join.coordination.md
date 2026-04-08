---
tags: [coordination, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Fold as Join Pattern

Folding over a stream modeled as a join pattern: each element arrival triggers a reaction that updates an accumulator. Natural with multi-value channels. Enables stateful stream processing without explicit mutable state.

**Depends on**: [[multi-value-channels.coordination]]
**Enables**: (none directly)
**Connections**:
- related: [[join-calculus.coordination]] — bridges the gap between stateless map/filter and stateful aggregation; join calculus reaction rules naturally express this
