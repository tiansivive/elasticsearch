---
tags: [language, control-flow, recursion, iteration, note, paper-trail, continuation, search]
refs:
  - session:a4c44992-3966-4627-a399-19f52f7da836
  - plan:recursion_phase1
---
# Repeat Design Exploration

Paper trail of the design exploration for `repeat` in piescript's loop-match construct.
Started from "how do we detect tail position for repeat" and explored five approaches
before settling on `Repeat a` TCon.

## Approaches explored

**1. Tail-position tracking** (rejected — too invasive)
Thread a `inLoopTail` boolean through the elaborator. Each expression form must decide
whether to propagate or block the flag. Every new CoreExpr form added in the future must
know about this flag. Same invasiveness problem as checking RepeatVal in the evaluator.

**2. ATP / Answer-type polymorphism** (explored — not fully solved)
Inspired by Filinski's answer-type polymorphism for shift/reset. Studied the yap compiler
(`~/Workspace/panlogion/yap`) which implements ATP for a language with shift/reset. The
key mechanism: a delimitation stack with dual answer types (initial/final), swapped at
shift entry. In yap, this prevents non-tail shift usage when types mismatch. For piescript:
the mapping isn't direct because `repeat` doesn't change the answer type (unlike `shift`).
The interesting insight is tracking two types simultaneously — explored but not fully
reconciled with HM unification for piescript's specific needs.

**3. Grammar restriction** (fallback — too conservative)
`repeat` as arm-body form, not a general expression. Parser enforces tail position.
Simple and correct but rejects valid programs like `(fn x -> repeat x) 1` (eta-expansion).
Identified as the fallback if no type-level solution works.

**4. RepeatSignal / onFailure** (evaluation-only — no static checking)
Use ActionListener's failure channel as a non-local jump. `repeat` fires `onFailure`,
`delegateFailureAndWrap` propagates without executing success callbacks, loop catches in
`onFailure`. Dead code truly dead. Clean evaluation mechanism but provides ZERO static
enforcement — the elaborator doesn't know about repeat at all.

**5. `Repeat a` builtin TCon** (chosen)
`repeat expr : Repeat S`. Opaque type — doesn't unify with consuming types. Loop
elaboration classifies arms post-hoc. Simple, no per-case tracking, provides static
enforcement via the type system. Known limitation: mixed-type branches fail. Future
fix paths: pattern guards, variant-based arm typing.

## Key insights from the exploration

- The tension between static enforcement and non-invasiveness is fundamental — you can't
  have both without a type system extension (effects, linearity, or variants).
- ATP is the right theoretical framework but requires adaptation for loop/repeat which
  differs from shift/reset (repeat doesn't change the answer type).
- The `Repeat a` TCon is a pragmatic middle ground: static enforcement for the common
  cases, accepted limitation for mixed-type branches.
- Pattern guards solve the practical problem (conditional repeat) without type system changes.
- Variant-based arm typing is the principled long-term solution when row-based variants arrive.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- documents: [[repeat-tcon.types]] — the exploration that led to the Repeat a decision
- explores: [[answer-type-polymorphism.types]] — ATP studied via yap compiler but not adopted
- explores: [[delimited-continuations.hub]] — shift/reset mechanism studied for repeat semantics
- explores: [[trampolining.technique]] — considered for stack safety, orthogonal to repeat typing
- informs: [[pattern-guards.language]] — identified as higher-priority fix for conditional repeat
- informs: [[variant-arm-typing.language]] — identified as principled long-term solution
- informs: [[mixed-type-branches.obstacle]] — the problem that drove the exploration
