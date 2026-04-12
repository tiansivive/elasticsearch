---
tags: [language, runtime, concurrency, implemented, evaluation, design-pattern, pattern, async, concept]
refs:
  - adr:D-041
  - code:Evaluator.java
  - code:EvalBuiltins.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# CPS Evaluation

The [[evaluator.language]] is a CPS (continuation-passing style) transformation where `ActionListener<Value>` callbacks serve as continuations.

- Pure expressions complete synchronously inline (zero overhead)
- When coordination primitives are encountered, the evaluator suspends via callback and resumes when [[channels.infrastructure]] deliver
- `SubscribableListener` chaining in `EvalBuiltins` provides stack-safe sequential stream processing
- ES's own `ActionListener` patterns make this natural — async signature has zero overhead for pure programs

**Depends on**: [[evaluator.language]], [[channels.infrastructure]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-a.roadmap]]
- implements: [[evaluator.language]] — CPS is the evaluation strategy for the core evaluator
- uses: [[channels.infrastructure]] — coordination primitives suspend/resume via channel callbacks
- uses: [[generic-thread-pool.infrastructure]] — `ActionListener` patterns from ES's async infrastructure
- enables: [[fused-loop-match.language]] — async suspension inside loop iterations
- enables: [[implicit-recursion.design]] — async-interleaved recursion is naturally stack-safe
- extends-to: [[cps-transform.compilation]] — evaluation-time CPS can become a compilation technique
- extends-to: [[cek-machine.evaluation]] — making implicit CPS explicit yields CEK
