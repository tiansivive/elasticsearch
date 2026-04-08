---
tags: [types, theoretical, effects]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Effect Systems

Algebraic effects, monadic effect tracking, effect handlers. Piescript's coordination effects (spawn, when, send, query) are currently implicit — not tracked in types. The free monad perspective (D-040) models them theoretically, but the type system doesn't distinguish `Double` (pure) from `Channel Double` (effectful via spawn). Future: explicit effect tracking would enable the optimizer to identify pure fragments for compilation.

**Depends on**: [[algebraic-effects.types]], [[free-monad.types]]
**Enables**: [[bytecode-compilation.performance]]
**Connections**:
- related: [[value-restriction.types]] — value restriction (D-046) is a crude effect approximation using syntactic value detection instead of effect tracking
- related: explicit effects would make it principled
- related: [[purity-enforcement.language]] — explicit effect tracking would replace implicit purity-by-construction
