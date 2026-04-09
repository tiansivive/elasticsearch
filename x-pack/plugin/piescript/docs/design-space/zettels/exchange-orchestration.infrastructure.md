---
tags: [infrastructure, streaming, distributed, implemented, concept, decision]
refs:
  - adr:D-054
  - code:EvalExchange.java
---
# Exchange Orchestration

Piescript exposes Exchange primitives (`Exchange.open`, `Exchange.sink`, `Exchange.connect`, `Exchange.addPage`, `Exchange.poll`, `Exchange.finish`) as composable builtins rather than hiding them behind an abstraction.
- The user orchestrates Exchange setup via [[channels.infrastructure]] -- sending exchange descriptors to remote nodes, connecting sinks and sources, and driving the data flow explicitly
- This is distinct from [[exchange-streaming.infrastructure]], which documents the ES Exchange internals that these builtins wrap
- The design choice follows the [[explicit-distribution.language]] philosophy: the primitives are always available, and libraries can build higher-level patterns on top

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[block-g.roadmap]]
- uses: [[exchange-streaming.infrastructure]] -- wraps ES ExchangeService internals as piescript builtins
- implements: [[explicit-distribution.language]] -- Exchange orchestration is the streaming instantiation of the explicit-distribution philosophy
- uses: [[channels.infrastructure]] -- exchange descriptors travel via channels for cross-node setup
