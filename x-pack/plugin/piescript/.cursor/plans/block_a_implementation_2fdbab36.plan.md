---
name: Block A Implementation
overview: "Implement Block A: spawn + single-value when with a uniformly async evaluator, new CoreExpr/Value/MonoType variants, ANTLR grammar extensions, elaboration, and integration tests."
todos:
  - id: step1-variants
    content: "Step 1: Add new variants across all layers (ANTLR grammar: spawn/when tokens + rules; CoreSpawn/CoreWhen in CoreExpr; SpawnVal in Value; Channel TCon; Spawns.java + Whens.java elaboration; CorePrinter cases; parser + elaborator tests)"
    status: completed
  - id: step2-async-eval
    content: "Step 2: Async evaluator refactor -- change evaluate() signature to ActionListener-based, transform all CoreExpr cases to CPS, make applyFunction/executeBuiltin async, add Executor to constructor"
    status: completed
  - id: step3-spawn-join-eval
    content: "Step 3: Implement spawn and when evaluation -- CoreSpawn forks to executor with SubscribableListener, CoreWhen uses positional AtomicArray + CountDown collector (done as part of Step 2)"
    status: completed
  - id: step4-transport
    content: "Step 4: Wire async evaluator into TransportPiescriptAction (both eval and dev pipelines)"
    status: completed
  - id: step5-tests
    content: "Step 5: Update all existing tests to use PlainActionFuture bridge, add spawn/when unit tests and integration tests for concurrent queries"
    status: completed
  - id: step6-docs
    content: "Step 6: Record D-041, update roadmap/current-state/architecture docs, create mvp.md"
    status: completed
isProject: false
---

# Block A: `spawn` + Single-Value `when` (Async Coordination)

## Design Decisions (to record in D-041)

Three decisions over what the roadmap currently describes:

- `**when` keyword instead of `join**`: The surface keyword for channel synchronization is `when`,
not `join`. In the ES/ESQL ecosystem, "join" universally means data joining (SQL JOIN, enrich
joins, lookup joins). Using `join` for channel synchronization would create a naming collision
within programs that also do data joining. `when` reads naturally as reactive coordination:
"when these channels are ready, do this." The Core IR node is `CoreWhen` and the elaboration
helper is `Whens.java`. The underlying Join Calculus theory is unchanged — `when` is simply
the user-facing name for join patterns.
- **Uniformly async evaluator**: No separate sync/async code paths. Every `evaluate` call takes an
`ActionListener<Value>`. For pure expressions, `listener.onResponse(value)` fires immediately on
the calling thread. This mirrors ES's own `ActionListener` conventions throughout the transport
layer.
- **Positional channel collector for `when`**: Do not use `GroupedActionListener` — its
`onResponse` stores results by arrival order, not binding order, which would break de Bruijn
indexing. Instead, use a hand-rolled positional collector: an `AtomicArray<Value>(n)` + `CountDown(n)`.
Each binding writes to its known slot index; when the countdown reaches zero, the delegate fires
with a correctly-ordered array. This is straightforward and guarantees binding-order results
regardless of which channel completes first.

## Syntax

```
spawn <expr>
when (<chanExpr> <var>) & (<chanExpr> <var>) & ... -> <body>
```

Example:

```
let ch = spawn (query `FROM logs-*`);
when (ch results) -> results |> map .message
```

## Step 1: New Variants (Grammar + Core IR + Types + Values)

A single step that adds the new "vocabulary" across all layers. No behavioral changes yet.

**ANTLR Grammar** (`[PiescriptLexer.g4](x-pack/plugin/piescript/src/main/antlr/PiescriptLexer.g4)`, `[PiescriptAntlrParser.g4](x-pack/plugin/piescript/src/main/antlr/PiescriptAntlrParser.g4)`):

