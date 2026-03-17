# Current State

> **Living doc** — update after every implementation session. This is the ground truth for "what
> exists right now."
>
> **Last updated**: 2026-03-17 (Block A complete; roadmap restructured for distributed vertical
> slice — D-042)

## Summary

**Phase 0 is complete. Phase 1 (sub-phases 1a–1d + D-035) is complete. Phase 2 (Index Resolution +
Concrete-Row Constraints + Eager Evaluation) is complete. Block A (spawn + single-value when) is
complete.** The evaluator is uniformly asynchronous (CPS / `ActionListener`-based), with `spawn`
forking computations to the GENERIC thread pool and `when` synchronizing on channels via a
positional collector. Pure expressions complete synchronously inline — no separate code paths.
ESQL queries fire asynchronously via `ActionListener`. Built-in functions (`map`, `filter`,
`reduce`) use `SubscribableListener` chaining for stack-safe iteration. 15 integration tests,
79 evaluator unit tests, 106 elaborator tests, 99 parser tests — all passing.

**Phase 1e (Pattern Matching) is deferred** — not blocking the MVP-critical path. The execution
model is the Join Calculus (D-040), with `spawn`/`when`/channels as coordination primitives.
The roadmap has been restructured (D-042) around a distributed vertical slice: Block B (ES
topology), Block C (cross-node execution), Block D (local data access via `scan`). See
[roadmap.md](roadmap.md) for the block structure, [mvp.md](mvp.md) for concrete examples of what
piescript enables today, and [roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt)
for Phase 1 items carried forward.

## What Works

