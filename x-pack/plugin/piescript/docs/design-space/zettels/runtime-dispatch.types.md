---
tags: [types, runtime, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Runtime Dispatch

Two distinct dimensions of runtime polymorphism in piescript's design:

**Typeclass dispatch** (future): Dictionary passing for ad-hoc polymorphism. A closure carrying instance methods threaded at runtime (GHC style) vs monomorphization (Rust style). Currently no runtime type information beyond `Value` variant — the evaluator trusts the type checker (D-025) and dispatches via Java pattern matching. Dictionary passing is the likely path since piescript closures already serialize and travel.

**MV/scalar dispatch** (Block H design): Whether operations automatically lift over multi-value fields. The Block H design session proposed APL-style scalar pervasion — scalar functions lift to work over arrays transparently. This is *rank polymorphism*, distinct from typeclass dispatch. The runtime must decide: is this a single value or a multi-value? See [[scalar-pervasion.data]].

**Depends on**: [[typeclasses.types]], [[multi-value-fields.data]]
**Enables**: (none directly)
**Connections**:
- related: [[mv-scalar-dispatch.data]] — MV story makes runtime dispatch concrete; it's how ES data actually works
- related: currently no runtime type information beyond Value variant
