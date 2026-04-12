---
tags: [paper, effects, performance, compilation, theoretical, evaluation, language, reference]
refs:
  - doc:references.md
  - resource:https://people.cs.kuleuven.be/~tom.schrijvers/Research/papers/mpc2015.pdf
---
# Wu & Schrijvers -- Fusion for Free

Nicolas Wu and Tom Schrijvers. "Fusion for Free: Efficient Algebraic Effect Handlers." *MPC 2015*.

Shows how to fuse chains of free monad / algebraic effect handlers into single-pass handlers, eliminating intermediate data structures. The key insight: when multiple handlers are composed (e.g., a state handler wrapped around an exception handler), naive interpretation allocates intermediate free monad trees at each layer. Fusion collapses these into a single handler that interprets all effects in one pass.

For piescript, this directly informs the optimizer's ability to fuse coordination operations. If piescript's coordination primitives are modeled as effects (which they are -- `spawn`, `send`, `when` are effect operations interpreted by the evaluator), then handler fusion is how the optimizer can collapse chains of coordination operations into more efficient combined operations. Also relevant to combinator fusion: fusing `map . filter . map` into a single pass is the same algebraic transformation.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- informs: [[algebraic-effects.types]] -- fusing algebraic effect handlers
- informs: [[combinator-fusion.performance]] -- the same fusion technique applies to map/filter/reduce chains
- informs: [[effect-handlers.types]] -- handler composition and fusion
- informs: [[free-monad.types]] -- free monad tree elimination via fusion
- informs: [[lowering-pass.performance]] -- fusion is a key optimization in the lowering pipeline
- part-of: [[papers.hub]]
- related: [[plotkin-pretnar-handlers.paper]] -- Plotkin & Pretnar define the handlers; Wu & Schrijvers optimize them
- related: [[flumejava.paper]] -- FlumeJava's combinator fusion is the same idea in a data-parallel setting
