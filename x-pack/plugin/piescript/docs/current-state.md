# Current State

> **Living doc** — update after every implementation session. This is the ground truth for "what
> exists right now."
>
> **Last updated**: 2026-03-14 (Phase 1 wrap-up, Phase 2 opened)

## Summary

**Phase 0 is complete. Phase 1a (Parser) is complete. Phase 1b (Type System + Elaborator) is
complete. Phase 1c (Evaluator + Wiring) is complete. Phase 1d (Open Rows & Row Polymorphism)
is complete. D-035 (Core IR System F refactor) is complete.** The Core IR is now System F with
explicit unary `CoreTypeAbs` and `CoreTypeApp` nodes. The elaborator uses deferred constraint
solving: constraints are emitted during elaboration and solved incrementally at generalization
boundaries. `generalize` and `instantiateAndWrap` live in `Elaborator`;
`TypeWalker.walkType`, `.generalize`, and `.instantiate` have been deleted. The dev endpoint
now exposes `core_raw`, `constraints`, and `zonker` fields for debugging. 332 tests passing.

**Phase 1e (Pattern Matching) is deferred** — it is not blocking the MVP-critical path. Phase 2
(Index Resolution + Concrete-Row Constraints) is now the active phase. See
[roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt) for the
consolidated list of Phase 1 items carried forward.

## What Works

