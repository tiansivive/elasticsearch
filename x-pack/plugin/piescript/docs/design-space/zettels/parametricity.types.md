---
tags: [types, theoretical]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Parametricity

Wadler's "Theorems for Free!" (1989): polymorphic functions satisfy algebraic laws derived from their types alone, without proof. A function `f : forall a. List a -> List a` must commute with `map g` for any `g` -- guaranteed by the type, not by testing.

For piescript, this is the theoretical guarantee that swapping the underlying container from a local List to a distributed dataset preserves correctness. The operations are defined only in terms of the algebraic interface (the [[typeclasses.types|typeclass]] methods), so any lawful implementation works. This is why the [[query-typeclass.data|Query a typeclass]] strategy is sound: the same piescript expression, interpreted via different instances, compiles to different backends while maintaining identical semantics.

**Depends on**: (none)
**Enables**: [[query-typeclass.data]], [[push-down-compilation.performance]]
**Connections**:
- informs: [[query-typeclass.data]] — foundation for the typeclass-driven push-down strategy
- related: [[bird-meertens.types]] — parallelizability from algebraic structure is a parametricity consequence
- complements: [[system-f-core.types]] — parametricity is a consequence of System F's universal quantification
- validates: [[typeclasses.types]] — parametricity guarantees that typeclass-based abstraction preserves semantics
