# Current State

> **Living doc** — update after every implementation session. This is the ground truth for "what
> exists right now."
>
> **Last updated**: 2026-03-16 (session 10 — Phase 2 complete, thread pool fix, integration tests green)

## Summary

**Phase 0 is complete. Phase 1 (sub-phases 1a–1d + D-035) is complete. Phase 2 (Index Resolution +
Concrete-Row Constraints + Eager Evaluation) is complete.** The evaluator fires ESQL queries
synchronously, converts rows to `RecordVal`s via `EsqlValueConverter`, and returns `StreamVal`.
Built-in functions (`map`, `filter`, `reduce`) operate over materialized streams. The transport
action runs on `ThreadPool.Names.GENERIC` to avoid deadlocking transport/coordination threads
(D-004 revision, D-039). 15 integration tests and all unit tests passing.

**Phase 1e (Pattern Matching) is deferred** — it is not blocking the MVP-critical path. **Block A
(spawn + single-value join) is the next active phase.** The execution model has been redesigned
around the Join Calculus (D-040), replacing the old plan-graph architecture (Phases 3–5) with
Blocks A–E. See [roadmap.md](roadmap.md) for the new block structure and
[roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt) for the
consolidated list of Phase 1 items carried forward.

## What Works

| Capability | Details |
|-----------|---------|
| REST endpoint | `POST /_piescript/eval` accepts `{"program": "..."}` |
| Dev endpoint | `POST /_piescript/dev` returns CST (`tree`), elaborated Core IR (`core`), raw Core IR (`core_raw`), constraints (`constraints`), zonker substitutions (`zonker`), resolved type (`type`), and evaluated result (`eval`). Parse errors return `parse_error`; type errors return `tree` + `type_error`; eval errors return `eval_error`. |
| Query typing + evaluation (Phase 2) | `` query `FROM idx` `` type-checks against real index mappings via `IndexResolver`, producing `Stream { field: Type, ... }`. The evaluator fires `EsqlQueryAction` synchronously, converts response rows to `RecordVal`s via `EsqlValueConverter`, and returns a `StreamVal`. Built-in functions (`map`, `filter`, `reduce`) operate over materialized streams. |
| Expression evaluation (Phase 1c) | Non-query programs go through parse → elaborate → evaluate pipeline, returning `{"type": "...", "result": ...}` |
| Request validation | Empty/blank programs rejected with 400 |
| Security | RBAC authorization via `shouldAuthorizeIndexActionNameOnly()`, operator privileges allowlist |
| Integration tests | 15 passing test cases covering query type-checking, eager evaluation (stream, map, filter, reduce), expression evaluation, error handling |
| Build | Compiles, passes `check`, `spotlessJavaCheck`, `javaRestTest` |
| ANTLR grammar | Lexer (`PiescriptLexer.g4`) and parser (`PiescriptAntlrParser.g4`) implementing full D1.17 surface syntax |
| Parser entry point | `PiescriptParser.java` — invokes ANTLR, produces parse tree; `parseToTreeString()` for CST inspection |
| Core IR printer | `CorePrinter` in `piescript.core`: `printExpr` (zonked), `printExprRaw` (bare metas/rigids), `printConstraints`, `printZonker` — used by dev endpoint for debugging |
| Parser unit tests | `PiescriptParserTests.java` — comprehensive coverage of every syntax form plus error cases |
| Type data structures (Phase 1b) | `Kind`, `MonoType`, `RowType`, `TypeScheme`, `LitVal`, `Op` in `piescript.types` package |
| Core IR (Phase 1b + D-035 + Phase 2) | `CoreExpr` sealed hierarchy in `piescript.core`: `CoreVar`, `CoreFree`, `CoreLit`, `CoreLam`, `CoreApp`, `CoreLet`, `CoreRecord`, `CoreProject`, `CoreUpdate`, `CorePrimOp`, `CoreTypeAbs`, `CoreTypeApp`, `CoreQuery` — extends `Node<CoreExpr>` with `MonoType` on every node. `CoreTypeAbs` and `CoreTypeApp` are unary System F nodes. `CoreFree` is a module-level free variable (built-ins). `CoreQuery` carries an ESQL query string and its resolved stream type. |
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
| Elaborator tests (Phase 1b + D-035) | `ElaboratorTests.java` — tests covering: literals (int, long, decimal, string, boolean, null), let-bindings (basic, annotated, nested, shadowing, top-level), lambdas (identity, typed, multi-param), application (direct, inference), let-polymorphism, arithmetic/comparison/boolean operators, unary ops, records (empty, literal, projection, update, field addition), pipe operator, accessor sugar, update sugar, blocks, parentheses, ascription, de Bruijn indexing, error cases (unbound variable, type mismatch, non-function application, duplicate field, projection on non-record, missing field, unknown type, if/then/else unsupported, update on non-record), and deferred tests (occurs check, cross-type arithmetic, lambda type mismatch). Updated for D-035 (deferred constraints change some error messages). |
| Runtime values (Phase 1c + Phase 2) | `Value` sealed interface in `piescript.eval`: `IntegerVal`, `LongVal`, `DoubleVal`, `KeywordVal(String)`, `BooleanVal`, `NullVal`, `RecordVal`, `StreamVal(List<Value>)`, `ClosureVal`, `BuiltinVal(name, arity, partialArgs)`. `StreamVal` is the eagerly materialized stream representation. `BuiltinVal` supports curried partial application; fully saturated calls dispatch to `executeBuiltin`. |
| ESQL value converter (Phase 2) | `EsqlValueConverter` in `piescript.eval`: converts `EsqlQueryResponse` rows to `StreamVal`. Each row becomes a `RecordVal` (column names → field keys, cell values → field values via `instanceof` dispatch). Handles `Integer`, `Long`, `Double`, `String`, `Boolean`, `null`, multi-value fields (v0: first element only). |
| Evaluator (Phase 1c + D-035 + Phase 2) | `Evaluator` in `piescript.eval`: tree-walking de Bruijn environment machine, handles all Core IR variants. Takes optional `Client` for query execution. `CoreQuery` fires `EsqlQueryAction` synchronously and converts to `StreamVal` via `EsqlValueConverter`. `CoreFree` → `BuiltinVal`. `CoreApp` dispatches to closures or built-in partial application. Built-ins `map`/`filter`/`reduce` operate over `StreamVal` elements via `applyFunction` callback. PrimOps handle arithmetic (integer-only, D-020), comparison, and boolean operations. |
| PiescriptResponse (Phase 1c + Phase 2) | Wrapper response: expression results (`{"type": ..., "result": ...}`), including `StreamVal` serialized as JSON arrays. Implements `ChunkedToXContentObject` and `Releasable`. |
| Transport pipeline (Phase 2) | Unified pipeline: parse → index resolution pre-pass → elaborate → evaluate. Runs on `ThreadPool.Names.GENERIC` (D-004 revision). Resolve callbacks fork to GENERIC before evaluation to avoid blocking coordination threads. |
| Evaluator tests (Phase 1c + Phase 2) | `EvaluatorTests.java` — 59 tests covering: literals, arithmetic, comparisons, booleans, let-bindings, lambdas, closures, records, pipes, blocks, accessor sugar, let-polymorphism, complex expressions, error cases (div-by-zero, null in arithmetic), and stream built-ins (`map` projection/transform, `filter` predicate/keep-all/remove-all, `reduce` sum/empty, `map` empty stream, query-without-client error). |