| Capability | Details |
|-----------|---------|
| REST endpoint | `POST /_piescript/eval` accepts `{"program": "..."}` |
| Dev endpoint | `POST /_piescript/dev` returns CST (`tree`), elaborated Core IR (`core`), raw Core IR (`core_raw`), constraints (`constraints`), zonker substitutions (`zonker`), resolved type (`type`), and evaluated result (`eval`). Parse errors return `parse_error`; type errors return `tree` + `type_error`; eval errors return `eval_error`. |
| ESQL passthrough | `query FROM idx \| WHERE x > 1 \| LIMIT 10;` executes as ESQL (fallback path) |
| Expression evaluation (Phase 1c) | Non-query programs go through parse → elaborate → evaluate pipeline, returning `{"type": "...", "result": ...}` |
| Request validation | Empty/blank programs rejected with 400 |
| Security | RBAC authorization via `shouldAuthorizeIndexActionNameOnly()`, operator privileges allowlist |
| Integration tests | 12 passing test cases covering query passthrough, expression evaluation, error handling |
| Build | Compiles, passes `check`, `spotlessJavaCheck`, `javaRestTest` |
| ANTLR grammar | Lexer (`PiescriptLexer.g4`) and parser (`PiescriptAntlrParser.g4`) implementing full D1.17 surface syntax |
| Parser entry point | `PiescriptParser.java` — invokes ANTLR, produces parse tree; `parseToTreeString()` for CST inspection |
| Core IR printer | `CorePrinter` in `piescript.core`: `printExpr` (zonked), `printExprRaw` (bare metas/rigids), `printConstraints`, `printZonker` — used by dev endpoint for debugging |
| Parser unit tests | `PiescriptParserTests.java` — comprehensive coverage of every syntax form plus error cases |
| Type data structures (Phase 1b) | `Kind`, `MonoType`, `RowType`, `TypeScheme`, `LitVal`, `Op` in `piescript.types` package |
| Core IR (Phase 1b + D-035) | `CoreExpr` sealed hierarchy in `piescript.core`: `CoreVar`, `CoreLit`, `CoreLam`, `CoreApp`, `CoreLet`, `CoreRecord`, `CoreProject`, `CoreUpdate`, `CorePrimOp`, `CoreTypeAbs`, `CoreTypeApp` — extends `Node<CoreExpr>` with `MonoType` on every node. `CoreTypeAbs` and `CoreTypeApp` are unary System F nodes emitted by the elaborator. |
| Type unit tests (Phase 1b) | `TypeDataStructureTests.java` — construction, equality, sealed hierarchy, factory methods |
| Core IR unit tests (Phase 1b) | `CoreExprTests.java` — construction, accessors, equality, replaceChildren, tree traversal |
| Elaboration context (Phase 1b) | Immutable `ElaborationContext` in `piescript.elab`: typing context (Γ) + binding level, passed by value through recursive descent. `lookup()` returns `Optional`. |
| Elaboration state (Phase 1b + D-035) | Mutable `ElaborationState` in `piescript.elab`: metavar supply + zonker with chain resolution + constraint accumulator. `emitConstraint(left, right, line, column)` for deferred constraints. `constraints()` returns the accumulated list. |
| Elaboration unit tests (Phase 1b) | `ElaborationContextTests.java` (immutability, de Bruijn indexing, shadowing, scope unwinding) + `ElaborationStateTests.java` (metas, zonker, integrated let-polymorphism workflow) |
| Unification (Phase 1b+1d) | `Unifier` in `piescript.elab`: Robinson unification with occurs check, null-as-bottom (D1.11), Leijen-style open-row unification (D-030) with tail solving. Returns `Optional<TypeError>`. |
| Type errors (Phase 1b) | `TypeError` sealed interface: `Mismatch`, `InfiniteType`, `FieldMismatch`, `MissingFields` |
| Unification unit tests (Phase 1b) | `UnifierTests.java` — meta solving, transitive chains, occurs check, null-as-bottom, arrow/record/app structural matching, cross-form mismatch |
| Elaborator (Phase 1b + D-035) | `Elaborator` in `piescript.elab`: pattern-matching recursive descent over ANTLR CST → Core IR. Bidirectional HM type inference with deferred constraint solving, `generalize` (metas → Rigids in zonker + `CoreTypeAbs`), `instantiateAndWrap` (Rigids → fresh metas + `CoreTypeApp`), primops as typed functions (concrete Integer-only signatures, D-020), desugaring (multi-param lambda, pipe, accessor, update sugar, blocks, top-level bindings). |
| Type walker (Phase 1b, reduced by D-035) | `TypeWalker` in `piescript.elab`: `resolveDeep` (used by `CorePrinter` for display) and `collectMetas` (used by `Elaborator.generalize`). `walkType`, `generalize`, and `instantiate` were deleted by D-035. |
| Elaboration exception (Phase 1b) | `ElaborationException`: unchecked, fail-fast, wraps source location + optional `TypeError`. |
| Elaborator tests (Phase 1b + D-035) | `ElaboratorTests.java` — tests covering: literals (int, long, decimal, string, boolean, null), let-bindings (basic, annotated, nested, shadowing, top-level), lambdas (identity, typed, multi-param), application (direct, inference), let-polymorphism, arithmetic/comparison/boolean operators, unary ops, records (empty, literal, projection, update, field addition), pipe operator, accessor sugar, update sugar, blocks, parentheses, ascription, de Bruijn indexing, error cases (unbound variable, type mismatch, non-function application, duplicate field, projection on non-record, missing field, unknown type, if/then/else unsupported, update on non-record), and deferred tests (occurs check, cross-type arithmetic, lambda type mismatch). Updated for D-035 (deferred constraints change some error messages). |
| Runtime values (Phase 1c) | `Value` sealed interface in `piescript.eval`: `IntegerVal`, `LongVal`, `DoubleVal`, `KeywordVal(String)`, `BooleanVal`, `NullVal`, `RecordVal`, `ClosureVal`. |
| Evaluator (Phase 1c + D-035) | `Evaluator` in `piescript.eval`: tree-walking de Bruijn environment machine, handles all 11 Core IR variants (including `CoreTypeAbs`/`CoreTypeApp` erasure), pattern-match dispatch for PrimOps, division-by-zero and null-in-arithmetic as `EvaluationException`. |
| PiescriptResponse (Phase 1c) | Wrapper response: expression results (`{"type": ..., "result": ...}`) or ESQL query delegation. Implements `ChunkedToXContentObject` and `Releasable`. |
| Transport pipeline (Phase 1c) | Dual-dispatch: `query ... ;` programs use ESQL passthrough, all other programs go through parse → elaborate → evaluate. |
| Evaluator tests (Phase 1c) | `EvaluatorTests.java` — 50 tests covering: literals, arithmetic, comparisons, booleans, let-bindings, lambdas, closures, records, pipes, blocks, accessor sugar, let-polymorphism, complex expressions, error cases (div-by-zero, null in arithmetic). |

## What Does Not Exist Yet

