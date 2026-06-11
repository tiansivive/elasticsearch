---
name: "Recursion Phase 1: Implicit Recursion + Fused Loop-Match"
overview: "Add recursion to piescript via two complementary mechanisms: (1) implicit recursion — all let-bindings are potentially recursive via tying the knot, with a guarded recursion check at elaboration time; (2) fused loop-match — a structured iteration construct (`loop`/`repeat`) that fuses pattern matching with looping, using a `Repeat a` builtin TCon for type-level enforcement and RepeatVal in the evaluator for the jump mechanism. Together these cover both general recursion (recursive functions) and safe iteration (pagination, accumulation). Out of scope: trampoline/CEK refactoring, tail-call optimization, `let rec` syntax, user-defined corecursion, pattern guards, Variant-based mixed-type arms. Pattern matching (prerequisite) is already implemented."
todos:
  - id: setup-zettels
    content: "Milestone 0: record design discussion in zettelkasten before implementation. New zettels, updates, connections, thread log. See work breakdown."
    status: completed
  - id: implicit-recursion
    content: "Implement implicit recursion: (a) change Evaluator CoreLet case to use tying-the-knot (mutable slot + backpatch), (b) add guarded recursion check in Let.java (static rejection of unguarded self-references). No SentinelVal — guarded recursion catches all invalid cases statically."
    status: completed
  - id: loop-match-grammar
    content: Add LOOP and REPEAT keywords to lexer. Add LoopExpr rule to parser (loop expr | arm1 | arm2 ...). Add RepeatExpr as a normal expression (repeat expr). Verify no ambiguity with existing | usage.
    status: completed
  - id: loop-match-ir
    content: "Add CoreLoop to CoreExpr sealed hierarchy (init expression + List<Alternative> arms, reusing existing Alternative/Pattern from CoreMatch). Add CoreRepeat as a CoreExpr variant (carries the new state expression, typed as Repeat ?S). Add Repeat as a builtin TCon in Prelude (kind: Type -> Type)."
    status: completed
  - id: loop-match-elaboration
    content: "Elaborate LoopExpr: fresh ?state and ?result metas. Elaborate init, unify with ?state. For each arm: infer pattern against ?state, elaborate body. After elaboration, check each arm body type: if it's Repeat ?S, unify ?S with ?state (step arm); otherwise unify with ?result (base arm). repeat expr: check expr against ?state, return type Repeat ?state. repeat outside a loop: elaboration error (no enclosing loop context). Known limitation: mixed-type branches (match ... | ... -> \"done\" | ... -> repeat 1) fail because Keyword and Repeat Double don't unify."
    status: completed
  - id: loop-match-evaluation
    content: "Evaluate CoreLoop: evaluate init, then loop. Each iteration: match state against arms (reuse EvalMatch). If result is RepeatVal, loop again with new state. Otherwise return the value. CoreRepeat evaluation: evaluate inner expr, wrap in RepeatVal. RepeatVal is a Value variant — flows through the CPS listener chain. Only the loop evaluator checks for it. Use deps.executor().execute(...) on repeat for stack safety."
    status: completed
  - id: serialization
    content: "Add CoreLoop and CoreRepeat to CoreExprSerialization with stable byte tags. Add RepeatVal to ValueSerialization — throw IOException (should never be on wire, same pattern as SearcherVal/DocRefVal/WriterVal). Also capture Milestone 1 recursion nuance: no-clone closure capture enables cyclic closure/env graphs, so recursive closure shipping requires cycle-aware closure serialization (reference table + patching). If not implemented in this phase, record as explicit deferred work and close-out limitation with session ref."
    status: completed
  - id: verification
    content: "Unit tests: (a) implicit recursion — recursive factorial, recursive fibonacci, guarded recursion rejection (let x = x + 1), recursive closure over channels (async interleaved), polymorphic recursive function. (b) loop-match — accumulator loop, pagination-style loop, nested pattern in loop arms, loop returning from base case, loop with async body, repeat type error (repeat in non-tail position produces Repeat a which doesn't unify with consuming types). Integration tests: recursive function via REST, loop-match via REST."
    status: completed
  - id: paper-trail-close-out
    content: "Per implementation-plan-workflow.meta.md: thread.md, session zettel, debug scripts (test-dev.sh, test-eval.sh with recursion + loop examples), current-state.md, hub/zettel maturity + connections, ADRs if needed, queue [x]; then zettelkasten reconciliation (discrepancies + confirm new zettels with user)."
    status: completed
