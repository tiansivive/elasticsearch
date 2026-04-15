---
tags: [coordination, distributed, mobility, recursion, testing, implemented]
refs:
  - session:recursion-closeout
---
# Recursive Closure Shipping

Shipping closures that capture recursive bindings to remote node inboxes.

In piescript, implicit recursion uses tying-the-knot backpatching in the closure
environment. A closure that references a recursive binding (for example `fact`) can be
serialized, sent through `send node.inbox`, and evaluated remotely while preserving the
captured recursive environment semantics.

This is a cross-feature seam between recursion and mobility: if either closure
serialization or recursive environment capture regresses, remote execution fails even when
local recursion still works.

**Depends on**: [[implicit-recursion.design]], [[code-mobility.coordination]], [[inbox.infrastructure]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-c.roadmap]]
- uses: [[closure-val.language]] -- closure body + environment travel over the wire
- uses: [[transport-channels.infrastructure]] -- wire transport for remote inbox evaluation
- validates: [[recursion.hub]] -- recursive env capture survives cross-node execution
- validated-by: [[cross-node-testing-layers.principle]] -- distributed behavior covered across testing layers