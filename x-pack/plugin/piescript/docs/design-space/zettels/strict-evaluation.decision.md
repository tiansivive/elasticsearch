---
tags: [language, evaluation, call-by-value, decided, concept, principle, safety, runtime, performance]
refs:
  - plan:scripting_language_design_9286506e
---
# Strict Evaluation

Piescript is strict (call-by-value) at the expression level. Arguments are fully evaluated before function application; no thunks are allocated, no STG-like machine is needed, and there are no space leaks from unevaluated chains. Laziness exists only at the stream/Exchange level, where pages flow on demand -- this is infrastructure-level pull, not language-level laziness.

The decision explicitly rejects Haskell-style lazy evaluation for expressions. Strict evaluation gives predictable execution order, deterministic memory behavior, and straightforward reasoning about what runs where -- critical for a distributed language where closures travel between nodes. The cost is that some compositional patterns (e.g., `take 10 (filter p xs)` fusing lazily) require explicit streaming infrastructure rather than falling out of the evaluation strategy.

**Depends on**: (none)
**Enables**: [[cek-machine.evaluation]], [[evaluator.language]], [[trampolining.technique]]
**Connections**:
- tension-with: [[codata.types]] -- codata is observation-based and naturally lazy; strict evaluation means coinductive types would need explicit thunks or infrastructure-level laziness
- tension-with: [[lazy-stream.types]] -- a lazy Stream type would require thunks or Exchange-level pull, not language-level laziness
- informs: [[cek-machine.evaluation]] -- the CEK machine is a strict evaluation model (environment machine, not STG)
- informs: [[evaluator.language]] -- the evaluator is a strict tree-walking interpreter; pure expressions fire callbacks synchronously
- informs: [[trampolining.technique]] -- trampolining reifies continuations for stack safety, not for laziness
- contrasts-with: Haskell -- Haskell is lazy by default (STG machine, thunks everywhere); piescript inverts this
- complements: [[explicit-distribution.language]] -- strict = predictable execution order = easier to reason about what runs where
- complements: [[purity.language]] -- strict + pure = referentially transparent with predictable evaluation order
- uses: [[de-bruijn-indices.language]] -- strict evaluation means the environment is always a `Value[]` of fully-evaluated values
- motivates: [[no-corecursion.decision]] -- strict evaluation makes user-defined corecursion impossible without thunks
