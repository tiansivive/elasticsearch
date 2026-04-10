---
tags: [coordination, open, concept, needs-design, later]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:distributed-coordination
---
# Fold as Join Pattern

Folding over a stream modeled as a [[join-calculus.coordination]] pattern: each element arrival triggers a [[when-synchronization.coordination]] reaction that updates an accumulator.
- Natural with [[multi-value-channels.coordination]]
- Enables stateful stream processing without explicit mutable state

**Depends on**: [[multi-value-channels.coordination]]
**Enables**: (none directly)
**Connections**:
- part-of: [[future-coordination.roadmap]]
- related: [[join-calculus.coordination]] — bridges the gap between stateless map/filter and stateful aggregation; join calculus reaction rules naturally express this
- related: [[when-synchronization.coordination]] — when reactions trigger on each element arrival
- related: [[multi-value-channels.coordination]] — stream elements arrive via multi-value channels
