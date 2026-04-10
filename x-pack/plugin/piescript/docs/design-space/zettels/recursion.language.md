---
tags: [language, control-flow, open, deferred, feature, task, needs-design, next]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
---
# Recursion

No recursion support. `let rec`, fix point combinator, tail-call optimization all deferred.
Batched consumption in Block D requires recursion or [[iterative-streaming.language|iterative
builtins]]. Single large `Shard.consume` call is the current workaround.

**Approach under discussion:** A `loop` builtin (no new keyword, no Core IR change) covers the
primary use cases (pagination, iterative algorithms). General recursion (`let rec`) deferred
to when it's actually needed. A `loop` builtin can be implemented iteratively in the evaluator,
making it stack-safe even for pure computation.

**Stack safety nuance:** The CPS evaluator (`ActionListener` callbacks) means recursion
interleaved with async operations (queries, spawns) is naturally stack-safe — the callback
fires on a fresh GENERIC thread. The [[stack-depth.language]] concern is real only for pure
recursive computation without async points. For the pagination use case (query per iteration),
the evaluator already handles it.

**Depends on**: [[evaluator.language]], [[pattern-matching.hub]]
**Enables**: [[composite-paging.data]]
**Connections**:
- part-of: [[future-type-system.roadmap]]
- blocked-by: [[pattern-matching.hub]] — any loop/recursion needs branching, which needs pattern matching (at minimum Boolean match)
- tension-with: [[stack-depth.language]] — unbounded recursion dangerous without TCO; mitigated for async-interleaved recursion (query per iteration unwinds stack)
- complements: [[recursive-types.types]] — recursive functions and recursive types are co-dependent features
- solves: [[composite-paging.data]] — recursive piescript loops needed for composite aggregation paging
