---
tags: [control-flow, continuation, concept]
refs: []
---
# One-Shot Continuations

Continuations used exactly once. Linear resource -- connects to QTT linearity. Enables zero-overhead compilation: no need to copy or heap-allocate the continuation frame, can use stack or registers. The common case for loops (each repeat uses the continuation once), exceptions (throw uses handler once), and early return.

One-shot is the sweet spot: it covers the vast majority of control flow patterns while permitting the most aggressive compilation strategies. The compiler can prove linearity statically (via QTT multiplicities or simpler escape analysis) and emit direct jumps or state transitions instead of closure allocations.

**Depends on**: (none)
**Enables**: [[state-machine-loop.compilation]]
**Connections**:
- part-of: [[delimited-continuations.hub]]
- uses: [[qtt-linearity.types]] -- one-shot = linear, multiplicity 1
- enables: [[state-machine-loop.compilation]] -- one-shot continuations compile to state transitions
- contrasts-with: [[multi-shot-continuations.control]] -- one vs many invocations
- optimizes: [[fused-loop-match.language]] -- repeat is a one-shot continuation use
