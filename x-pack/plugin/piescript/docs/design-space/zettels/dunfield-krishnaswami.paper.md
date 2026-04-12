---
tags: [paper, types, inference, polymorphism, theoretical, language, ir, reference]
refs:
  - doc:references.md
  - resource:https://arxiv.org/pdf/1306.6032.pdf
---
# Dunfield & Krishnaswami -- Complete and Easy Bidirectional Typechecking

Joshua Dunfield and Neelakantan R. Krishnaswami. "Complete and Easy Bidirectional Typechecking for Higher-Rank Polymorphism." *ICFP 2013*.

The foundational reference for practical bidirectional type checking. Describes how to combine inference mode (synthesize types bottom-up) and checking mode (propagate expected types top-down) in a single algorithm. The checking rule for universal types -- "to check `e` against `forall a. t`, introduce a fresh skolem `a` and check `e` against `t`" -- directly informs piescript's handling of type annotations (D-034).

While piescript currently uses rank-1 polymorphism (not higher-rank), the bidirectional structure is the same. The paper's approach of existential variables with ordered contexts provides a clean framework for type inference that piescript's elaborator follows.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- formalizes: [[bidir-checking.types]] -- the paper's algorithm is the foundation for piescript's bidirectional checking
- formalizes: [[elaboration-architecture.types]] -- the synthesis/checking mode split in the elaborator follows this paper
- informs: [[higher-rank.types]] -- complete algorithm for higher-rank polymorphism inference
- informs: [[type-annotations.types]] -- annotations provide expected types that feed checking mode
- informs: [[meta-variables.types]] -- existential variables in the paper map to piescript's metavariables
- informs: [[hindley-milner.types]] -- extends HM with bidirectional structure
- part-of: [[papers.hub]]
- related: [[outsidein-x.paper]] -- complementary approach to type inference with constraints