## What Does Not Exist Yet

| Capability | Target Block | Notes |
|-----------|-------------|-------|
| Pattern matching | 1e (deferred) | No match expressions (deferred — not blocking Blocks A+; see D-029) |
| `spawn` / `when` (async coordination) | Block A | No concurrency primitives — queries execute synchronously, sequentially. Surface keyword is `when` (D-041). |
| `Channel τ` type + `SpawnVal` runtime value | Block A | No channel abstraction in type system or runtime |
| Uniformly async evaluator (CPS / ActionListener) | Block A | Evaluator is fully synchronous; will be refactored to uniformly async (D-041) |
| Multi-value channels | Block B | Block A channels are single-value only |
| `newchan` / `send` primitives | Block B | No explicit channel creation or message sending |
| `writeTo` sink primitive | Block C | No mechanism to write stream results to an index |
| `groupBy` combinator | Block C+ | No grouping/aggregation semantics |
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

2. **~~`DIRECT_EXECUTOR_SERVICE`~~ (resolved).** The transport action now runs on `ThreadPool.Names.GENERIC`,
   which avoids the deadlock that occurred when the evaluator blocked the transport thread with a
   synchronous `EsqlQueryAction` call. Parse, elaborate, and evaluate still run synchronously, but
   on a pooled thread that is safe to block.

