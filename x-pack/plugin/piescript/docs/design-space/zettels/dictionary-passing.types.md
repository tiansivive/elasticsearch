---
tags: [types, typeclasses, technique, needs-design, runtime, concept]
refs:
  - doc:references.md
  - resource:https://doi.org/10.1145/75277.75283
---
# Dictionary Passing

Wadler & Blott's approach to typeclass implementation: the compiler implicitly passes a
dictionary (record of instance methods) at each call site constrained by a typeclass. The
dictionary is a record of functions -- e.g., a `Num` dictionary contains `(+)`, `(-)`, `(*)`,
`fromInteger`. GHC's primary implementation strategy.

**Key insight for piescript:** dictionaries are serializable values. Since piescript's
[[closure-val.language|closures]] are already fully serializable and can travel to remote nodes
via [[code-mobility.coordination]], dictionaries (which are just records of closures) inherit
this property for free. A function constrained by `Num a =>` receives a dictionary that can be
shipped alongside the closure -- no special treatment needed for distributed typeclass dispatch.

**Trade-offs vs [[monomorphization.compilation|monomorphization]] (Rust's approach):**
- Dictionary passing: one copy of polymorphic code, indirect dispatch, serializable dictionaries
- Monomorphization: specialized code per type, direct dispatch, code size explosion
- Dictionary passing is the natural fit for piescript given existing closure infrastructure

**Depends on**: [[typeclasses.types]], [[closure-val.language]]
**Enables**: [[runtime-dispatch.types]]
**Connections**:
- part-of: [[typeclasses.types]]
- contrasts-with: [[monomorphization.compilation]] -- the two implementation strategies for typeclass polymorphism
- uses: [[closure-val.language]] -- dictionaries are records of closures; reuse existing serialization
- uses: [[code-mobility.coordination]] -- dictionaries travel to remote nodes alongside closures
- informs: [[runtime-dispatch.types]] -- dictionary passing is the likely approach for typeclass dispatch
- informs: [[typeclass-instances.types]] -- each instance generates a dictionary value
- uses: [[record-type.language]] -- a dictionary is structurally a record of functions
- related: [[evaluator-trusts-typechecker.language]] -- dictionary selection is resolved at elaboration time; evaluator trusts the choice
- informs: [[serialization.infrastructure]] -- dictionaries are new serializable values if they're reified as records
