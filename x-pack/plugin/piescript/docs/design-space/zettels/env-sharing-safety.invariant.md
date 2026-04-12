---
tags: [language, evaluation, concurrency, safety, purity, concept, invariant]
refs:
  - code:Evaluator.java
  - code:Value.java
  - code:EvalCoordination.java
---
# Environment Sharing Safety Invariant

The captured `Value[]` environment array in spawned closures is safe to share across concurrent evaluations. This holds because: (1) the environment is never mutated in place -- lambda application prepends the argument to a new array copy, (2) the language is pure -- no mutable references exist, and (3) de Bruijn indexing means variable resolution is a direct array lookup with no aliasing concerns.

When `spawn expr` forks a closure to the GENERIC thread pool, the closure's `env` array is shared (not copied) between the spawning thread and the forked evaluation. This is safe precisely because no evaluation step writes to an existing `env` slot. Each lambda application creates a fresh `Value[]` with the argument prepended. The original array is structurally immutable after creation.

Future formalization: QTT linearity annotations could make this invariant explicit at the type level -- environment arrays would carry unrestricted (omega) multiplicity, and the type system would statically guarantee no mutation.

**Depends on**: [[purity.language]], [[de-bruijn-indices.language]], [[closure-val.language]]
**Enables**: [[spawn.coordination]], [[code-mobility.coordination]]
**Connections**:
- implements: [[evaluator.language]] -- correctness argument for concurrent evaluation in the tree-walking interpreter
- informs: [[qtt-linearity.types]] -- future: linearity formalizes this invariant at the type level
- uses: [[purity.language]] -- purity guarantees no mutation of captured values
- uses: [[de-bruijn-indices.language]] -- de Bruijn indexing means env is a simple array; no name-based aliasing
- uses: [[closure-val.language]] -- `ClosureVal(body, env)` is the data structure whose sharing safety this invariant establishes
- complements: [[strict-evaluation.decision]] -- strict evaluation means all env values are fully evaluated before capture; no thunk-related races
- validates: [[spawn.coordination]] -- spawn safely shares the closure environment across threads
- validates: [[code-mobility.coordination]] -- serializing a closure's env is safe because no concurrent mutation can occur
- analogous-to: Erlang process isolation -- Erlang copies; piescript shares safely due to immutability