3. **`KeywordVal` uses `String`, not `BytesRef`.** The evaluator converts ESQL's `BytesRef` keywords
   to `String` at the `CoreLit` boundary (D-026). When ESQL query results flow into piescript
   expressions, the reverse conversion will be needed.

4. **Integer-only arithmetic.** PrimOps are typed as `Integer -> Integer -> Integer` (D-020).
   `Long` and `Double` literals exist but cannot participate in arithmetic operations.

5. **No backwards compatibility versioning.** `PiescriptRequest` and `PiescriptResponse` do not use
   `TransportVersion` checks. The response serialization is not exercised across nodes.

## MVP North Star

The end goal for the foundations being built now is the **Unified Data Pipelines MVP**: a piescript
program that replaces the combination of ES Transforms, enrich policies, enrich processors, and
ingest pipeline chains with a single typed program — with concurrent query execution via `spawn` +
`when`. See [vision.md § MVP](vision.md#mvp-unified-data-pipelines) for the full rationale and
[roadmap.md § MVP Milestone](roadmap.md#mvp-milestone--unified-data-pipelines) for the block
mapping.

The current foundation work (type system, Core IR, evaluator) feeds directly into this goal: the
type checker will verify field compatibility across entire pipelines, the System F Core IR provides
the typed substrate for coordination primitives, and the Join Calculus model (`spawn`/`when`/
channels) provides the concurrency primitives for coordinating multiple queries. Push-down
compilation into ESQL (Block D) is a post-MVP optimization.

## Immediate Next Steps

Phases 0–2 are complete. Phase 1e (Pattern Matching) is deferred — it is not on the MVP critical
path. **Block A (`spawn` + single-value `when`) is the next active phase.**

Block A introduces the Join Calculus coordination primitives: `spawn` (async computation returning
a channel), `when` (synchronization on channels — keyword chosen to avoid SQL/ESQL JOIN collision,
see D-041), and `Channel τ` (typed channel). The evaluator becomes uniformly async: every `evaluate`
call takes an `ActionListener<Value>`, with pure expressions completing synchronously inline
(D-041). Implementation leverages `SubscribableListener<Value>` for channels and a positional
collector (`AtomicArray` + `CountDown`) for `when` synchronization.
See [roadmap.md § Block A](roadmap.md#block-a--spawn--single-value-when-async-coordination-memo)
and D-040, D-041 for the full design.

Phase 1 tech debt that can be addressed opportunistically:

- Replace `resolveDeep` in `CorePrinter` with environment-based Rigid resolution (D-032).
- Switch `zonkOrKeep` to an `Optional`-returning `zonk` API (D-032).
- `MonoType` → `Type` with `Forall` variant (D-038) — needed eventually for expression-level
  polytype ascription; annotated-let path works without it.

See [roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt) for
the full consolidated list.

Review:

- [vision.md](vision.md) for the MVP goal and design philosophy
- [roadmap.md](roadmap.md) for the updated block breakdown and MVP milestone
- [decisions.md](decisions.md) for all architectural decisions (D-040 for Join Calculus model, D-041 for Block A implementation decisions)

**Ref**: [Phase 2 completion session](303bcf3e-9eef-4719-a47d-24c1ff27a675),
[Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef)
