---
tags: [beam, mem-management, reference, exploration]
refs:
  - doc:references.md
---
# Per-Process Garbage Collection (BEAM)

- Each lightweight process has its own heap, collected independently via generational copying GC.
- No global GC pauses -- a process collecting its 10KB heap doesn't affect thousands of other concurrent processes.
- When a process dies, its entire heap is instantly reclaimed with no GC cost.
- Message passing copies data between heaps, isolating failure domains but adding copy overhead.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- part-of: [[beam-lessons.comparable]] — one of the four key BEAM runtime lessons
- analogous-to: [[channel-lifecycle.infrastructure]] — channel cleanup on completion is piescript's equivalent of "process dies, memory reclaimed"
