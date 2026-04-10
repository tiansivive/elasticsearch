---
tags: [language, control-flow, open, deferred, feature, task, needs-design, next]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
---
# Recursion

No recursion support. `let rec`, fix point combinator, tail-call optimization all deferred. Batched consumption in Block D requires recursion or [[iterative-streaming.language|iterative builtins]]. Single large `Shard.consume` call is the current workaround.

**Depends on**: [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[future-type-system.roadmap]]
- tension-with: [[stack-depth.language]] — unbounded recursion dangerous without TCO
- complements: [[recursive-types.types]] — recursive functions and recursive types are co-dependent features
- workaround-for: [[composite-paging.data]] — recursive piescript loops needed for composite aggregation paging
