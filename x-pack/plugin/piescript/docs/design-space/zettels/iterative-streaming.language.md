---
tags: [language, evaluation, performance, implemented]
refs:
  - adr:D-041
  - code:EvalBuiltins.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Iterative Streaming

`map`/`filter`/`reduce` over `ListVal` use an iterative while-loop pattern (not recursive callbacks) to avoid stack growth. The `SubscribableListener` chain is O(n) upfront allocation but avoids O(n) stack depth for synchronous completion. This is critical for large ESQL result sets processed on the coordinator.

**Depends on**: [[evaluator.language]], [[list-type.language]]
**Enables**: (none directly)
**Connections**:
- related: [[stack-depth.language]] — this pattern mitigates the stack depth risk for list builtins specifically
- related: [[cps-evaluation.language]] — async API handles genuinely async lambda bodies without special-casing
