---
tags: [types, effects, evaluation, open, concept]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Effect Handlers

Effect handler implementation patterns for piescript (interpretation of [[algebraic-effects.types]]).
- An effect handler intercepts effect operations and provides their implementation -- the same effect can be handled differently in different contexts
- Piescript's [[evaluator.language]] already IS an effect handler: it interprets coordination primitives ([[spawn.coordination]], [[send.coordination]], [[when-synchronization.coordination]]) as effects, with the async evaluator providing their implementation

**Depends on**: [[algebraic-effects.types]], [[free-monad.types]]
**Enables**: [[combinator-fusion.performance]]
**Connections**:
- part-of: [[future-type-system.roadmap]]
- implemented-by: [[evaluator.language]] — current evaluator IS an effect handler (interprets coordination effects)
- related: [[lowering-pass.performance]] — future optimizer inspects effect structure
