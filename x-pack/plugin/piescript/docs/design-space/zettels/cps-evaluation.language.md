---
tags: [language, runtime, concurrency, implemented, evaluation, design-pattern, async]
refs:
  - adr:D-041
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# CPS Evaluation

The evaluator is a CPS (continuation-passing style) transformation where `ActionListener<Value>` callbacks serve as continuations. Pure expressions complete synchronously inline (zero overhead). When coordination primitives are encountered, the evaluator suspends via callback and resumes when channels deliver. `SubscribableListener` chaining in `EvalBuiltins` provides stack-safe sequential stream processing.

**Depends on**: [[evaluator.language]], [[channels.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: ES's own `ActionListener` patterns make this natural — async signature has zero overhead for pure programs; the uniformity is free
