---
tags: [paper, effects, evaluation, theoretical, types, language, continuation, reference]
refs:
  - doc:references.md
  - resource:https://homepages.inf.ed.ac.uk/gdp/publications/handling-algebraic-effects.pdf
---
# Plotkin & Pretnar -- Handlers of Algebraic Effects

Gordon Plotkin and Matija Pretnar. "Handlers of Algebraic Effects." 2009.

The foundational framework for algebraic effects and handlers. Effects (exceptions, state, nondeterminism, I/O) are modeled through equational theories with primitive operations. Handlers yield models of these theories -- a handler interprets effect operations by providing implementations, with the key ability to capture and resume the continuation at the point of the effect.

For piescript, this paper provides the conceptual architecture: coordination primitives (`spawn`, `send`, `when`) are effect operations that describe what should happen; the evaluator is the handler that interprets them. This separation of effect description from effect interpretation is exactly piescript's architecture -- it enables the optimizer to inspect and transform the effect tree (the residual from partial evaluation) before the handler executes it.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- formalizes: [[algebraic-effects.types]] -- the foundational formalization of algebraic effects
- formalizes: [[effect-handlers.types]] -- handlers as models of equational theories
- informs: [[evaluator.language]] -- piescript's evaluator is an effect handler
- informs: [[delimited-continuations.hub]] -- effect handlers capture delimited continuations at effect sites
- informs: [[free-monad.types]] -- free monads are the syntactic representation of algebraic effect trees
- part-of: [[papers.hub]]
- related: [[wu-schrijvers-fusion.paper]] -- Wu & Schrijvers optimize the handlers Plotkin & Pretnar define
- related: [[granule-graded-modal.paper]] -- graded modalities can track effect usage quantitatively
