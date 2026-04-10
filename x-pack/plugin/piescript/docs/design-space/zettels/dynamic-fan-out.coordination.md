---
tags: [coordination, concurrency, open, concept, needs-design, later]
refs:
  - thread:distributed-coordination
---
# Dynamic Fan-Out

Forking computation over a runtime-determined number of branches (par over a computed list).

- Distinct from static [[spawn.coordination]] where the number of branches is known at elaboration time
- Requires a list-of-[[channels.infrastructure]] or channel-of-lists pattern to collect results from a dynamically sized set of spawned computations
- The key challenge is typing the join point: the collector must accept N results where N is not statically known

**Depends on**: [[spawn.coordination]], [[list-type.language]]
**Enables**: (none directly)
**Connections**:
- extends: [[spawn.coordination]] — generalizes static spawn to runtime-determined branch counts
- related: [[list-type.language]] — fan-out iterates over a list to determine branches
- analogous-to: [[map-reduce.distributed]] — dynamic fan-out is the local analogue of distributed map-reduce
