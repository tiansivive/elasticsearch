---
tags: [language, tech-debt, runtime, evaluation, task, known-issue]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:Evaluator.java
  - code:EvalBuiltins.java
---
# Stack Depth

- The [[evaluator.language|evaluator]] is recursive with no trampoline.
- Deeply nested let-chains and `map`/`filter` over large result sets (thousands of rows) will hit `StackOverflowError`.
- The `SubscribableListener` chain in `EvalBuiltins` is O(n) upfront allocation and O(n) stack for synchronous completion.

**Depends on**: [[evaluator.language]]
**Enables**: (none directly)
**Connections**:
- motivates: [[iterative-streaming.language]] — the mitigation for list builtin stack growth
- tension-with: [[recursion.hub]] — unbounded recursion would make this critical
- tension-with: [[recursive-types.types]] — either recursive type approach makes stack depth critical
- solves: [[trampolining.technique]]
- constrains: [[execution-model.question]]
