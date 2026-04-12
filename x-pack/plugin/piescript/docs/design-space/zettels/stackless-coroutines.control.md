---
tags: [control-flow, continuation, concept, coroutine]
refs: []
---
# Stackless Coroutines

Coroutines implemented via CPS transform or state machine (no runtime stack capture). Compiler rewrites the function into a state machine where yield points become states. Kotlin's approach. Lighter than stackful but cannot yield from nested call sites -- only from the direct function body.

The "stackless" means the compiler handles suspension at compile time rather than the runtime capturing stack frames. Each function that can suspend is transformed into a state machine at compilation. The advantage is zero runtime overhead for the suspension mechanism itself. The limitation is that suspension points must be syntactically visible -- you cannot suspend from inside a helper function called by the suspending function unless that helper is also marked as suspending (viral annotation).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[delimited-continuations.hub]]
- uses: [[state-machine-loop.compilation]] -- state machine is the compilation technique
- uses: [[one-shot-continuations.control]] -- each resume is typically one-shot
- contrasts-with: [[stackful-continuations.control]] -- compiler transform vs runtime stack capture
- tension-with depth -- cannot yield from nested functions, limits expressiveness
