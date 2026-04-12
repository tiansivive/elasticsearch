---
tags: [language, control-flow, needs-design, now, iteration]
refs:
  - thread:language-expressiveness
---
# Fused Loop-Match

Structured iteration fusing pattern matching with looping:

```
loop state
| base-pat -> result
| step-pat -> ... repeat newState
```

Base cases are arms without `repeat`; step cases use `repeat` as a structured jump. The
evaluator implements this as a while-loop — guaranteed stack-safe even for pure computation.
No trampoline needed.

Inspired by Clojure `loop`/`recur` and Scheme named `let`. Forces base-case discipline via
pattern matching. Expression-oriented (the whole construct returns a value). Explicit state
(the `loop` binding declares what is carried between iterations).

Composes with async — a query inside a loop arm suspends naturally via the CPS evaluator.

**Depends on**: [[pattern-matching.hub]], [[evaluator.language]]
**Enables**: [[composite-paging.data]]
**Connections**:
- part-of: [[recursion.hub]]
- uses: [[pattern-types.language]] — patterns in loop arms
- uses: [[core-match.language]] — evaluator reuses pattern matching logic
- uses: [[cps-evaluation.language]] — async suspension inside loop iterations
- contrasts-with: [[implicit-recursion.design]] — structured/safe path vs general/flexible path
- compiles-to: [[state-machine-loop.compilation]] — repeat compiles to state transition with zero overhead
- uses: [[one-shot-continuations.control]] — each repeat is a one-shot continuation use
- inspired-by: Clojure `loop`/`recur` and Scheme named `let`
