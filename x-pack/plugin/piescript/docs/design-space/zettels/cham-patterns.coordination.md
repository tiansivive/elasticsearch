---
tags: [coordination, theoretical]
refs:
  - vision:speculative
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# CHAM Patterns

Chemical Abstract Machine (Berry & Boudol 1992) with [[curry-narrowing.language]] for functional patterns on channel message stores. Maximal parallel firing: runtime discovers and concurrently executes all non-overlapping matches. Control-plane performance model — channel stores are small.

**Depends on**: [[multi-value-channels.coordination]]
**Enables**: (none directly)
**Connections**:
- part-of: [[future-coordination.roadmap]]
- related: [[multi-value-channels.coordination]] — generalizes [[when-synchronization.coordination]] from simple presence to functional patterns; highly speculative
- related: [[join-calculus.coordination]] — CHAM reaction rules generalize join calculus join patterns to functional pattern matching over message stores
- related: [[curry-narrowing.language]] — functional-logic patterns enable declarative concurrent reactions