| Capability | Details |
|-----------|---------|
| REST endpoint | `POST /_piescript/eval` accepts `{"program": "..."}` |
| Dev endpoint | `POST /_piescript/dev` returns CST (`tree`), elaborated Core IR (`core`), raw Core IR (`core_raw`), constraints (`constraints`), zonker substitutions (`zonker`), resolved type (`type`), and evaluated result (`eval`). Parse errors return `parse_error`; type errors return `tree` + `type_error`; eval errors return `eval_error`. |
| Query typing + evaluation (Phase 2) | `` query `FROM idx` `` type-checks against real index mappings via `IndexResolver`, producing `Stream { field: Type, ... }`. The evaluator fires `EsqlQueryAction` asynchronously via `ActionListener`, converts response rows to `RecordVal`s via `EsqlValueConverter`, and returns a `StreamVal`. Built-in functions (`map`, `filter`, `reduce`) operate over materialized streams. |
| `spawn` / `when` (Block A) | `spawn <expr>` forks computation to the GENERIC thread pool, returning a `SpawnVal(SubscribableListener<Value>)` — a typed single-value channel. `when (ch1 x) & (ch2 y) -> body` synchronizes on one or more channels using a positional collector (`AtomicArray` + `CountDown`), binding channel results to variables in the body. Supports concurrent multi-index queries. |
| `Channel τ` type + `SpawnVal` (Block A) | `Channel` is a type constructor (`AppType(TCon("Channel"), tau)`). `SpawnVal` wraps a `SubscribableListener<Value>`. The elaborator infers `Channel τ` for `spawn` expressions and unwraps it in `when` bindings. |
| Uniformly async evaluator (Block A) | Every `evaluate` call takes an `ActionListener<Value>`. Pure expressions fire callbacks synchronously inline (zero overhead). Coordination primitives (`spawn`, `when`, `query`) are truly async. No separate sync/async code paths (D-041). |
| Expression evaluation (Phase 1c) | Non-query programs go through parse → elaborate → evaluate pipeline, returning `{"type": "...", "result": ...}` |
| Request validation | Empty/blank programs rejected with 400 |
| Security | RBAC authorization via `shouldAuthorizeIndexActionNameOnly()`, operator privileges allowlist |
| Integration tests | 15 passing test cases covering query type-checking, eager evaluation (stream, map, filter, reduce), expression evaluation, error handling |
| Build | Compiles, passes `check`, `spotlessJavaCheck`, `javaRestTest` |
| ANTLR grammar | Lexer (`PiescriptLexer.g4`) and parser (`PiescriptAntlrParser.g4`) implementing full D1.17 surface syntax plus `SPAWN`, `WHEN`, `AMP` tokens and `SpawnExpr`/`WhenExpr` rules (Block A) |
| Parser entry point | `PiescriptParser.java` — invokes ANTLR, produces parse tree; `parseToTreeString()` for CST inspection |
| Core IR printer | `CorePrinter` in `piescript.core`: `printExpr` (zonked), `printExprRaw` (bare metas/rigids), `printConstraints`, `printZonker` — used by dev endpoint for debugging |
| Parser unit tests | `PiescriptParserTests.java` — comprehensive coverage of every syntax form plus error cases |
| Type data structures (Phase 1b) | `Kind`, `MonoType`, `RowType`, `TypeScheme`, `LitVal`, `Op` in `piescript.types` package |
| Core IR (Phase 1b + D-035 + Phase 2 + Block A) | `CoreExpr` sealed hierarchy in `piescript.core`: `CoreVar`, `CoreFree`, `CoreLit`, `CoreLam`, `CoreApp`, `CoreLet`, `CoreRecord`, `CoreProject`, `CoreUpdate`, `CorePrimOp`, `CoreTypeAbs`, `CoreTypeApp`, `CoreQuery`, `CoreSpawn`, `CoreWhen` — extends `Node<CoreExpr>` with `MonoType` on every node. `CoreTypeAbs` and `CoreTypeApp` are unary System F nodes. `CoreFree` is a module-level free variable (built-ins). `CoreQuery` carries an ESQL query string and its resolved stream type. `CoreSpawn` wraps a body expression (type `Channel bodyType`). `CoreWhen` carries a list of `WhenBinding(CoreExpr channel, @Nullable String debugName)` and a body. |
| Type unit tests (Phase 1b) | `TypeDataStructureTests.java` — construction, equality, sealed hierarchy, factory methods |
| Core IR unit tests (Phase 1b) | `CoreExprTests.java` — construction, accessors, equality, replaceChildren, tree traversal |
| Elaboration context (Phase 1b + Phase 2) | Immutable `ElaborationContext` in `piescript.elab`: typing context (Γ) with de Bruijn-indexed local bindings + module-level free variable map + binding level, passed by value through recursive descent. `lookup()` checks local bindings first; `lookupModule()` falls back to the module map. Local variables shadow module-level names. |
| Elaboration state (Phase 1b + D-035) | Mutable `ElaborationState` in `piescript.elab`: metavar supply + zonker with chain resolution + constraint accumulator. `emitConstraint(left, right, line, column)` for deferred constraints. `constraints()` returns the accumulated list. |
| Elaboration unit tests (Phase 1b) | `ElaborationContextTests.java` (immutability, de Bruijn indexing, shadowing, scope unwinding) + `ElaborationStateTests.java` (metas, zonker, integrated let-polymorphism workflow) |
| Unification (Phase 1b+1d) | `Unifier` in `piescript.elab`: Robinson unification with occurs check, null-as-bottom (D1.11), Leijen-style open-row unification (D-030) with tail solving. Returns `Optional<TypeError>`. |
| Type errors (Phase 1b) | `TypeError` sealed interface: `Mismatch`, `InfiniteType`, `FieldMismatch`, `MissingFields` |
| Unification unit tests (Phase 1b) | `UnifierTests.java` — meta solving, transitive chains, occurs check, null-as-bottom, arrow/record/app structural matching, cross-form mismatch |
| Elaborator (Phase 1b + D-035 + Phase 2) | `Elaborator` in `piescript.elab`: pattern-matching recursive descent over ANTLR CST → Core IR. Bidirectional HM type inference with deferred constraint solving, `generalize` (metas → Rigids in zonker + `CoreTypeAbs`), `instantiateAndWrap` (parameterized by `Function<MonoType, CoreExpr>` factory — produces `CoreVar` for local bindings, `CoreFree` for module-level free variables), primops as typed functions (concrete Integer-only signatures, D-020), desugaring (multi-param lambda, pipe, accessor, update sugar, blocks, top-level bindings). Two-tier variable lookup: local de Bruijn bindings then module-level free variables. |
| Prelude (Phase 2) | `Prelude` in `piescript.elab`: defines the module map of built-in function type schemes (`map`, `filter`, `reduce`) and their arities. Type schemes use pre-allocated Rigid IDs (negative, disjoint from `ElaborationState.freshRigid`). Wired into `ElaborationContext.withModule(Prelude.MODULE)` at elaboration start. |
| Type walker (Phase 1b, reduced by D-035) | `TypeWalker` in `piescript.elab`: `resolveDeep` (used by `CorePrinter` for display) and `collectMetas` (used by `Elaborator.generalize`). `walkType`, `generalize`, and `instantiate` were deleted by D-035. |
| Elaboration exception (Phase 1b) | `ElaborationException`: unchecked, fail-fast, wraps source location + optional `TypeError`. |
| Elaborator tests (Phase 1b + D-035 + Block A) | `ElaboratorTests.java` — 106 tests covering all Phase 1b/D-035 tests plus `spawn`/`when` type inference (channel type production, unwrapping, multi-binding scenarios, type errors for non-channel `when` bindings). |
| Runtime values (Phase 1c + Phase 2 + Block A) | `Value` sealed interface in `piescript.eval`: `IntegerVal`, `LongVal`, `DoubleVal`, `KeywordVal(String)`, `BooleanVal`, `NullVal`, `RecordVal`, `StreamVal(List<Value>)`, `ClosureVal`, `BuiltinVal(name, arity, partialArgs)`, `SpawnVal(SubscribableListener<Value>)`. `StreamVal` is the eagerly materialized stream representation. `BuiltinVal` supports curried partial application. `SpawnVal` wraps a single-completion channel (Block A). |
| ESQL value converter (Phase 2) | `EsqlValueConverter` in `piescript.eval`: converts `EsqlQueryResponse` rows to `StreamVal`. Each row becomes a `RecordVal` (column names → field keys, cell values → field values via `instanceof` dispatch). Handles `Integer`, `Long`, `Double`, `String`, `Boolean`, `null`, multi-value fields (v0: first element only). |
| Evaluator (Phase 1c + D-035 + Phase 2 + Block A) | Uniformly async tree-walking de Bruijn environment machine, split across four classes: `Evaluator` (core dispatch + `CoreExpr` cases), `EvalPrimOps` (arithmetic, comparison, boolean ops), `EvalBuiltins` (stream processing — `map`/`filter`/`reduce` via `SubscribableListener` chaining), `EvalCoordination` (`when` evaluation via `PositionalCollector`). Takes `Client` and `Executor`. `CoreQuery` fires `EsqlQueryAction` asynchronously. `CoreSpawn` forks to executor, returns `SpawnVal`. `CoreWhen` uses positional collector to synchronize channels and extend the de Bruijn environment. |
| PiescriptResponse (Phase 1c + Phase 2) | Wrapper response: expression results (`{"type": ..., "result": ...}`), including `StreamVal` serialized as JSON arrays. Implements `ChunkedToXContentObject` and `Releasable`. |
| Transport pipeline (Phase 2 + Block A) | Unified async pipeline: parse → index resolution pre-pass → elaborate → evaluate (via `ActionListener`). Runs on `ThreadPool.Names.GENERIC` (D-004 revision). The evaluator completes the transport `ActionListener` when done — including after async `spawn`/`when` resolution. |
| Evaluator tests (Phase 1c + Phase 2 + Block A) | `EvaluatorTests.java` — 79 tests covering: all Phase 1c/Phase 2 tests plus `spawn`/`when` semantics (pure values, computations, multiple bindings, nested spawn, lambda bodies), async evaluation with real thread pools (`Executors.newFixedThreadPool`), and deterministic tests with `DIRECT_EXECUTOR_SERVICE`. |

