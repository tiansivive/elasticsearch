---
tags: [compilation, continuation, technique, concept, lowering]
refs:
  - resource:https://doi.org/10.1017/S0956796807006381
---
# CPS Transform (Compilation)

Compiling continuations by CPS-transforming the source program. Every function takes an extra continuation argument. Classic technique. Drawback: closure allocation for every continuation point (overhead). Not the only compilation strategy -- state machine compilation avoids this overhead for one-shot cases.

The distinction from piescript's existing CPS evaluation: [[cps-evaluation.language]] uses `ActionListener` callbacks as implicit continuations at evaluation time. CPS transform as a compilation technique makes these continuations explicit in the IR, enabling further optimization passes but also introducing closure allocation overhead.

Resources: Appel "Compiling with Continuations" (1992), Kennedy "Compiling with Continuations, Continued" (2007).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[delimited-continuations.hub]]
- extends: [[cps-evaluation.language]] -- piescript's evaluator already uses implicit CPS; this makes it a compilation technique
- contrasts-with: [[state-machine-loop.compilation]] -- CPS allocates closures, state machine uses jumps
- uses: [[closure-conversion.compilation]] -- CPS-transformed code needs closure conversion for efficient codegen
- tension-with: [[one-shot-continuations.control]] -- CPS allocates closures even for one-shot continuations, overhead
