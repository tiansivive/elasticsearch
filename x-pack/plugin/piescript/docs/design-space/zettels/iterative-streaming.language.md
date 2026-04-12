---
tags: [language, evaluation, performance, implemented, documentation, iteration]
refs:
  - adr:D-041
  - code:EvalBuiltins.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Iterative Streaming

`map`/`filter`/`reduce` over `ListVal` use an iterative while-loop pattern (not recursive callbacks) to avoid [[stack-depth.language]] growth.
- The `SubscribableListener` chain is O(n) upfront allocation but avoids O(n) stack depth for synchronous completion
- This is critical for large [[esql-compilation.esql]] result sets processed on the coordinator

**Depends on**: [[evaluator.language]], [[list-type.language]]
**Enables**: (none directly)
**Connections**:
- solves: [[stack-depth.language]] — this pattern mitigates the stack depth risk for list builtins specifically
- uses: [[cps-evaluation.language]] — async API handles genuinely async lambda bodies without special-casing