isProject: false
---

## Agent guardrails (read first)

- **Stop and ask the user** if requirements conflict, specs are ambiguous, or the next step would
need **substantial unplanned design** (new public surfaces, broad refactors) not covered here or in
linked zettels/ADRs.
- **Do not** expand scope silently or guess intent. **Do not assume** what the user meant—when in
doubt, **ask for guidance** and, if helpful, quote the unclear passage.
- **Prefer** small, reviewable steps. If review is on, halt at the boundaries in **Review policy**.

## Review policy

- **Stop after each todo / milestone**: yes
- **Who validates**: user

## Scope

- **In scope**:
  - Implicit recursion for all let-bindings (tying the knot + guarded recursion check)
  - Fused loop-match construct (`loop`/`repeat` keywords, `CoreLoop`/`CoreRepeat` IR,
  `Repeat a` builtin TCon, elaboration, evaluation)
  - Serialization for new IR nodes
  - Unit tests + integration tests
  - Debug script updates
  - Zettelkasten updates (new zettels for pattern guards, variant arm typing; update existing)
- **Design links**: [[recursion.hub]], [[implicit-recursion.design]], [[tying-the-knot.technique]],
[[guarded-recursion.technique]], [[fused-loop-match.language]], [[pattern-matching.hub]],
[[core-match.language]], [[composite-paging.data]]

### Out of scope (planned non-goals)

- Trampoline / CEK machine refactoring (depends on [[execution-model.question]])
- Tail-call optimization (optimization, not core mechanism)
- `let rec` keyword (rejected — [[let-rec-syntax.language]])
- `fix` combinator as surface syntax (rejected — [[fix-combinator.theory]])
- User-defined corecursion (rejected — [[no-corecursion.decision]])
- Mutual recursion (`let f = ... g ... and g = ... f ...`)
- Exhaustiveness checking for loop-match arms
- `loop` destructuring in lambda params or `when` bindings (future [[pattern-reuse.language]])
- Pattern guards (future — solves conditional repeat without mixed-type branches)
- Variant-based internal arm typing (`#return`/`#repeat` — enables mixed-type branches when Variants arrive)
- ATP-inspired dual-type tracking (parked exploration)

### Deferred work (postponed during implementation)

- (none currently)

## Acceptance criteria

