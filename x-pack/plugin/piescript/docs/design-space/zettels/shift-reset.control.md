---
tags: [control-flow, continuation, theoretical, concept]
refs:
  - resource:https://doi.org/10.1016/0304-3975(90)90147-A
---
# Shift/Reset

The core primitive pair for delimited continuations. `shift` captures the continuation up to the nearest `reset`; `reset` delimits the scope. Subsumes most control flow: loops (shift = repeat), exceptions (shift = throw, reset = catch), generators (shift = yield), coroutines (shift = suspend).

`reset` installs a delimiter on the continuation. `shift` captures the continuation up to that delimiter, reifies it as a function, and passes it to the shift body. The captured continuation can be invoked zero times (abort/exception), once (normal return/loop step), or many times (backtracking/nondeterminism).

Resources: Danvy & Filinski, "Abstracting Control" (1990).

**Depends on**: [[cek-machine.evaluation]] -- needs reified continuations
**Enables**: [[algebraic-effects.types]]
**Connections**:
- part-of: [[delimited-continuations.hub]]
- enables: [[algebraic-effects.types]] -- effects compile to shift/reset
- enables: [[fused-loop-match.language]] -- repeat is a restricted shift
- contrasts-with: [[call-cc.control]] -- delimited vs undelimited
- uses: [[one-shot-continuations.control]] or [[multi-shot-continuations.control]] -- captured continuation may be used once or many times
- tension-with: [[distributed-continuations.obstacle]] -- shift inside shipped code, reset on initiator
