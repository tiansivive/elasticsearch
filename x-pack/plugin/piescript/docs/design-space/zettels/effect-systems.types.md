---
tags: [types, theoretical, effects, concept]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Effect Systems

Algebraic effects, monadic effect tracking, [[effect-handlers.types]]. Piescript's coordination effects ([[spawn.coordination]], [[when-synchronization.coordination]], [[send.coordination]], query) are currently implicit -- not tracked in types.
- The [[free-monad.types]] perspective (D-040) models them theoretically, but the type system doesn't distinguish `Double` (pure) from `Channel Double` (effectful via spawn)
- Future: explicit effect tracking would enable the optimizer to identify pure fragments for [[bytecode-compilation.performance]]

**Depends on**: [[algebraic-effects.types]], [[free-monad.types]]
**Enables**: [[bytecode-compilation.performance]]
**Connections**:
- subsumes: [[value-restriction.types]] — value restriction (D-046) is a crude effect approximation using syntactic value detection instead of effect tracking
- motivates: [[bytecode-compilation.performance]] — explicit effects would identify pure fragments for compilation
- refines: [[purity-enforcement.language]] — explicit effect tracking would replace implicit purity-by-construction
