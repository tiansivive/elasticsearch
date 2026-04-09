---
tags: [language, evaluation, concept, superseded]
refs: []
---
# Terminal Operations

The concept that `fold` and top-level `StreamVal` consumption are "terminal operations" that force plan execution:
- Analogous to [[spark.comparable]]'s distinction between transformations (lazy) and actions (eager)
- In a lazy evaluation model, building up chains of `map`, `filter`, and `flatMap` would describe a computation without executing it
- Only a terminal operation like `fold`, `collect`, or returning a stream as the program result would trigger actual data movement
- Currently superseded by [[eager-materialization.data]] -- everything executes immediately -- but the concept becomes relevant again if piescript introduces lazy streaming or deferred query plans
- Related to [[iterative-streaming.language]] which provides the current streaming model

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- analogous-to: [[spark.comparable]] -- Spark actions vs transformations is the same lazy/eager boundary concept
- evolved-into: [[eager-materialization.data]] -- currently everything is eager; terminal ops matter when laziness returns
- related: [[iterative-streaming.language]] -- streaming model relates to when data movement is triggered
