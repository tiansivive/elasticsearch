---
tags: [language, control-flow, hub, now, recursion]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
---
# Recursion

Overview of the recursion design space in piescript.

All bindings are implicitly recursive (Haskell-style) — no `rec` keyword. The elaborator
extends the de Bruijn environment before evaluating the RHS, and the evaluator uses
tying-the-knot (mutable slot + backpatch) to handle self-reference. Recursion safety is
enforced statically via guarded recursion; the runtime recursion sentinel remains
a documented fallback design and is currently deferred.

For structured iteration, a fused loop-match construct provides guaranteed stack safety
(evaluator implements as while-loop). The fix combinator was rejected as surface syntax
(too abstract for target audience) but remains valid as an internal elaboration target.
`let rec` syntax was rejected — implicit recursion is simpler with identical semantics.

**Stack safety:** Async-interleaved recursion is already safe — the CPS evaluator
(`ActionListener` callbacks) fires continuations on fresh GENERIC threads. Pure recursion
without async points is unsafe without a trampoline. The fused loop-match solves this for
structured iteration. General trampoline deferred pending the execution model question.

**Includes**: [[implicit-recursion.design]], [[tying-the-knot.technique]], [[fix-combinator.theory]], [[let-rec-syntax.language]], [[fused-loop-match.language]], [[recursion-sentinel.evaluation]], [[guarded-recursion.technique]], [[no-corecursion.decision]], [[repeat-tcon.types]], [[repeat-design-exploration.note]], [[mixed-type-branches.obstacle]]

**Depends on**: [[pattern-matching.hub]], [[evaluator.language]]
**Enables**: [[composite-paging.data]]
**Connections**:
- tension-with: [[stack-depth.language]] — unbounded pure recursion dangerous without trampoline
- complements: [[recursive-types.types]] — recursive functions and recursive types are co-dependent features
- constrains: [[execution-model.question]] — recursion safety depends on evaluator architecture
- uses: [[cps-evaluation.language]] — async interleaving provides natural stack safety
- uses: [[trampolining.technique]] — proper stack safety for pure recursion (deferred pending execution model question)
- specializes: [[delimited-continuations.hub]] — continuations subsume recursion; recursion is a restricted form
- uses: [[guarded-recursion.technique]] — static check that self-references are under lambdas; primary enforcement (runtime sentinel deferred)
- informs: [[codata.types]] — codata relaxes guarded recursion; `let x = x + 1` is valid for coinductive types
- informs: [[anamorphisms.types]] — fused loop-match is an anamorphism with early termination
- constrained-by: [[no-corecursion.decision]] — user-defined corecursion explicitly rejected; streams come from infrastructure only
- uses: [[repeat-tcon.types]] — Repeat a TCon for type-level enforcement of loop/repeat
- tension-with: [[mixed-type-branches.obstacle]] — Repeat a creates limitation on mixed-type branches
- enhanced-by: [[pattern-guards.language]] — future: guards solve conditional repeat
- enhanced-by: [[variant-arm-typing.language]] — future: variants enable mixed-type arms
- supersedes: recursion.language (deleted) — this hub replaced the original monolithic zettel
