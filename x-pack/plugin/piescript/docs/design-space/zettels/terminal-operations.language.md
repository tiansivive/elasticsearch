---
tags: [language, evaluation, concept, superseded]
refs: []
---
# Terminal Operations

The concept that `fold` and top-level `StreamVal` consumption are "terminal operations" that force plan execution:
- Analogous to [[spark.comparable]]'s distinction between transformations (lazy) and actions (eager)
- In a lazy evaluation model, building up chains of `map`, `filter`, and `flatMap` would describe a computation without executing it
- Only a terminal operation like `fold`, `collect`, or returning a stream as the program result would trigger actual data movement
- Currently superseded by [[eager-materialization.data]] -- everything executes immediately -- but the concept becomes relevant again now that Block G introduces exchange-based streaming
- Related to [[iterative-streaming.language]] which provides the current streaming model
- With [[exchange-streaming.infrastructure]] (Block G) implemented, the lazy/eager boundary is back: `Exchange.poll` is a terminal-like operation that triggers data movement through the exchange pipeline

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- analogous-to: [[spark.comparable]] -- Spark actions vs transformations is the same lazy/eager boundary concept
- superseded-by: [[eager-materialization.data]] -- currently everything is eager; terminal ops matter when laziness returns
- revived-by: [[exchange-streaming.infrastructure]] -- Block G reintroduces the lazy/eager boundary; Exchange.poll is the terminal operation that triggers data movement
- related: [[iterative-streaming.language]] -- streaming model relates to when data movement is triggered
