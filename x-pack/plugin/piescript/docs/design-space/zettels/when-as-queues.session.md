---
tags: [paper-trail, coordination, pi-calculus, channels]
refs:
  - session:1f16c50b-8f6e-4234-94a0-b369a9b987eb
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# When-as-Queues Session

Discussion about extending when clauses from single-value presence checks to multi-value queues with functional pattern matching. Key insight: Curry-style narrowing for functional patterns, not just predicate filtering. Maximal parallel firing (CHAM semantics) — narrowing finds ALL satisfying assignments, fires them all concurrently. Channel stores are small (control-plane), so narrowing performance is tractable.

**Connections**:
- produced: [[cham-patterns.coordination]] — CHAM + Curry narrowing design emerged here
- produced: [[multi-value-channels.coordination]] — the multi-value channel concept refined
- informs: [[fold-as-join.coordination]]
- related: [[when-synchronization.coordination]] — extends the current single-value when
