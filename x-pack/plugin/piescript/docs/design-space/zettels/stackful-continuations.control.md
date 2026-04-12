---
tags: [control-flow, continuation, concept, coroutine]
refs: []
---
# Stackful Continuations

Coroutines/continuations that capture the full runtime stack. Can yield from any call depth. Heavier: requires stack copying or segmented stacks. Java's Project Loom virtual threads use this model. Open questions: heap allocation of captured frames, copyability (needed for multi-shot), escape analysis (can the compiler prove the frame doesn't escape?).

The "stackful" means the runtime can suspend at any point in the call stack, not just at compiler-designated suspension points. This is maximally expressive -- any function can be suspended without annotation. The cost is that suspension requires capturing or copying the runtime stack, which is expensive and interacts with GC, memory layout, and platform constraints. The JVM historically did not support this; Project Loom adds virtual threads that achieve it via continuation objects in the VM.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[delimited-continuations.hub]]
- uses: [[multi-shot-continuations.control]] -- replayability requires copyable frames
- contrasts-with: [[stackless-coroutines.control]] -- runtime stack capture vs compiler transform
- uses: [[call-cc.control]] -- typically how call/cc is implemented
- tension-with JVM -- JVM doesn't expose stack frames; Project Loom works around this
