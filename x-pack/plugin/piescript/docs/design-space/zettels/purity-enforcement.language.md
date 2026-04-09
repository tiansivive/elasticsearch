---
tags: [language, safety, open, concept]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Purity Enforcement

Piescript's [[purity.language|purity]] is currently implicit -- there is no checker that verifies functions are side-effect-free.

- The language has no mutation primitives and no I/O beyond coordination effects, so purity holds by construction for now.
- But [[ffi-painless.external|FFI]] (`Java.call`) could break purity.
- Open questions: should the [[elaboration-architecture.types|elaborator]] track effectfulness? Should FFI calls be marked as effectful? Should the [[kind-system.types|kind system]] distinguish pure from effectful types (enabling the optimizer to identify compilable pure fragments)?

**Depends on**: [[purity.language]], [[ffi-painless.external]]
**Enables**: [[bytecode-compilation.performance]]
**Connections**:
- contrasts-with: [[effect-systems.types]] — explicit effect tracking would replace implicit purity
- tradeoff-with: [[value-restriction.types]] — the value restriction is a crude effect approximation using syntactic analysis
- related: [[free-monad.types]] — coordination primitives form an effect signature; the free monad perspective models effects theoretically
