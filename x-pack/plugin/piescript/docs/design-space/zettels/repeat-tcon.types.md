---
tags: [types, recursion, iteration, decided, concept, kinds]
refs:
  - session:a4c44992-3966-4627-a399-19f52f7da836
  - plan:recursion_phase1
  - thread:language-expressiveness
---
# Repeat a — Builtin Type Constructor

`Repeat a` is a builtin TCon (kind: `Type → Type`) used to enforce that `repeat` values
cannot be consumed by type-specific operations. `repeat expr` has type `Repeat S` where
`S` is the loop state type. `Repeat S` is opaque — it doesn't unify with `Double`,
`Keyword`, or any other concrete type.

**How it works**: The loop elaboration classifies each arm body by its type. If the type
matches `AppType(TCon("Repeat"), ?s)`, it's a step arm — unify `?s` with the loop state
type. Otherwise, it's a base arm — unify with the loop result type.

**What it catches statically**:
- `repeat 1 + 2` → `Repeat Double + Double` → type error (`+` expects Double)
- `let x = repeat 1 in x + 1` → `x : Repeat Double`, `x + 1` → type error
- `repeat` outside a loop → elaboration error (no loop state meta in scope)

**What it allows**:
- `(fn x -> repeat x) 1` → lambda returns `Repeat Double`, flows to loop → valid
- `let x = repeat 1 in x` → `x : Repeat Double`, arm body is `Repeat Double` → step arm

**Known limitation**: Mixed-type branches fail — `Repeat S` doesn't unify with `R`:
```piescript
-- FAILS: Keyword ~ Repeat Double
match cond | true -> "done" | false -> repeat 1
```
Future fixes: [[pattern-guards.language]] (higher priority), [[variant-arm-typing.language]].

**Alternatives considered and rejected**:
- Tail-position tracking: too invasive (per-case flag threading in elaborator)
- ATP/answer-type-polymorphism: promising but not fully solved for loop/repeat
- Grammar restriction: too conservative (rejects eta-expansion like `(fn x -> repeat x) 1`)
- Bottom/never type: unifies with everything, defeats static checking
- RepeatSignal/onFailure: evaluation-only, no static enforcement

See [[repeat-design-exploration.note]] for the full exploration.

**Depends on**: [[kind-system.types]], [[f-omega-lite.types]]
**Enables**: [[fused-loop-match.language]]
**Connections**:
- part-of: [[recursion.hub]]
- implements: [[fused-loop-match.language]] — the type-level enforcement mechanism
- uses: [[kind-system.types]] — Repeat registered in Prelude.KINDS as Type → Type
- uses: [[prelude.language]] — Repeat added to builtin type constructors
- uses: [[unification-algorithm.types]] — loop elaboration checks arm types via pattern match on AppType
- tension-with: [[mixed-type-branches.obstacle]] — the known limitation this approach creates
- superseded-by: [[variant-arm-typing.language]] — future: variants replace Repeat TCon with internal tagging
- contrasts-with: [[answer-type-polymorphism.types]] — ATP was explored but not adopted for this use case
