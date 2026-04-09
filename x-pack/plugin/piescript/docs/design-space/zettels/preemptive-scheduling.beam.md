---
tags: [beam, scheduling, concurrency, reference, exploration]
refs: []
---
# Preemptive Scheduling (BEAM)

- Each process gets approximately 4000 reductions (roughly function calls) before the scheduler yields it.
- Guarantees fairness across thousands of concurrent processes without requiring cooperative scheduling -- no process can starve others by running a tight loop.
- The reduction counter is decremented at function entry points, making preemption predictable and low-overhead.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- part-of: [[beam-lessons.comparable]] — one of the four key BEAM runtime lessons
- analogous-to: [[generic-thread-pool.infrastructure]] — ES's generic thread pool faces the same fairness challenge; reduction counting is one proven solution