## What Does Not Exist Yet

| Capability | Target Block | Notes |
|-----------|-------------|-------|
| Pattern matching | 1e (deferred) | No match expressions (deferred — not blocking Blocks A+; see D-029) |
| Double/float arithmetic | Phase 1 tech debt | D-020: PrimOps are integer-only. `Long` and `Double` values exist but can't participate in arithmetic. Blocks user-defined aggregates (e.g., p-series weighted sum). |
| Math functions (`pow`, `sqrt`, etc.) | Phase 1 tech debt | No exponentiation or standard math functions. |
| `sort` / `take` combinators | Block C | No sorting or top-N selection within piescript. Must push into ESQL. |
| `groupBy` combinator | Block C+ | No grouping/aggregation semantics within piescript. Must push into ESQL. |
| Multi-value channels | Block B | Block A channels are single-value only |
| `newchan` / `send` primitives | Block B | No explicit channel creation or message sending |
| `writeTo` sink primitive | Block C | No mechanism to write stream results to an index |
| Scheduled async execution | Block C | No persistent task or scheduler |
| Push-down optimizer | Block D | No compilation of piescript transforms to ESQL expressions |
| Exchange integration | Block E | No streaming data flow via ESQL's compute engine |
| Feature flag / license gating | TBD | No gating mechanism |

