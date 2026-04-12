---
tags: [language, evaluation, effects, purity, concept, open, tech-debt]
refs:
  - code:EvalBuiltins.java
  - adr:D-043
  - thread:language-expressiveness
---
# List.map / Traverse Tension

`List.map` silently sequences effects. When the mapped function performs coordination (queries, sends, spawns), `map` behaves as `traverse` -- it applies the effectful function to each element in order, threading the async continuation through the list. Semantically, this is Haskell's `mapM` / `traverse`, not pure `map`.

The problem: the evaluator's `applyFunction` doesn't distinguish pure from effectful function arguments. A pure `map` should be parallelizable (or at least order-independent); an effectful `traverse` must sequence. Currently they're the same code path, which means:

1. Pure maps pay the cost of sequential async continuation threading (no parallel execution).
2. Effectful maps look like pure maps to the programmer -- no type-level signal that effects are happening.
3. Refactoring a pure map callback to include an effect silently changes evaluation semantics.

When an effect model arrives (via [[algebraic-effects.types]] or [[effect-systems.types]]), `map` and `traverse` should be split:
- `List.map : (a -> b) -> List a -> List b` -- pure, parallelizable
- `List.traverse : (a -> F b) -> List a -> F (List b)` -- effectful, sequenced

Until then, `List.map` is a known impurity: it's traverse wearing map's clothes.

**Depends on**: [[list-type.language]], [[evaluator.language]]
**Enables**: (none directly -- tension to resolve when effects land)
**Connections**:
- tension-with: [[purity.language]] -- map should be pure; current impl silently sequences effects
- informs: [[traverse.language]] -- traverse is the correct name for what map currently does with effectful callbacks
- informs: [[algebraic-effects.types]] -- effect system would distinguish map from traverse at the type level
- implements: [[evaluator.language]] -- applyFunction doesn't distinguish pure from effectful function arguments
- related: [[iterative-streaming.language]] -- the while-loop pattern in EvalBuiltins IS the sequential traverse implementation
- informs: [[effect-systems.types]] -- the map/traverse split is a concrete motivation for effect tracking
- motivates: [[purity-enforcement.language]] -- enforcing purity would catch effectful callbacks passed to map
- tension-with: [[combinator-fusion.performance]] -- fusing map chains assumes purity; effectful map breaks fusion
