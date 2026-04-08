---
tags: [language, control-flow, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Recursion

No recursion support. `let rec`, fix point combinator, tail-call optimization all deferred. Batched consumption in Block D requires recursion or iterative builtins. Single large `Shard.consume` call is the current workaround.

**Depends on**: [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- related: [[stack-depth.language]] — unbounded recursion dangerous without TCO
- related: [[recursive-types.types]] — recursive functions and recursive types are co-dependent features