- Lexer: add `SPAWN`, `WHEN`, `AMP`, `ARROW` tokens (if `ARROW` doesn't already exist)
- Parser: add `SpawnExpr` and `WhenExpr` alternatives to the `primary` rule. `WhenExpr` has a `whenBinding+` list separated by `AMP`, each binding being `LPAREN expr ident RPAREN`, followed by `ARROW expr`

**Core IR** (`[CoreExpr.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/core/CoreExpr.java)`):

- `CoreSpawn(Source, CoreExpr body, MonoType type)` -- child: `body`; type is `Channel bodyType`
- `CoreWhen(Source, List<WhenBinding> bindings, CoreExpr body, MonoType type)` -- `WhenBinding` is a record `(CoreExpr channel, @Nullable String debugName)`; children: channel exprs + body; type is `body.type()`
- Update the `permits` clause on `CoreExpr`

**Type system** (`[MonoType.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/types/MonoType.java)`):

- No new `MonoType` variant needed. `Channel` is a `TCon("Channel")` used via existing `AppType`: `AppType(TCon("Channel"), tau)`. Add a constant `Elaborator.CHANNEL = new MonoType.TCon("Channel")` alongside the existing `STREAM`.

**Runtime values** (`[Value.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/Value.java)`):

- `SpawnVal(SubscribableListener<Value> channel)` -- wraps a single-completion channel

**Elaboration** (`[Elaborator.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/elab/Elaborator.java)`):

- Add `case PiescriptAntlrParser.SpawnExprContext s -> Spawns.spawn(this, s, ctx)` to `elaborate()`
- Add `case PiescriptAntlrParser.WhenExprContext w -> Whens.when_(this, w, ctx)` to `elaborate()`
- Create `Spawns.java`: elaborate body, return `CoreSpawn` with type `AppType(CHAN, bodyType)`
- Create `Whens.java`: elaborate each channel expr (expect `AppType(CHAN, tau)`), bind each variable with type `tau` in the context (de Bruijn), elaborate body in extended context, return `CoreWhen`

**CorePrinter**: add cases for `CoreSpawn` and `CoreWhen` display.

**Tests**: parser tests for new syntax forms, elaborator tests for type inference (`spawn` produces `Channel tau`, `when` binds correct types).

## Step 2: Async Evaluator Refactor

The core architectural change. Transforms the evaluator from `Value evaluate(CoreExpr, Value[])` to `void evaluate(CoreExpr, Value[], ActionListener<Value>)`.

**Signature change** in `[Evaluator.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/eval/Evaluator.java)`:

```java
// Before
Value evaluate(CoreExpr expr, Value[] env) { return switch (expr) { ... }; }

// After
void evaluate(CoreExpr expr, Value[] env, ActionListener<Value> listener) {
    try {
        switch (expr) { ... }
    } catch (Exception e) {
        listener.onFailure(e);
    }
}
```

The public entry point becomes:

```java
public void evaluate(CoreExpr expr, ActionListener<Value> listener) {
    evaluate(expr, EMPTY_ENV, listener);
}
```

**Case-by-case transformation** -- every `yield`/`return` becomes `listener.onResponse(...)`, and every recursive `evaluate(sub, env)` that feeds a next step becomes `evaluate(sub, env, listener.delegateFailureAndWrap((l, val) -> ...))`:

- **Leaf nodes** (CoreLit, CoreVar, CoreFree, CoreLam): synchronous -- call `listener.onResponse(value)` directly. No callback chain.
- **CoreLet**: `evaluate(rhs, env, listener.delegateFailureAndWrap((l, rhsVal) -> evaluate(body, prepend(rhsVal, env), l)))`
- **CoreApp**: evaluate `fn`, then in callback evaluate `arg`, then in callback apply. This is where the CPS stacking shows up most.
- **CoreRecord**: sequential evaluation of each field via a recursive helper that threads through an accumulator and listener.
- **CoreProject/CoreUpdate**: evaluate sub-expression, then in callback do the sync field work and respond.
- **CoreTypeAbs/CoreTypeApp**: delegate to child, same listener.
- **CorePrimOp**: evaluate args (sequential callbacks), then compute sync and respond.
- **CoreQuery**: change from `client.execute(...).actionGet()` (blocking) to `client.execute(EsqlQueryAction.INSTANCE, request, listener.delegateFailureAndWrap((l, response) -> { l.onResponse(EsqlValueConverter.convertResponse(response)); response.close(); }))` -- truly async.
- **CoreSpawn / CoreWhen**: see Step 3.

`**applyFunction` also becomes async: `void applyFunction(Value fn, Value arg, ActionListener<Value> listener)`.

**Built-ins** (`executeBuiltin`): `map`, `filter`, `reduce` iterate over `StreamVal` elements.
`applyFunction` must have an async signature because it calls `evaluate`, but the *behavior* for
pure lambda bodies (the overwhelming common case) is still synchronous — `listener.onResponse()`
fires immediately inline. Use an **iterative while-loop** (not recursive callbacks) to avoid stack
growth: advance an index counter, call `applyFunction`, and if the callback fires synchronously
continue the loop; if it truly suspends (exotic case: lambda body containing `when`), break out
and re-enter the loop from the callback. This is the same pattern ES uses in `ThrottledIterator`.
No stack growth regardless of stream size.

**Key implementation pattern** -- use `listener.delegateFailureAndWrap` for chaining. This is the idiomatic ES pattern: it creates a new listener that on success calls your lambda with `(delegateListener, result)`, and on failure passes through. Example:

```java
case CoreLet let -> evaluate(let.rhs(), env,
    listener.delegateFailureAndWrap((l, rhsVal) ->
        evaluate(let.body(), prepend(rhsVal, env), l)));
```

**Constructor change**: the evaluator needs access to `Executor` (for `spawn`) and optionally `Client` (for queries). Constructor becomes `Evaluator(@Nullable Client client, Executor executor)`. Unit tests can use `EsExecutors.DIRECT_EXECUTOR_SERVICE` for deterministic single-threaded testing.

## Step 3: `spawn` and `when` Evaluation

With the async evaluator in place, add the two new cases:

`**CoreSpawn`:

```java
case CoreSpawn spawn -> {
    var channel = new SubscribableListener<Value>();
    executor.execute(ActionRunnable.wrap(channel, l ->
        evaluate(spawn.body(), env, l)));
    listener.onResponse(new Value.SpawnVal(channel));
}
```

`spawn` returns immediately with a `SpawnVal` wrapping the channel. The body evaluates on a forked thread and completes the channel when done. Failures in the body propagate to the channel (via `ActionRunnable.wrap`), not to the spawning expression's listener.

**Environment sharing safety**: the `env` array is captured by the forked lambda and shared with
the spawning thread. This is safe because `env` is never mutated in place — `prepend` always
allocates a new array, and the language is pure with lexical scoping. Add a comment in the
implementation noting this invariant. Linearity/multiplicities (Phase 6) would formalize this
guarantee at the type level.

`**CoreWhen`:

```java
case CoreWhen when -> {
    var bindings = when.bindings();
    int n = bindings.size();
    var results = new AtomicArray<Value>(n);
    var countdown = new CountDown(n);
    var failure = new AtomicReference<Exception>();

    for (int i = 0; i < n; i++) {
        final int slot = i;
        evaluate(bindings.get(i).channel(), env,
            ActionListener.wrap(chanVal -> {
                var channel = ((Value.SpawnVal) chanVal).channel();
                channel.addListener(ActionListener.wrap(value -> {
                    results.setOnce(slot, value);
                    if (countdown.countDown()) {
                        if (failure.get() != null) {
                            listener.onFailure(failure.get());
                        } else {
                            var whenEnv = env;
                            // Prepend in reverse for de Bruijn indexing
                            for (int j = n - 1; j >= 0; j--) {
                                whenEnv = prepend(results.get(j), whenEnv);
                            }
                            evaluate(when.body(), whenEnv, listener);
                        }
                    }
                }, e -> {
                    failure.compareAndSet(null, e);
                    if (countdown.countDown()) {
                        listener.onFailure(failure.get());
                    }
                }));
            }, e -> {
                failure.compareAndSet(null, e);
                if (countdown.countDown()) {
                    listener.onFailure(failure.get());
                }
            }));
    }
}
```

Each binding writes its channel's result to a **positional slot** (`results.setOnce(slot, value)`),
guaranteeing binding-order regardless of which channel completes first. The `CountDown` fires the
`when` body when all slots are filled. Failures at any level (channel expression evaluation or
channel completion) are captured and propagated.

## Step 4: Transport Action Wiring

`[TransportPiescriptAction.java](x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/TransportPiescriptAction.java)` changes:

`**elaborateAndEvaluate`: instead of `var value = evaluator.evaluate(coreExpr)` (sync), use:

```java
evaluator.evaluate(coreExpr, listener.delegateFailureAndWrap((l, value) ->
    l.onResponse(PiescriptResponse.fromValue(value, type))));
```

`**elaborateAndEvaluateDev**`: same async pattern, but requires special error handling. The dev
endpoint captures eval errors as *data* (the `evalError` field in `DevInfo`), not as transport
failures. The async callback must handle `onFailure` by building a `DevInfo` response with the
error message, rather than calling `listener.onFailure`. Use a wrapping `ActionListener` that
converts failures to dev responses:

```java
evaluator.evaluate(coreExpr, ActionListener.wrap(
    value -> listener.onResponse(PiescriptResponse.fromDev(
        new DevInfo(treeString, core, coreRaw, type, constraints, zonker,
                    diagnostics, value.toString(), null, null, null))),
    e -> listener.onResponse(PiescriptResponse.fromDev(
        new DevInfo(treeString, core, coreRaw, type, constraints, zonker,
                    diagnostics, null, e.getMessage(), null, null)))
));
```

`**CoreQuery` response lifecycle: `convertResponse` materializes all data into a `StreamVal`
before `response.close()` runs. Add a comment noting this assumption — if we ever move to
lazy/streaming conversion, the close-after-convert pattern would need revisiting.

**Evaluator construction**: pass `executor` (the GENERIC thread pool executor) from the transport action to the evaluator.

## Step 5: Test Updates

**Unit tests** (`[EvaluatorTests.java](x-pack/plugin/piescript/src/test/java/org/elasticsearch/xpack/piescript/eval/EvaluatorTests.java)`):

- The `evaluate(String)` helper changes to use `PlainActionFuture` to bridge the async evaluator back to synchronous test assertions:

```java
private Value evaluate(String source) {
    // ... parse + elaborate as before ...
    var future = new PlainActionFuture<Value>();
    new Evaluator(null, EsExecutors.DIRECT_EXECUTOR_SERVICE)
        .evaluate(coreExpr, future);
    return future.actionGet();
}
```

- All 59 existing tests should pass unchanged (the async signature is transparent when callbacks fire synchronously).
- New tests for `spawn`/`when` semantics:
  - `spawn` of a pure expression returns a `SpawnVal`
  - `when` on a completed channel binds the value
  - N-ary `when` waits for all channels
  - Error propagation: spawn body failure surfaces in `when`
  - `spawn` of a query (requires integration test)

**Integration tests** (`[PiescriptIT.java](x-pack/plugin/piescript/src/javaRestTest/java/org/elasticsearch/xpack/piescript/PiescriptIT.java)`):

- Test concurrent queries:

```
let ch1 = spawn (query `FROM piescript-test`);
let ch2 = spawn (query `FROM piescript-test`);
when (ch1 r1) & (ch2 r2) -> { left: r1, right: r2 }
```

- Test spawn + when + transform pipeline
- Test error propagation (spawn a query against a non-existent index)

## Step 6: Documentation

- Record D-041 in `decisions.md`: `when` keyword, uniformly async evaluator, positional collector for `when`
- Update `roadmap.md` Block A task statuses
- Update `current-state.md` with new capabilities
- Update `architecture.md` evaluator section (remove "v0 synchronous" language, describe async model)

## Risk: Stack depth from CPS chaining

For pure programs, every recursive evaluation step chains a callback on the same thread stack. A deeply nested expression like `let x1 = ... in let x2 = ... in ... let x100 = ...` builds 100 frames. This is identical to the current recursion depth, so no regression. If it becomes a problem in the future, a trampoline can be added (same approach as before).

## Risk: Built-in sequential iteration — mitigated

Built-in `map`/`filter`/`reduce` apply a function to each `StreamVal` element. The async
`applyFunction` signature is required because it calls `evaluate`, but the actual behavior for
pure lambdas is synchronous (callback fires inline). The iterative while-loop pattern described
in Step 2 handles this: no recursive callbacks, no stack growth, works correctly even if a lambda
body is genuinely async. This is the same approach ES uses in `ThrottledIterator`.

## Known Limitations (Block A)

- **Top-level `SpawnVal` serialization**: if a program's top-level result is a `spawn` expression
(type `Channel tau`), the `SpawnVal` wrapping a `SubscribableListener` is not meaningfully
serializable. The response will contain an opaque representation of the channel object, similar
to how `Promise.resolve()` in JavaScript returns a Promise object rather than its resolved value.
This is not useful but does not fail. Users should use `when` to unwrap channel values before
returning results. Tracked for future improvement: the elaborator could emit a warning or the
transport layer could auto-unwrap a top-level `SpawnVal` by waiting on its channel.