| Capability | Target Phase | Notes |
|-----------|-------------|-------|
| Pattern matching | 1e (deferred) | No match expressions (deferred — not blocking Phase 2+; see D-029) |
| Typed query results | 2 | `query` returns untyped ESQL passthrough, not `Stream (Record ρ)` |
| Stream runtime + plan graph | 3 | No `CoreProcess` IR, no plan graph, no stream combinators |
| `writeTo` sink primitive | 3 | No mechanism to write stream results to an index |
| `groupBy` combinator | 3 | No grouping/aggregation semantics |
| Push-down optimizer | 3 | No compilation of piescript transforms to ESQL expressions |
| ExpressionEvaluator compiler | 3 | No fast path — tree-walking evaluator only |
| `par` (parallel composition) | 4 | No concurrent multi-query support |
| Scheduled async execution | 4b | No persistent task or scheduler |
| Async query support | 3 | Only synchronous `syncEsqlQueryRequest` |
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

2. **`DIRECT_EXECUTOR_SERVICE`.** The transport action runs on the calling thread. Parse, elaborate,
   and evaluate all run synchronously. This is acceptable for Phase 1's expression-only workload but
   will need a dedicated thread pool when computation becomes heavier.

3. **`KeywordVal` uses `String`, not `BytesRef`.** The evaluator converts ESQL's `BytesRef` keywords
   to `String` at the `CoreLit` boundary (D-026). When ESQL query results flow into piescript
   expressions, the reverse conversion will be needed.

4. **Integer-only arithmetic.** PrimOps are typed as `Integer -> Integer -> Integer` (D-020).
   `Long` and `Double` literals exist but cannot participate in arithmetic operations.

5. **No backwards compatibility versioning.** `PiescriptRequest` and `PiescriptResponse` do not use
   `TransportVersion` checks. The response serialization is not exercised across nodes (same-node
   only via `DIRECT_EXECUTOR_SERVICE`).

## MVP North Star

The end goal for the foundations being built now is the **Unified Data Pipelines MVP**: a piescript
program that replaces the combination of ES Transforms, enrich policies, enrich processors, and
ingest pipeline chains with a single typed program — with distributed execution via push-down into
ESQL's engine. See [vision.md § MVP](vision.md#mvp-unified-data-pipelines) for the full rationale
and [roadmap.md § MVP Milestone](roadmap.md#mvp-milestone--unified-data-pipelines) for the phase
mapping.

The current Phase 1 foundation work (type system, Core IR, evaluator) feeds directly into this
goal: the type checker will verify field compatibility across entire pipelines, the System F Core IR
will be compiled to ESQL ExpressionEvaluators for vectorized execution, and the plan graph
architecture will enable the push-down optimizer to route computation to data nodes.

## Immediate Next Steps

Phase 1 (sub-phases 1a–1d + D-035) is complete. Phase 1e (Pattern Matching) is deferred — it is
not on the critical path for Phase 2+. **Phase 2 (Index Resolution + Concrete-Row Constraints)
is now the active phase.**

Phase 2 work:

1. **Index resolution pre-pass** — integrate with `IndexResolver` to resolve index mappings at
   elaboration time.
2. **`query` expression typing** — `query` returns `Stream (Record ρ)` where `ρ` is derived from
   the resolved index mapping, not an untyped ESQL passthrough.
3. **Concrete-row constraint processing** — detect cross-index field type conflicts at the
   field-access site.
4. **`map`/`filter` as built-in typed functions** — prelude functions that operate on streams
   (D-016). Paves the way for Phase 3's plan graph.
5. **DataType → TCon mapping table** — bridge between ESQL `DataType` and piescript type
   constructors.

Phase 1 tech debt that can be addressed opportunistically (not blocking Phase 2):

- Replace `resolveDeep` in `CorePrinter` with environment-based Rigid resolution (D-032).
- Switch `zonkOrKeep` to an `Optional`-returning `zonk` API (D-032).
- `MonoType` → `Type` with `Forall` variant (D-038) — needed eventually for expression-level
  polytype ascription; annotated-let path works without it.

See [roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt) for
the full consolidated list.

Review:

- [vision.md](vision.md) for the MVP goal and design philosophy
- [roadmap.md](roadmap.md) for the updated phase breakdown and MVP milestone
- [decisions.md](decisions.md) for all architectural decisions (especially D-035)
