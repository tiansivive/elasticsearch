---
tags: [language, tech-debt, runtime, evaluation]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Stack Depth

The evaluator is recursive with no trampoline. Deeply nested let-chains and map/filter over large result sets (thousands of rows) will hit `StackOverflowError`. The `SubscribableListener` chain in `EvalBuiltins` is O(n) upfront allocation and O(n) stack for synchronous completion.

**Depends on**: [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- related: consider `ThrottledIterator`-style iterative rewrite or explicit trampoline — relevant before targeting large result sets
- related: [[iterative-streaming.language]] — the mitigation for list builtin stack growth