## Known Deviations from Plan (Technical Debt)

These are implementation deviations from the accepted design decisions, tracked for resolution:

1. **`resolveDeep` still used by `CorePrinter` (D-032 deviation).** D-032 calls for removing
   `TypeWalker.resolveDeep`. D-035 eliminated its use in `generalize` and `instantiate`, but
   `CorePrinter` still uses it for display. The long-term fix is environment-based Rigid
   resolution in downstream passes (printer, optimizer, lowering).

2. **`zonkOrKeep` instead of `Optional`-returning `zonk` (D-032 deviation).**
   `ElaborationState.resolveType` was renamed to `zonkOrKeep` with the old return-meta-on-miss
   semantics, not the `Optional<MonoType>` API specified by D-032.

3. **Bidirectional checking mode partially implemented (D-036).** The elaborator now has both
   `elaborate` (synthesis) and `check` (checking) modes. Checking propagates expected types
   inward for lambdas, records, let/block bodies, and ascription. However, polytype ascription
   (`(e : ∀a. τ)`) at expression level does not work correctly — see item 4.

4. **`MonoType` cannot represent polytypes (D-038).** `CoreTypeAbs` (type abstraction `Λa. e`)
   has type `∀a. τ` in System F, but `MonoType` has no `Forall` variant, so `CoreTypeAbs.type()`
   returns the body's monotype with bare rigids. This means polytype ascription produces orphaned
   rigids that `generalize` cannot discover (it only collects unsolved metas). The fix is to
   rename `MonoType` to `Type`, add a `Forall` variant, and update all consumers. Related tests
   are skipped with `@AwaitsFix`. The annotated-let path (`let f : a -> a = ...`) works because
   the scheme is constructed directly from the annotation, bypassing this issue.

## Known Limitations and Shortcuts

These are intentional simplifications that will need attention:

1. **No semicolon handling in queries.** `extractEsqlQuery()` splits on the last `;`. If the ESQL
   query itself contains a semicolon (e.g., in a string literal), it will break. This is fine
   because the `query ... ;` wrapper is temporary syntax.

2. **Full materialization.** Every ESQL query result is fully materialized into a `List<Value>` on
   the coordinator node. No streaming, no pagination, no back-pressure. Large result sets will OOM.
   Resolution: Exchange streaming (orchestrated explicitly by piescript via channels).

3. **Coordinator-bound compute.** All `map`/`filter`/`reduce` runs on the coordinator node. Data
   travels from data nodes to the coordinator, gets materialized, then processed. Resolution:
   distributed execution (Blocks B–D: ship compute to data nodes via `send`) + typeclass push-down.

4. **O(n×m) cross-index joins.** The enrichment pattern (filter inner collection per outer element)
   is a nested loop. No hash join, no index lookup. Fine for small-to-medium datasets. Resolution:
   distributed joins or typeclass-driven push-down to Lucene (future).

5. **`KeywordVal` uses `String`, not `BytesRef`.** The evaluator converts ESQL's `BytesRef` keywords
   to `String` at the `CoreLit` boundary (D-026). When ESQL query results flow into piescript
   expressions, the reverse conversion will be needed.

