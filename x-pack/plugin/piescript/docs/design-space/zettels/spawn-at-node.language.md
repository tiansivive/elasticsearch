---
tags: [language, coordination, syntax, distributed, mobility, open, needs-design, concept]
refs:
  - vision:distributed-computation-model
---
# Spawn @node Sugar

`spawn @node (expr)` as syntactic sugar for targeted remote spawn. Currently, remote execution requires explicit inbox send:

```
send node.inbox (fn info ->
  let result = expr;
  send resultCh result
)
```

The `spawn @node` sugar would desugar to approximately the same thing -- create a channel, send a closure to the target node's inbox, have the closure send the result back. The sugar hides the inbox/channel plumbing while preserving the explicit node targeting.

**Tension**: piescript's [[explicit-distribution.language|explicit distribution philosophy]] values making the distributed plumbing visible. Sugar that hides inbox sends may conflict with this -- the user might not realize they're shipping a closure, paying serialization costs, and creating cross-node channels. Counter-argument: the desugaring is mechanical and well-defined; hiding it is no different from `spawn` hiding channel creation + fork + send (which it already does locally).

**Depends on**: [[inbox.infrastructure]], [[code-mobility.coordination]], [[spawn.coordination]]
**Enables**: (none directly -- ergonomic improvement)
**Connections**:
- contrasts-with: [[send.coordination]] -- explicit inbox send is the current approach; spawn @node is sugar over it
- tension-with: [[explicit-distribution.language]] -- hiding the inbox send may conflict with explicit distribution philosophy
- uses: [[inbox.infrastructure]] -- desugars to inbox send on the target node
- uses: [[code-mobility.coordination]] -- the closure shipped to the target node IS code mobility
- extends: [[spawn.coordination]] -- generalizes local spawn to remote spawn with node targeting
- informs: [[currying.language]] -- partial application of spawn @node could yield node-targeted spawn factories
- informs: [[topology.infrastructure]] -- spawn @node needs a node reference from topology
- complements: [[data-locality.distributed]] -- spawn @node is how you move computation to data
