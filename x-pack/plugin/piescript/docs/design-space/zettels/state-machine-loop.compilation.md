---
tags: [compilation, continuation, technique, concept, lowering]
refs: []
---
# State Machine Loop Compilation

Compiling continuations as state machines rather than CPS closures. Each continuation point becomes a state; repeat/shift becomes a state transition. Combined with block-params IR, achieves zero-overhead looping. Kotlin compiles coroutines this way. Eliminates closure allocation overhead of CPS transform.

The key insight: if a continuation is used exactly once (one-shot), the "capture and invoke" can be compiled as a jump to a labeled block rather than allocating a closure. A set of mutually-one-shot continuations becomes a state machine with transitions between blocks. This is how Kotlin suspend functions compile to JVM bytecode -- each suspension point becomes a state in a switch.

**Depends on**: [[one-shot-continuations.control]]
**Enables**: (none directly)
**Connections**:
- part-of: [[delimited-continuations.hub]]
- contrasts-with: [[cps-transform.compilation]] -- state transitions vs closure allocation
- uses: [[block-params-ir.compilation]] -- block parameters enable efficient state transitions as jumps
- uses: [[one-shot-continuations.control]] -- state machine is the optimal compilation for one-shot continuations
- optimizes: [[fused-loop-match.language]] -- loop-match repeat compiles to state transition with zero overhead
- informs: [[bytecode-compilation.performance]] -- state machine style is natural for JVM bytecode
