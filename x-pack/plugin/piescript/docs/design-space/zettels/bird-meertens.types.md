---
tags: [theoretical, performance]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Bird-Meertens Formalism

Algebraic theory of parallel list operations:

- List homomorphisms (h(xs ++ ys) = h(xs) + h(ys)) are automatically parallelizable
- `map` is always a homomorphism; `filter` is always a homomorphism
- `reduce` is a homomorphism when combiner is associative with identity
- [[third-homomorphism.performance]] (Gibbons 1996) tells exactly which piescript operations are safe to push down and parallelize

**Depends on**: (none)
**Enables**: [[push-down-compilation.performance]], [[combinator-fusion.performance]]
**Connections**:
- part-of: [[deferred-push-down.roadmap]]
- informs: [[third-homomorphism.performance]] — BMF is the foundation for the Third Homomorphism Theorem
- complements: [[calm-theorem.types]] — complementary parallelization criteria; CALM focuses on monotonicity, BMF on algebraic structure
- related: [[parametricity.types]] — parametric polymorphism guarantees that BMF's algebraic laws hold across implementations; the connection is bidirectional