6. **Integer-only arithmetic.** PrimOps are typed as `Integer -> Integer -> Integer` (D-020).
   `Long` and `Double` literals exist but cannot participate in arithmetic operations. This is
   the most impactful gap for real-world use cases (e.g., risk scoring requires float math).

7. **ES conventions tech debt.** Several items related to Elasticsearch plugin conventions and
   production readiness: no `TransportVersion` guards on serialization, no logging, older
   `ActionListener.wrap()` patterns, `ActionType` name scope review needed, and more. See
   [roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt)
   for the full list.

## MVP North Star

> **Revised**: 2026-03-17. MVP target shifted from "unified data pipelines" to "distributed
> vertical slice" (D-042).

The end goal for the foundations being built now is the **Distributed Vertical Slice MVP**: a
piescript program that discovers cluster topology, ships code to data nodes, accesses local data
via `scan`, and coordinates results via channels. This proves the core value proposition: user-
controlled distributed computing with code mobility, coordinated by the Join Calculus.

See [vision.md § MVP](vision.md#mvp-distributed-vertical-slice) for the full rationale and
[roadmap.md § MVP Milestone](roadmap.md#mvp-milestone--distributed-vertical-slice) for the block
mapping.

The unified data pipelines story (replacing Transforms, enrich policies, etc.) remains valid as a
superset of the distributed vertical slice — it requires `writeTo` (Block E stretch goal),
`groupBy` combinator, and scheduled execution.

## Immediate Next Steps

Phases 0–2 and Block A are complete. The language supports concurrent multi-index queries via
`spawn`/`when`, typed functional composition over query results, and structured record output.
See [mvp.md](mvp.md) for concrete examples.

**Next on the MVP critical path** (distributed vertical slice):

1. **Block B** — ES topology as typed values. Inject `ClusterService`, implement `index_topology`
   builtin, design node/shard record types.
2. **Block C** — Cross-node code execution. `spawn!`, `send`, `Value` serialization, `CoreExpr`
   serialization, channel registry, transport handlers, remote evaluator. The hardest block —
   will be subdivided.
3. **Block D** — Local data access (`scan`). Lucene queries on data nodes inside shipped closures.

**High-impact small items** (can be addressed opportunistically alongside blocks):

- **Double/float arithmetic (D-020)** — extend `EvalPrimOps` to handle `Long` and `Double`.
  Blocks user-defined aggregates like p-series weighted sums.
- **Math functions** — `pow`, `sqrt`, etc. as primops or prelude built-ins.
- **Integration tests for spawn/when** — unit tests pass and manual testing confirms correctness,
  but Gradle integration tests for concurrent ESQL queries have not been added.

**Phase 1 tech debt** (opportunistic):

- Replace `resolveDeep` in `CorePrinter` with environment-based Rigid resolution (D-032).
- Switch `zonkOrKeep` to an `Optional`-returning `zonk` API (D-032).
- `MonoType` → `Type` with `Forall` variant (D-038).

See [roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt) for
the full consolidated list.

**Deferred work** (not on the vertical slice critical path):

- Multi-value channels (streaming patterns) — old Block B, deferred
- `writeTo` + scheduled execution — Block E stretch goal
- Typeclass-driven push-down (RawData → Lucene) — future optimization
- Exchange streaming (scale) — future, orchestrated explicitly by piescript
- Push-down to ESQL text — deprioritized (typeclass approach is more general)

Review:

- [mvp.md](mvp.md) for concrete examples of what piescript enables today
- [vision.md](vision.md) for the MVP goal and design philosophy
- [roadmap.md](roadmap.md) for the updated block breakdown and MVP milestone
- [decisions.md](decisions.md) for all architectural decisions (D-040 Join Calculus model, D-041
  Block A implementation, D-042 distributed execution model and block restructure)

**Ref**: [Phase 2 completion session](303bcf3e-9eef-4719-a47d-24c1ff27a675),
[Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef),
[Block A implementation](14bf4826-a39e-4012-ab4c-d73ad902a95f),
[Distributed execution discussion](14bf4826-a39e-4012-ab4c-d73ad902a95f)
