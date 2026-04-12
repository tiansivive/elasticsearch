---
tags: [paper, types, inference, typeclasses, theoretical, polymorphism, language, reference]
refs:
  - doc:references.md
  - resource:https://www.microsoft.com/en-us/research/wp-content/uploads/2016/02/jfp-outsidein.pdf
---
# Vytiniotis et al. -- OutsideIn(X)

Dimitrios Vytiniotis, Simon Peyton Jones, Tom Schrijvers, and Martin Sulzmann. "OutsideIn(X): Modular Type Inference with Local Assumptions." *Journal of Functional Programming*, 2011.

The formal treatment of how GADTs interact with Hindley-Milner inference. Parameterized by a constraint domain X, the algorithm handles implication constraints (local type assumptions introduced by GADT pattern matching), let-generalization with local assumptions, and interaction between type classes and GADTs. Implemented in GHC.

For piescript, this paper informs several areas: (1) the decision to reject GADTs (the complexity of implication constraints is a major reason), (2) how typeclass constraints interact with inference, (3) the elaboration architecture's handling of local type assumptions, and (4) the potential future path for type narrowing via validation witnesses as a controlled alternative to full GADTs.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- informs: [[gadt-rejection.types]] -- OutsideIn(X) complexity is part of why piescript rejects GADTs
- informs: [[typeclasses.types]] -- typeclass constraint solving follows OutsideIn(X) patterns
- informs: [[elaboration-architecture.types]] -- implication constraints and local assumptions in the elaborator
- informs: [[type-narrowing.types]] -- type narrowing via witnesses is a controlled alternative to full GADT matching
- informs: [[deferred-constraints.types]] -- constraint-based inference with deferred solving
- part-of: [[papers.hub]]
- related: [[dunfield-krishnaswami.paper]] -- complementary approach: OutsideIn uses constraints, Dunfield uses ordered contexts
- related: [[hindley-milner.types]] -- extends HM with local assumptions and implication constraints
