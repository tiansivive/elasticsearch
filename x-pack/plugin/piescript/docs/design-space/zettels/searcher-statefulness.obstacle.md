---
tags: [data, evaluation, purity, obstacle, lucene, concept]
refs:
  - adr:D-050
  - code:SearcherState.java
  - code:EvalShard.java
---
# Searcher Statefulness

`SearcherVal`'s internal cursor is mutable -- `Shard.consume` advances an in-place `DocIdSetIterator` via `SearcherState`. This creates a tension with piescript's purity guarantee ([[purity.language]]): a mutable cursor lives inside a language that claims referential transparency.

Accepted as a pragmatic escape hatch because:
1. The cursor is **single-threaded** -- only one evaluation thread advances it at a time.
2. The cursor is **node-local** -- `SearcherVal` is non-serializable and never travels across nodes.
3. The mutation is **monotonic** -- the cursor only moves forward, never rewinds. Two calls to `consume` on the same searcher yield different results (violating purity), but the violation is contained and predictable.
4. The alternative (immutable cursor passing) would require threading cursor state through every call, which the current evaluator doesn't support ergonomically.

The statefulness is why `SearcherVal` is [[non-serializable-types.types|non-serializable]] -- serializing a mutable cursor would require snapshotting iterator position, which is meaningless on a different node with different segments.

**Depends on**: [[shard-read.data]], [[purity.language]]
**Enables**: (none directly -- it's an accepted impurity)
**Connections**:
- tension-with: [[purity.language]] -- mutable cursor inside a pure language; accepted pragmatic violation
- implements: [[shard-read.data]] -- SearcherState is the runtime backing for Shard.consume
- informs: [[non-serializable-types.types]] -- non-serializable BECAUSE of mutable state that can't travel
- informs: [[bracket-patterns.language]] -- searchers need resource cleanup; statefulness makes cleanup critical
- informs: [[searcher-lifecycle.data]] -- the cursor's lifecycle IS the searcher's lifecycle
- tension-with: [[effect-systems.types]] -- a proper effect system would track the mutation as an effect
- motivates: [[local-kind.types]] -- Local kind would enforce at the type level what's currently a runtime invariant (no shipping)
- contrasts-with: [[iterative-streaming.language]] -- iterative streaming is a pure pattern; searcher statefulness is an impure escape hatch for the same problem (incremental consumption)
