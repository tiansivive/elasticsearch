---
tags: [thread, roadmap, coordination, distributed]
refs: []
---
# Distributed Coordination

From multi-value channels to actor-model lifecycle and supervised long-lived
computations. This thread extends piescript's Join Calculus coordination model
beyond single-value channels toward streaming patterns, persistent identity,
scheduled execution, and fault-tolerant distributed processes.

## Sequence

1. **Multi-value channels** [[multi-value-channels.coordination]] — needs-design
   Channels that carry streams of messages over time. Multiset message store,
   notification, completion/close semantics, backpressure.

2. **Channel lifecycle** [[channel-lifecycle.infrastructure]] — needs-design
   Leak prevention for `spawn!` channels. Scope-based cleanup.
   _Shared with: error-handling_

3. **Dynamic fan-out** [[dynamic-fan-out.coordination]] — needs-design
   Fan-out to a dynamic number of targets (not statically known at
   elaboration time).

4. **Fold-as-join** [[fold-as-join.coordination]] — needs-design
   Aggregation patterns over multi-value channels.
   Depends on: [[multi-value-channels.coordination]]

5. **CHAM patterns** [[cham-patterns.coordination]] — exploration
   Functional-pattern matching on channel message stores. Curry-style
   narrowing, maximal parallel firing.
   Depends on: [[multi-value-channels.coordination]]

6. **Scheduled execution** [[scheduled-execution.lifecycle]] — needs-design
   `PiescriptPersistentTasksExecutor`. Cron/interval semantics,
   checkpointing, failure recovery.

7. **Actor model** [[actor-model.lifecycle]] — needs-design
   Persistent script identity. PUT/GET/DELETE lifecycle, named channels,
   SSE streaming.
   Depends on: [[scheduled-execution.lifecycle]]

8. **Named channels** [[named-channels.lifecycle]] — needs-design
   Exposed channels as HTTP endpoints. Token-capability security.
   Depends on: [[actor-model.lifecycle]]

9. **Long-lived computations** [[long-lived-computations.lifecycle]] — exploration
   Materialized views, running aggregations, event-driven processing.
   Depends on: [[actor-model.lifecycle]], [[scheduled-execution.lifecycle]]

**Depends on**: (none — root thread)
**Enables**: (none directly)
**Connections**:
- includes: [[multi-value-channels.coordination]]
- includes: [[channel-lifecycle.infrastructure]]
- includes: [[dynamic-fan-out.coordination]]
- includes: [[fold-as-join.coordination]]
- includes: [[cham-patterns.coordination]]
- includes: [[scheduled-execution.lifecycle]]
- includes: [[actor-model.lifecycle]]
- includes: [[named-channels.lifecycle]]
- includes: [[long-lived-computations.lifecycle]]
- related: [[error-handling.thread]] — shares channel lifecycle
- related: [[external-interaction.thread]] — shares actor model, named channels
