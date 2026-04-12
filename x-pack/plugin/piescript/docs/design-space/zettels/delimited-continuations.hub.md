---
tags: [control-flow, continuation, hub, theoretical, someday]
refs:
  - resource:https://doi.org/10.1016/0304-3975(90)90147-A
  - resource:http://okmij.org/ftp/continuations/
---
# Delimited Continuations

First-class delimited continuations as a unifying control flow mechanism. Subsumes loops, exceptions, generators, coroutines, and algebraic effects. Major power, major complexity. Distributed complications are a first-class obstacle.

The core idea: capture "the rest of the computation" up to a delimiter and reify it as a first-class value that can be invoked, stored, or passed. This single mechanism subsumes every structured control flow pattern — loops (repeat = invoke captured continuation), exceptions (throw = abort to handler delimiter), generators (yield = suspend to consumer delimiter), coroutines (suspend/resume = continuation swap).

The distributed setting is the fundamental obstacle. When a continuation spans a network boundary (shift inside shipped code, reset on the initiator), the captured "rest of the computation" lives partly on one node and partly on another. This makes naive adoption impossible and demands either static restrictions or serializable continuation frames.

Resources: Danvy & Filinski "Abstracting Control" (1990), Oleg Kiselyov's continuations page, Filinski "Representing Monads" (1994).

**Includes**: [[shift-reset.control]], [[call-cc.control]], [[one-shot-continuations.control]], [[multi-shot-continuations.control]], [[answer-type-polymorphism.types]], [[cps-transform.compilation]], [[state-machine-loop.compilation]], [[block-params-ir.compilation]], [[distributed-continuations.obstacle]], [[stackless-coroutines.control]], [[stackful-continuations.control]]

**Depends on**: [[cek-machine.evaluation]] -- needs reified continuations
**Enables**: [[algebraic-effects.types]]
**Connections**:
- subsumes: [[recursion.hub]] -- any loop is expressible via continuations
- subsumes: [[fused-loop-match.language]] -- loop-match is a restricted continuation pattern
- enables: [[algebraic-effects.types]] -- effects are sugar over delimited continuations
- tension-with: [[code-mobility.coordination]] -- distributed continuations are hard
- tension-with: [[execution-model.question]] -- continuation representation depends on model
- informs: [[pattern-matching.hub]] -- match failure could be modeled as an effect
