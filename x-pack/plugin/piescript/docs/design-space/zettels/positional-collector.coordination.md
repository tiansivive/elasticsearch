---
tags: [coordination, concurrency, pi-calculus, implemented, documentation]
refs:
  - adr:D-041
  - code:EvalCoordination.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Positional Collector

[[core-ir.language|CoreWhen]] uses a hand-rolled positional collector (`AtomicArray<Value>` + `CountDown`) instead of ES's `GroupedActionListener`.

- `GroupedActionListener` stores results by arrival order (`pos.incrementAndGet() - 1`), but [[when-synchronization.coordination|when]] bindings must see values at their declared binding positions for correct [[de-bruijn-indices.language|de Bruijn indexing]].
- The positional collector writes each result to a known slot index, guaranteeing correct ordering regardless of completion timing.

**Depends on**: [[when-synchronization.coordination]], [[de-bruijn-indices.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-a.roadmap]]
- rejected-in-favor-of: `GroupedActionListener` — arrival-order storage corrupts environment when channels complete out of binding order
- implements: [[when-synchronization.coordination]]
