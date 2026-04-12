---
tags: [paper, compilation, performance, data-processing, comparable, theoretical, distributed, streaming, push-down, reference]
refs:
  - doc:references.md
  - resource:https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/35650.pdf
---
# Chambers et al. -- FlumeJava

Craig Chambers, Ashish Raniwala, Frances Perry, Stephen Adams, Robert R. Henry, Robert Bradshaw, and Nathan Weizenbaum. "FlumeJava: Easy, Efficient Data-Parallel Pipelines." *ICFP 2010*.

Google's internal system for data-parallel pipelines. Computation is modeled as deferred functional combinators over `PCollection`s (parallel collections). The optimizer fuses combinator chains, performs push-down, and selects execution strategies -- the same optimization space as piescript's query compilation. The `PCollection` is essentially a distributed functor.

Key relevance to piescript: (1) deferred evaluation -- combinators build a plan rather than executing immediately, enabling whole-program optimization before execution; (2) combinator fusion -- adjacent map/filter operations are fused into single passes; (3) the plan-then-execute architecture parallels piescript's partial evaluation producing a residual that is then optimized and executed.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- informs: [[combinator-fusion.performance]] -- FlumeJava's optimizer fuses combinator chains
- informs: [[lowering-pass.performance]] -- deferred evaluation + plan optimization before execution
- informs: [[stream-a.language]] -- `PCollection` is the same concept as piescript's `Stream a`
- informs: [[push-down-compilation.performance]] -- FlumeJava selects execution strategies via push-down
- informs: [[nbe-compilation.esql]] -- deferred combinators producing a plan is the same as NbE partial evaluation
- part-of: [[papers.hub]]
- related: [[bird-meertens.types]] -- FlumeJava's parallelization is grounded in BMF homomorphism theory
- analogous-to: [[esql-compilation.esql]] -- piescript compiles combinators to ESQL plans, FlumeJava compiles to MapReduce plans
