---
tags: [lifecycle, scheduling, open, exploration, concept, feature, someday]
refs:
  - thread:distributed-coordination
---
# Long-Lived Computations

Persistent computations that survive across query invocations:

- Materialized views
- Running aggregations
- Event-driven processing

Moves piescript from "run and get results" toward "persistent distributed computation with safe [[mutable-shared-state.types]]," where programs maintain identity and mutable state across invocations rather than being ephemeral request handlers. Depends on [[actor-model.lifecycle]] for process identity and [[scheduled-execution.lifecycle]] for invocation triggers.

**Depends on**: [[mutable-shared-state.types]], [[actor-model.lifecycle]], [[scheduled-execution.lifecycle]]
**Enables**: (none)
**Connections**:
- part-of: [[future-coordination.roadmap]]
- inspired-by: [[otp-supervision.coordination]] — OTP's model of long-lived supervised processes informs the lifecycle design
- related: [[checkpointing.lifecycle]] — long-lived computations need checkpointing to survive node restarts
- motivates: [[persistent-task-integration.infrastructure]] — ES PersistentTasks are the runtime substrate for long-lived computations
