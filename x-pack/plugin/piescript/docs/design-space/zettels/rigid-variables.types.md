---
tags: [types, implemented, polymorphism, documentation]
refs:
  - adr:D-031
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:piescript.types
---
# Rigid Variables

`MonoType.Rigid(int id, Kind kind)` -- skolem constants representing bound type variables.

- Two rigids with the same id [[unification-algorithm.types|unify]]; a rigid with anything else is a type error.
- Appear in [[type-scheme.types|TypeScheme]] bodies.
- At instantiation, replaced with fresh [[meta-variables.types|metas]]. At generalization, unsolved metas become rigids.

**Depends on**: [[hindley-milner.types]]
**Enables**: [[system-f-core.types]], [[value-restriction.types]]
**Connections**:
- inspired-by: standard ML/Haskell approach (GHC calls them "skolems") — necessary for correct universal-type checking
- complements: [[meta-variables.types]] — metas become rigids at generalization; rigids become fresh metas at instantiation
- part-of: [[type-scheme.types]] — rigids appear in TypeScheme bodies
- uses: [[unification-algorithm.types]] — two rigids with the same id unify; otherwise type error