1. `let f = fn x -> match x | 0 -> 1 | n -> n * f (n - 1) in f 5` evaluates to `120`
2. `let x = x + 1 in x` is rejected at elaboration time with a guarded-recursion error
3. `loop 0 | 10 -> "done" | n -> repeat (n + 1)` evaluates to `"done"`
4. `loop { acc: 0, n: 5 } | { acc, n: 0 } -> acc | { acc, n } -> repeat { acc: acc + n, n: n - 1 }` evaluates to `15`
5. `repeat 1 + 2` inside a loop arm is a type error (`Repeat Double` doesn't unify with `Double` for `+`)
6. `repeat` outside a loop is an elaboration error
7. Recursive functions with async operations (queries) inside are stack-safe
8. All existing tests pass (no regressions from implicit recursion change)
9. `./gradlew :x-pack:plugin:piescript:check` passes
10. `./gradlew :x-pack:plugin:piescript:javaRestTest` passes

## Work breakdown

### Milestone 0: Zettelkasten setup (todo: setup-zettels)

Record the design discussion outcomes before writing any code.

**New zettels to create:**

- `recursion-phase1.implementation` — implementation zettel (1:1 with plan). Refs: `plan:recursion_phase1`, `session:a4c44992-3966-4627-a399-19f52f7da836`. Links to recursion.hub, implicit-recursion.design, fused-loop-match.language, guarded-recursion.technique, tying-the-knot.technique, pattern-matching.hub.
- `recursion-phase1.queue` — queue zettel mirroring plan todos as checklist.
- `pattern-guards.language` — `[language, syntax, control-flow, pattern-matching, open, needs-design, next]`. Guards on match/loop arms (`| pat when cond -> body`). Solves conditional repeat without mixed-type branches. Higher priority than Variant approach. Connects to: pattern-matching.hub, fused-loop-match.language, recursion.hub, exhaustiveness-checking.types, match-syntax.language.
- `variant-arm-typing.language` — `[language, types, control-flow, pattern-matching, open, needs-design, later]`. Internal `#return`/`#repeat` variant tags wrapping arm codomains. Enables mixed-type branches (normal return + repeat in same match). User never sees the Variant. CoreLoop desugars to CoreMatch. Implemented once in match elaboration; loop gets it for free. Connects to: fused-loop-match.language, pattern-matching.hub, recursion.hub, adts.types (row-based Variants prerequisite), core-match.language.
- `repeat-tcon.types` — `[types, recursion, iteration, decided, concept]`. Refs: `session:a4c44992-3966-4627-a399-19f52f7da836`. `Repeat a` as builtin TCon (kind: Type → Type). Opaque — doesn't unify with consuming types. Loop elaboration classifies arms by checking for `AppType(TCon("Repeat"), ?s)`. Chosen over: tail-position tracking (too invasive), ATP (not fully solved), grammar restriction (too conservative), bottom type (defeats static checking). Connects to: fused-loop-match.language, recursion.hub, kind-system.types, prelude.language, core-match.language.
- `repeat-design-exploration.note` — `[language, control-flow, recursion, iteration, note, paper-trail]`. Refs: `session:a4c44992-3966-4627-a399-19f52f7da836`. Records the full design exploration: tail-position tracking (rejected — per-case invasiveness), ATP/answer-type-polymorphism (promising but not fully solved for piescript's use case, reference to yap compiler), grammar restriction (fallback — too conservative), RepeatSignal/onFailure (evaluation-only, no static checking), bottom type (defeats purpose). Settled on `Repeat a` TCon. Connects to: repeat-tcon.types, delimited-continuations.hub, answer-type-polymorphism.types, fused-loop-match.language, strict-evaluation.decision.
- `mixed-type-branches.obstacle` — `[language, types, pattern-matching, obstacle, open]`. Match arms must unify to a single codomain type (standard HM). `Repeat S` and `R` don't unify, so conditional repeat inside a match arm fails. Three future solutions: pattern guards, variant arm typing, ATP dual-type tracking. Connects to: pattern-matching.hub, fused-loop-match.language, repeat-tcon.types, pattern-guards.language, variant-arm-typing.language, answer-type-polymorphism.types.

**Updates to existing zettels:**

- `fused-loop-match.language` — add: settled design (`Repeat a` TCon), known limitation (mixed-type branches), future fix paths (guards, variants, ATP). New connections to repeat-tcon.types, mixed-type-branches.obstacle, pattern-guards.language, variant-arm-typing.language.
- `recursion.hub` — add: `Repeat a` TCon decision, includes repeat-tcon.types. Connections to mixed-type-branches.obstacle, pattern-guards.language, variant-arm-typing.language.
- `answer-type-polymorphism.types` — add connection: explored-for [[fused-loop-match.language]] but not fully solved for piescript's loop/repeat use case.
- `recursion-sentinel.evaluation` — update: superseded by guarded-recursion.technique for this phase (no SentinelVal).

**Thread log:** Append session block to thread.md covering the design exploration.

### Milestone 1: Implicit recursion (todo: implicit-recursion)

**Evaluator change** — `Evaluator.java` CoreLet case:

Current:

```java
case CoreLet let -> evaluate(
    let.rhs(), env,
    listener.delegateFailureAndWrap((l, rhsVal) -> evaluate(let.body(), prepend(rhsVal, env), l))
);
```

New (tying the knot):

```java
case CoreLet let -> {
    // 1. Allocate env with placeholder at position 0 (will be backpatched)
    var recEnv = prepend(Value.NullVal.INSTANCE, env);
    // 2. Evaluate RHS with self-reference available at index 0
    evaluate(let.rhs(), recEnv, listener.delegateFailureAndWrap((l, rhsVal) -> {
        // 3. Backpatch: replace placeholder with actual value
        recEnv[0] = rhsVal;
        // 4. Evaluate body
        evaluate(let.body(), recEnv, l);
    }));
}
```

The `prepend` method currently returns a new array. For tying the knot, `recEnv` must be
mutable — the closure captured during RHS evaluation must see the backpatch. This means
`recEnv` is allocated once and mutated in place (position 0 only). This is safe because:

- Only position 0 is mutated (the new binding)
- Mutation happens before body evaluation begins
- The env-sharing safety invariant ([[env-sharing-safety.invariant]]) is preserved: no concurrent
mutation, pure language, prepend for subsequent bindings allocates new arrays

**No SentinelVal needed.** The guarded recursion check catches all invalid self-references
statically at elaboration time. The placeholder is never observed.

**Guarded recursion check** — `Let.java`, during elaboration:

Mark the binding as "under construction" in the context. On variable lookup, if the binding
is under construction AND we're not under a lambda abstraction, reject statically:
`"binding 'name' cannot reference itself outside a function body"`.

Implementation: add an `underConstruction` flag to the binding entry in `ElaborationContext`.
Set it when entering a let RHS, clear it when entering a lambda body. Check on variable
lookup for de Bruijn index 0.

### Milestone 2: Loop-match grammar (todo: loop-match-grammar)

Lexer additions:

```
LOOP    : 'loop';
REPEAT  : 'repeat';
```

Parser additions:

```antlr
| LOOP expr (BAR alternative)+ # LoopExpr
```

`repeat` is a normal expression (RepeatExpr):

```antlr
| REPEAT expr # RepeatExpr
```

`repeat` is grammatically an expression — it can appear anywhere syntactically. The TYPE
SYSTEM (not the grammar) enforces where it's valid: `Repeat a` doesn't unify with consuming
types, so misuse is a type error.

### Milestone 3: Loop-match IR (todo: loop-match-ir)

```java
// CoreLoop: loop init | pat1 -> body1 | pat2 -> body2
public final class CoreLoop extends CoreExpr {
    // init: the initial state expression
    // arms: List<Alternative> (reuse from CoreMatch)
    // type: the result type (from base-case arm bodies)
}

// CoreRepeat: repeat newState
public final class CoreRepeat extends CoreExpr {
    // expr: the new state expression
    // type: Repeat ?state (the Repeat TCon applied to the state type)
}
```

`Repeat` as a builtin TCon in `Prelude`:

```java
public static final MonoType.TCon REPEAT = new MonoType.TCon("Repeat");
// Kind: Type -> Type
KINDS.put("Repeat", MonoType.Arrow.of(TYPE, TYPE));
```

### Milestone 4: Loop-match elaboration (todo: loop-match-elaboration)

In the elaborator (new `Loops.java`):

1. Fresh meta `?state` for the loop state type
2. Fresh meta `?result` for the loop result type
3. Elaborate init expression, unify its type with `?state`
4. For each arm:
  a. Infer pattern type, unify with `?state` (same as match)
   b. Extend env with pattern variables
   c. Elaborate body as a normal expression
5. After all arms elaborated, classify each arm by its body type:
  - If body type is `AppType(TCon("Repeat"), ?s)` → step arm: unify `?s` with `?state`
  - Otherwise → base arm: unify body type with `?result`
6. The `CoreLoop` type is `?result`

`repeat expr` elaboration:

- Check `expr` against `?state` (the enclosing loop's state type)
- Return type: `AppType(TCon("Repeat"), ?state)`
- If no enclosing loop → elaboration error

The enclosing loop's `?state` meta needs to be accessible during elaboration of arm bodies.
This requires a minimal loop context — not a full delimitation stack, just tracking the
current loop's state meta. Can be a field on `ElaborationContext` or `ElaborationState`.

**Known limitation**: Mixed-type branches inside a loop arm fail:

```piescript
loop 0
| n -> match (n > 10) | true -> "done" | false -> repeat (n + 1)
```

The inner match tries to unify `Keyword` with `Repeat Double` → type error. This is accepted.
Workaround: restructure as separate loop arms. Future fix: pattern guards or variant-based
arm typing.

### Milestone 5: Loop-match evaluation (todo: loop-match-evaluation)

`RepeatVal` as a `Value` variant:

```java
public record RepeatVal(Value newState) implements Value {}
```

CoreRepeat evaluation:

```java
case CoreRepeat repeat -> evaluate(repeat.expr(), env,
    listener.delegateFailureAndWrap((l, newState) ->
        l.onResponse(new Value.RepeatVal(newState))
    ));
```

CoreLoop evaluation:

```java
case CoreLoop loop -> evaluate(loop.init(), env,
    listener.delegateFailureAndWrap((l, initVal) ->
        evaluateLoop(loop.arms(), env, initVal, l)
    ));
```

```java
void evaluateLoop(List<Alternative> arms, Value[] outerEnv, Value state,
                  ActionListener<Value> listener) {
    EvalMatch.matchArms(this, arms, outerEnv, state, new ActionListener<>() {
        @Override
        public void onResponse(Value result) {
            if (result instanceof Value.RepeatVal repeat) {
                // Loop again with new state. Use executor for stack safety.
                deps.executor().execute(() ->
                    evaluateLoop(arms, outerEnv, repeat.newState(), listener));
            } else {
                listener.onResponse(result);
            }
        }
        @Override
        public void onFailure(Exception e) { listener.onFailure(e); }
    });
}
```

**How RepeatVal works**: `repeat expr` evaluates `expr` and wraps it in `RepeatVal`. The
`RepeatVal` flows through the CPS `ActionListener` chain — intermediate callbacks
(`delegateFailureAndWrap`) pass values through without inspecting them. The loop evaluator's
listener is the ONLY place that checks `instanceof RepeatVal`.

If `RepeatVal` reaches a consuming position (primop, record field, etc.), it means the
`Repeat a` type didn't prevent it — which would be a type checker bug. The evaluator trusts
the type checker (D-025).

**Stack safety**: `deps.executor().execute(...)` ensures each iteration runs on a fresh
stack frame from the GENERIC thread pool, even for pure synchronous loops. For async loops
(containing queries/spawns), the callback fires on a fresh thread naturally.

### Milestone 6: Serialization (todo: serialization)

- `CoreLoop`: new byte tag in `CoreExprSerialization`. Serialize init + arms (arms reuse
`Alternative` serialization from `CoreMatch`).
- `CoreRepeat`: new byte tag. Serialize the inner expression.
- `RepeatVal`: throw `IOException` on serialization attempt (same pattern as
`SearcherVal`, `DocRefVal`, `WriterVal`). Should never reach the wire — the `Repeat a`
type prevents it from being part of a serializable expression's result.

Nuance introduced by Milestone 1 (`refs: session:c6f881e5-b782-4685-ac6e-29572892b1d5`):

- Recursive let now depends on shared env identity (no env clone in `CoreLam`) so backpatching
  is visible to closures.
- This allows cyclic runtime graphs for recursive closures (`ClosureVal -> env[0] -> ClosureVal`).
- Current closure/env serialization is tree-shaped; recursive closure shipping therefore needs
  cycle-aware encoding (reference IDs + backpatch on decode) rather than naive recursive walk.
- Scope update for this phase: cycle-aware closure shipping was explicitly pulled into scope by
  user approval and implemented alongside loop/repeat serialization.

## Design notes

### Why both mechanisms?

Implicit recursion gives general recursive functions (`let f = fn x -> ... f ...`). Fused
loop-match gives guaranteed-safe iteration without trampoline. They serve different audiences:

- Implicit recursion: FP-comfortable users writing tree traversals, divide-and-conquer,
complex recursive algorithms
- Loop-match: everyone else writing pagination, accumulation, convergent algorithms

And a clean separation: loop-match = structured tail-call iteration (enforced by `Repeat a`
type), implicit recursion = general recursion for non-tail-call patterns.

### How `Repeat a` provides static safety

`repeat expr` has type `Repeat S` where `S` is the loop state type. `Repeat S` is an opaque
TCon that doesn't unify with any consuming type:

- `repeat 1 + 2` → `Repeat Double + Double` → type error (`+` expects `Double`)
- `let x = repeat 1 in x + 1` → `x : Repeat Double`, `x + 1` → type error
- `(fn x -> repeat x) 1` → returns `Repeat Double` → if this is a loop arm body, the loop
recognizes `Repeat Double` as a step arm. Valid.
- `let x = repeat 1 in x` → `x : Repeat Double`, arm body `Repeat Double` → step arm. Valid
(pointless but not wrong).

The type system prevents RepeatVal from being consumed. The only way it flows through is via
polymorphic/transparent positions (identity function, let body pass-through), which is safe.

### De Bruijn implications for tying the knot

Current `prepend`:

```java
static Value[] prepend(Value v, Value[] env) {
    var newEnv = new Value[env.length + 1];
    newEnv[0] = v;
    System.arraycopy(env, 0, newEnv, 1, env.length);
    return newEnv;
}
```

For tying the knot, we need `recEnv` to be the same array instance that closures captured
during RHS evaluation. This means `prepend` for the let binding must return the array that
gets mutated. Subsequent `prepend` calls (for nested bindings) create new arrays as before —
only the recursive binding's slot is backpatched.

### Known limitation: mixed-type branches

`Repeat S` doesn't unify with `R` (the result type). This means conditionally repeating
inside a match/if doesn't work:

```piescript
-- This FAILS (Keyword ~ Repeat Double):
loop 0
| n -> if n > 10 then "done" else repeat (n + 1)

-- Workaround: separate arms
loop 0
| n when n > 10 -> "done"   -- (requires pattern guards — future)
| n -> repeat (n + 1)

-- Or restructure:
loop 0
| 10 -> "done"
| n -> repeat (n + 1)
```

Future solutions (all deferred):

1. **Pattern guards** — `| pat when cond -> body` separates the condition from the arm body.
  Higher priority than Variant approach.
2. **Variant-based arm typing** — internally wrap arm codomains in `< #return: R | #repeat: S >`.
  Both arms have the same variant type. Implemented once in match elaboration; loop gets it
   for free via `CoreLoop(init, matchExpr)` desugaring.
3. **ATP-inspired dual-type tracking** — `repeat` acts as both `R` and `Repeat S` depending
  on context. Parked exploration.

## Risks, complications, and breaking changes

- **Breaking change: env mutability.** The tying-the-knot approach mutates `recEnv[0]` in
place. This is a deviation from the current immutable-env convention. The mutation is
strictly bounded (position 0, before body evaluation, single-threaded). But it's a
precedent that should be documented.
- **Risk: `prepend` returns new arrays.** Need to ensure the recursive binding's env is the
one closures capture. May need a variant `prependMutable` or change `prepend` behavior for
the recursive case.
- **Risk: `Repeat a` in unexpected positions.** If RepeatVal somehow reaches a consuming
evaluator case, it's an AssertionError ("type checker bug"). The `Repeat a` type should
prevent this, but if there's a gap in the type checking, the error will be opaque. Mitigated
by RepeatVal's toString including a helpful message.
- **Breaking changes: none intended.** The evaluator change (backpatch for all let bindings)
is backward-compatible — non-recursive bindings simply never reference index 0 in the RHS,
so the placeholder is never read and the backpatch is a no-op.
- **Wire format: additive.** New byte tags for `CoreLoop`, `CoreRepeat`. Old programs
without these nodes are unaffected.

## Verification (plan-specific)

```bash
./gradlew :x-pack:plugin:piescript:compileJava
./gradlew :x-pack:plugin:piescript:test
./gradlew :x-pack:plugin:piescript:javaRestTest
./gradlew :x-pack:plugin:piescript:check
```

Unit tests in:

- `EvaluatorTests.java` — implicit recursion cases
- `ElaboratorTests.java` — guarded recursion rejection, `Repeat a` type errors
- New loop-match tests or added to existing test files

Integration tests in:

- `PiescriptIT.java` — recursive function + loop-match via REST

Debug scripts:

- `debug/test-dev.sh` — add recursive function + loop examples
- `debug/test-eval.sh` — same

## Close-out

1. **Mechanical checklist** — Follow implementation-plan-workflow.meta.md.
2. **Zettelkasten reconciliation** — Compare shipped code to recursion.hub and all sub-zettels.
  Update maturity tags (`implemented` where applicable). Verify connections.
3. **New zettels** — Propose any that surface during implementation. Confirm with user.

- Extend `PiescriptIT` with recursive function and loop-match scenarios
- Update `current-state.md` with recursion capabilities

## Design decisions

- **Pre-settled**: implicit recursion ([[implicit-recursion.design]]), tying the knot
([[tying-the-knot.technique]]), guarded recursion ([[guarded-recursion.technique]]),
no `let rec` ([[let-rec-syntax.language]]), no `fix` surface ([[fix-combinator.theory]]),
no corecursion ([[no-corecursion.decision]]), no SentinelVal
- **Decided during design discussion**: `Repeat a` builtin TCon for type-level enforcement
(over tail-position tracking, ATP, grammar restriction, RepeatSignal/onFailure). Known
limitation: mixed-type branches fail. Future fixes: pattern guards, variant arm typing.
- **Decided during implementation** (`refs: session:c6f881e5-b782-4685-ac6e-29572892b1d5`):
implicit recursion correctness requires closures to capture the same env instance used for
`let` backpatching (no env clone in `CoreLam`). This intentionally permits cyclic closure
graphs (`ClosureVal -> env[0] -> ClosureVal`) and required a transport update: closure/env
serialization for shipped code is now cycle-aware (reference IDs + decode-time backpatching),
not a pure tree walk.

## Plan drift

- Original plan had tail-position enforcement for `repeat` and RepeatVal guaranteed not to
leak. Replaced by `Repeat a` TCon approach after extensive design discussion exploring:
tail-position tracking (too invasive per-case), ATP/answer-type-polymorphism (elegant but
not fully solved for our use case), grammar restriction (too conservative — rejects valid
programs), RepeatSignal/onFailure (evaluation-level only, no static checking). The `Repeat a`
TCon is simpler and provides static enforcement via the type system with a well-understood
limitation (mixed-type branches). Future path via Variant-based arm typing is identified.
- Milestone 1 implementation changed closure capture semantics (no env clone) to satisfy
tying-the-knot invariants for recursion. This revealed a previously implicit assumption in
shipping/serialization: recursive closures can form cycles, so closure env serialization needed
explicit cycle handling for remote execution paths. This follow-up was pulled into scope and
implemented in this phase (`refs: session:c6f881e5-b782-4685-ac6e-29572892b1d5`).

