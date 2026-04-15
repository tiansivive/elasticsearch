---
tags: [language, control-flow, feature, implemented, iteration]
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

**Type enforcement via `Repeat a` TCon**: `repeat expr` has type `Repeat S` where S is the
loop state type. `Repeat` is a builtin TCon (kind: Type → Type) that doesn't unify with
consuming types. The loop elaboration classifies arms post-hoc: `Repeat ?S` → step arm,
otherwise → base arm. See [[repeat-tcon.types]].

**Known limitation**: Mixed-type branches fail — `Repeat S` doesn't unify with `R` in the
same match. `match cond | true -> "done" | false -> repeat 1` is a type error. Future fixes:
[[pattern-guards.language]] (highest priority), [[variant-arm-typing.language]] (after Variants).
See [[mixed-type-branches.obstacle]].

**Depends on**: [[pattern-matching.hub]], [[evaluator.language]]
**Enables**: [[composite-paging.data]]
**Connections**:
- part-of: [[recursion.hub]]
- uses: [[repeat-tcon.types]] — Repeat a TCon for type-level enforcement
- uses: [[pattern-types.language]] — patterns in loop arms
- uses: [[core-match.language]] — evaluator reuses pattern matching logic
- uses: [[cps-evaluation.language]] — async suspension inside loop iterations
- contrasts-with: [[implicit-recursion.design]] — structured/safe path vs general/flexible path
- compiles-to: [[state-machine-loop.compilation]] — repeat compiles to state transition with zero overhead
- uses: [[one-shot-continuations.control]] — each repeat is a one-shot continuation use
- tension-with: [[mixed-type-branches.obstacle]] — mixed-type branches don't work with Repeat a
- enhanced-by: [[pattern-guards.language]] — guards solve conditional repeat without mixed types
- enhanced-by: [[variant-arm-typing.language]] — future: variants enable mixed-type arms
- inspired-by: Clojure `loop`/`recur` and Scheme named `let`
