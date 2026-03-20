# Current State

> **Living doc** — update after every implementation session. This is the ground truth for "what
> exists right now."
>
> **Last updated**: 2026-03-20 (Block D: Local data access. `use` declarations, `Index r` type,
> `Shard.open`/`Shard.consume`/`Shard.read` primitives, qualified builtin names, `DocRef r` type
> threading, negative serialization tests for non-serializable values. Block D is now complete.)

## Summary

**Phase 0 is complete. Phase 1 (sub-phases 1a–1d + D-035) is complete. Phase 2 (Index Resolution +
Concrete-Row Constraints + Eager Evaluation) is complete. Block A (spawn + single-value when) is
complete. Block B (ES topology, `List` type rename, list utilities) is complete. Block C
sub-blocks C.1–C.5 (cross-node code execution + builder DSL + multi-node integration tests) are
complete. Block D (local data access via `use`, `Shard.open`, `Shard.consume`, `Shard.read`) is
complete.** Piescript can now access local Lucene data on data nodes via the `use`/`Shard.*`
primitives. `use "index-name" as idx` declares a typed `Index r` value whose row type `r` is
resolved from field capabilities at elaboration time. `Shard.open` acquires a `Searcher r` for a
shard, `Shard.consume` iterates `DocRef r` references, and `Shard.read` reads all doc-value fields
from a `DocRef r` returning a record of type `r`. Non-serializable types (`SearcherVal`, `DocRefVal`)
are enforced at the serialization boundary — attempting to send them across the wire fails with an
`IOException`. Block D also introduces qualified builtin names (`Shard.open`, `Index.routing`,
`List.map`, `Math.sqrt`, `Cluster.topology`) and the `Index r` type (carrying index name, UUID,
and field metadata). UUID is resolved at evaluation time from `ClusterService`, keeping Core IR
cluster-state-free.

**Phase 1e (Pattern Matching) is deferred** — not blocking the MVP-critical path. The execution
model is the Join Calculus (D-040), with `spawn`/`when`/`send`/channels as coordination primitives.
The roadmap has been restructured (D-042) around a distributed vertical slice: Block C (cross-node
execution), Block D (local data access via `scan`). See [roadmap.md](roadmap.md) for the block
structure, [mvp.md](mvp.md) for concrete examples of what piescript enables today, and
[roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt)
for Phase 1 items carried forward.

## What Works

| Capability | Details |
|-----------|---------|
| REST endpoint | `POST /_piescript/eval` accepts `{"program": "..."}` |
| Dev endpoint | `POST /_piescript/dev` returns CST (`tree`), elaborated Core IR (`core`), raw Core IR (`core_raw`), constraints (`constraints`), zonker substitutions (`zonker`), resolved type (`type`), and evaluated result (`eval`). Parse errors return `parse_error`; type errors return `tree` + `type_error`; eval errors return `eval_error`. |
| Query typing + evaluation (Phase 2) | `` query `FROM idx` `` type-checks against real index mappings via `IndexResolver`, producing `List { field: Type, ... }`. The evaluator fires `EsqlQueryAction` asynchronously via `ActionListener`, converts response rows to `RecordVal`s via `EsqlValueConverter`, and returns a `ListVal`. Built-in functions (`map`, `filter`, `reduce`) operate over materialized lists. |
| `spawn` / `when` (Block A) | `spawn <expr>` forks computation to the GENERIC thread pool, returning a `ChannelVal(nodeId, channelId)`. `when (ch1 x) & (ch2 y) -> body` synchronizes on one or more channels using a positional collector (`AtomicArray` + `CountDown`), binding channel results to variables in the body. Supports concurrent multi-index queries. `when` only works on local channels (remote channels are rejected at runtime — D-045). |
| `spawn!` (Block C) | Bare channel creation — `spawn!` creates a channel without executing a body. Returns `ChannelVal(localNodeId, channelId)`. The user completes the channel via explicit `send`. Subject to value restriction (D-046): `let ch = spawn!` stays monomorphic. |
| `send` (Block C) | `send <channel> <value>` delivers a value to a channel. Local channels: completes the `SubscribableListener` in the `ChannelRegistry`. Remote channels: serializes the value and sends a transport message to the owner node. Inbox channels always go through the transport layer. Fire-and-forget: returns `Null` immediately (D-047). |
| Cross-node closure evaluation (Block C) | Closures can be sent to remote nodes via the inbox mechanism. `send node.inbox (fn info -> ...)` serializes the closure over the wire, the target node evaluates it asynchronously with local node info as the argument. Transport response is fire-and-forget — evaluation errors stay on the target node (D-047). |
| Unified numeric type (`Double`) | All numbers in piescript are `Double` (IEEE 754 64-bit). Integer literals (`42`), decimal literals (`3.14`), and all ESQL numeric field types (`integer`, `long`, `double`, `float`, etc.) map to `Double`. Arithmetic, comparisons, and unary negation all operate on doubles. No numeric widening or coercion needed. `Integer`, `Long` still exist in `Value` for serialization but are widened to `DoubleVal` at the ESQL boundary. Whole-number doubles serialize as integers in JSON for clean output. Resolves D-020. |
| Math builtins | `abs`, `floor`, `ceil`, `round`, `sqrt`, `log` (`Double → Double`); `min`, `max`, `pow` (`Double → Double → Double`); `toInt` (`Double → Double`, truncates to integer). All backed by `java.lang.Math`. Curried, so partial application works: `let clamp = min 100 in clamp 150`. |
| Builder DSL (Block C.4) | `Exprs`, `Values`, and `Types` utility classes in `piescript.core`, `piescript.eval`, and `piescript.types` respectively. Static factory methods for concise construction: `lit(42)`, `lam("x", DOUBLE, body)`, `app(fn, arg)`, `rec(field(...))`, `doubleVal(n)`, `keyword(s)`, `record("k", v)`, `arrow(a, b)`, `list(t)`, `channel(t)`. Type inference where possible (e.g., `lam` computes arrow type, `rec` builds record type from fields). Reduces test and production verbosity. |
| `use` declaration (Block D) | `use "index-name" as idx` is a top-level declaration that binds a typed `Index r` value. The row type `r` is resolved from field capabilities via the `IndexResolutionPrePass` at elaboration time. Elaborates to `CoreLet` with `LitVal.IndexLit`. |
| `Index r` type (Block D) | `Index r` carries index name, UUID, and field metadata. UUID is resolved at evaluation time from `ClusterService` (not stored in Core IR). `r` is the row type derived from field capabilities. `Index.routing`, `Index.shards`, `Index.nodes` all accept `Index r` (not raw strings). |
| `Shard.open` / `Shard.consume` / `Shard.read` (Block D) | `Shard.open : Index r → ShardRecord → { match_all: Boolean } → Channel (Searcher r)` acquires a Lucene `IndexSearcher` asynchronously and delivers it via a channel. `Shard.consume : Double → Searcher r → List (DocRef r)` iterates `DocIdSetIterator` for up to N docs (synchronous). `Shard.read : DocRef r → r` reads all doc-value fields from a document reference (synchronous, throws on missing doc values). |
| `Searcher r` / `DocRef r` types (Block D) | Opaque, non-serializable node-local types. `Searcher r` wraps Lucene `IndexSearcher` + `SearcherState`. `DocRef r` wraps a Lucene doc ID + `SearcherState` reference. Both parameterized by row type `r` for static type safety. Serialization of these types throws `IOException`. |
| Qualified builtin names (Block D) | All builtins use qualified names: `Shard.open`, `Index.routing`, `List.map`, `Math.sqrt`, `Cluster.topology`, etc. Parser supports dotted identifiers for builtin dispatch. |
| `EvalTopology` (Block B + Block C) | Implements the `topology` builtin. Reads `ClusterState` → `RoutingTable` → `IndexRoutingTable` → `ShardRouting` → `DiscoveryNode` and converts to typed `RecordVal`/`ListVal` records. Node records include an `inbox` field (`ChannelVal(nodeId, "inbox")`) for sending closures to remote nodes. Shard records include `uuid` field. |
| `Channel τ` type + `ChannelVal` (Block A + Block C) | `Channel` is a type constructor (`AppType(TCon("Channel"), tau)`). `ChannelVal(nodeId, channelId)` is a serializable channel reference (D-045). The actual `SubscribableListener<Value>` lives in the per-node `ChannelRegistry`. |
| Serialization (Block C + Block D) | Full-fidelity serialization for 12 serializable `Value` variants (`ValueSerialization`), all 16 `CoreExpr` variants (`CoreExprSerialization`), and all `MonoType`, `RowType`, `LitVal`, `Op`, `Kind` types (`TypeSerialization`). `ClosureVal` serializes its `CoreExpr` body and `Value[]` environment recursively. `BuiltinVal` serializes name, arity, and partial args. `IndexVal` serializes name, UUID, and field type map. `SearcherVal` and `DocRefVal` explicitly throw `IOException` on serialization attempt. `LitVal.IndexLit` serializes name and field types (no UUID — resolved at eval time). 58 round-trip tests. |
| Transport layer (Block C) | `PiescriptSendAction` (`indices:data/read/piescript/send`), `PiescriptSendRequest` (channelId + serialized value), `TransportPiescriptSendAction` (handler with inbox and regular channel dispatch). `ChannelRegistry` is a singleton per node, shared across transport actions via Guice injection. |
| `topology` builtin (Block B) | `topology "index-name"` returns a record with both shard-centric and node-centric views of the cluster topology. Shards include `index`, `shard_id`, `primary`, `state`, and nested `node` record. Nodes include `id`, `name`, `address`, and nested `shards` list. Only STARTED shards are included. Exact index name only (no wildcards). See D-044. |
| `List` type (Block B, renamed from `Stream`) | `List` is the type constructor for in-memory lists (`TCon("List")`). `ListVal(List<Value>)` is the runtime representation. Renamed from `Stream`/`StreamVal` (D-043) to accurately reflect finite, eager, in-memory semantics. "Stream" reserved for future lazy/Exchange-backed streaming. |
| List utility builtins (Block B) | `head : List a → a`, `tail : List a → List a`, `length : List a → Double`, `isEmpty : List a → Boolean`. Operate on `ListVal.elements()`. Essential for working with topology results and other list values. |
| `EvalDependencies` context (Block B + Block C) | The evaluator takes an `EvalDependencies` record bundling `Client`, `Executor`, `ClusterService`, `TransportService`, `ChannelRegistry`, and `localNodeId`. See D-044, D-045. |
| Uniformly async evaluator (Block A) | Every `evaluate` call takes an `ActionListener<Value>`. Pure expressions fire callbacks synchronously inline (zero overhead). Coordination primitives (`spawn`, `when`, `query`) are truly async. No separate sync/async code paths (D-041). |
| Expression evaluation (Phase 1c) | Non-query programs go through parse → elaborate → evaluate pipeline, returning `{"type": "...", "result": ...}` |
| Request validation | Empty/blank programs rejected with 400 |
| Security | RBAC authorization via `shouldAuthorizeIndexActionNameOnly()`, operator privileges allowlist |
| Integration tests | 26 single-node tests (`PiescriptIT`) + 11 multi-node tests (`PiescriptMultiNodeIT`) covering query type-checking, eager evaluation, expression evaluation, topology, list utilities, error handling, cross-node execution, `use` declarations, shard data access, and non-serializable value wire rejection |
| Build | Compiles, passes `check`, `spotlessJavaCheck`, `javaRestTest` |
| ANTLR grammar | Lexer (`PiescriptLexer.g4`) and parser (`PiescriptAntlrParser.g4`) implementing full D1.17 surface syntax plus `SPAWN`, `WHEN`, `AMP` tokens and `SpawnExpr`/`WhenExpr` rules (Block A) |
| Parser entry point | `PiescriptParser.java` — invokes ANTLR, produces parse tree; `parseToTreeString()` for CST inspection |
| Core IR printer | `CorePrinter` in `piescript.core`: `printExpr` (zonked), `printExprRaw` (bare metas/rigids), `printConstraints`, `printZonker` — used by dev endpoint for debugging |
| Parser unit tests | `PiescriptParserTests.java` — comprehensive coverage of every syntax form plus error cases |
| Type data structures (Phase 1b) | `Kind`, `MonoType`, `RowType`, `TypeScheme`, `LitVal`, `Op` in `piescript.types` package |
| Core IR (Phase 1b + D-035 + Phase 2 + Block A + Block C) | `CoreExpr` sealed hierarchy in `piescript.core`: `CoreVar`, `CoreFree`, `CoreLit`, `CoreLam`, `CoreApp`, `CoreLet`, `CoreRecord`, `CoreProject`, `CoreUpdate`, `CorePrimOp`, `CoreTypeAbs`, `CoreTypeApp`, `CoreQuery`, `CoreSpawn`, `CoreWhen`, `CoreSend` — extends `Node<CoreExpr>` with `MonoType` on every node. `CoreTypeAbs` and `CoreTypeApp` are unary System F nodes. `CoreFree` is a module-level free variable (built-ins). `CoreQuery` carries an ESQL query string and its resolved list type. `CoreSpawn` wraps a `@Nullable` body expression (type `Channel bodyType`); null body represents bare `spawn!` channel creation. `CoreSend` takes a channel expression and a value expression (type `Null`). `CoreWhen` carries a list of `WhenBinding(CoreExpr channel, @Nullable String debugName)` and a body. |
| Type unit tests (Phase 1b) | `TypeDataStructureTests.java` — construction, equality, sealed hierarchy, factory methods |
| Core IR unit tests (Phase 1b) | `CoreExprTests.java` — construction, accessors, equality, replaceChildren, tree traversal |
| Elaboration context (Phase 1b + Phase 2) | Immutable `ElaborationContext` in `piescript.elab`: typing context (Γ) with de Bruijn-indexed local bindings + module-level free variable map + binding level, passed by value through recursive descent. `lookup()` checks local bindings first; `lookupModule()` falls back to the module map. Local variables shadow module-level names. |
| Elaboration state (Phase 1b + D-035) | Mutable `ElaborationState` in `piescript.elab`: metavar supply + zonker with chain resolution + constraint accumulator. `emitConstraint(left, right, line, column)` for deferred constraints. `constraints()` returns the accumulated list. |
| Elaboration unit tests (Phase 1b) | `ElaborationContextTests.java` (immutability, de Bruijn indexing, shadowing, scope unwinding) + `ElaborationStateTests.java` (metas, zonker, integrated let-polymorphism workflow) |
| Unification (Phase 1b+1d) | `Unifier` in `piescript.elab`: Robinson unification with occurs check, null-as-bottom (D1.11), Leijen-style open-row unification (D-030) with tail solving. Returns `Optional<TypeError>`. |
| Type errors (Phase 1b) | `TypeError` sealed interface: `Mismatch`, `InfiniteType`, `FieldMismatch`, `MissingFields` |
| Unification unit tests (Phase 1b) | `UnifierTests.java` — meta solving, transitive chains, occurs check, null-as-bottom, arrow/record/app structural matching, cross-form mismatch |
| Elaborator (Phase 1b + D-035 + Phase 2 + Block C) | `Elaborator` in `piescript.elab`: pattern-matching recursive descent over ANTLR CST → Core IR. Bidirectional HM type inference with deferred constraint solving, `generalize` (metas → Rigids in zonker + `CoreTypeAbs`) guarded by **value restriction** (D-046: only syntactic values generalize; side-effecting expressions like `spawn!`, `send`, applications stay monomorphic), `instantiateAndWrap` (parameterized by `Function<MonoType, CoreExpr>` factory — produces `CoreVar` for local bindings, `CoreFree` for module-level free variables), primops as typed functions (Double signatures — D-020 resolved), desugaring (multi-param lambda, pipe, accessor, update sugar, blocks, top-level bindings). Two-tier variable lookup: local de Bruijn bindings then module-level free variables. `Spawns.spawnBang()` elaborates bare `spawn!` (null-body `CoreSpawn`). `Sends.send()` elaborates `send` with channel/value type alignment. |
| Prelude (Phase 2 + Block B + math builtins + Block D) | `Prelude` in `piescript.elab`: defines the module map of built-in function type schemes. All builtins use qualified names: `List.map`, `List.filter`, `List.reduce`, `List.head`, `List.tail`, `List.length`, `List.isEmpty`, `Cluster.topology`, `Index.routing`, `Index.shards`, `Index.nodes`, `Math.abs`, `Math.floor`, `Math.ceil`, `Math.round`, `Math.sqrt`, `Math.log`, `Math.min`, `Math.max`, `Math.pow`, `Math.toInt`, `Shard.open`, `Shard.consume`, `Shard.read`. Type schemes use pre-allocated Rigid IDs (negative, disjoint from `ElaborationState.freshRigid`). Wired into `ElaborationContext.withModule(Prelude.MODULE)` at elaboration start. |
| Type walker (Phase 1b, reduced by D-035) | `TypeWalker` in `piescript.elab`: `resolveDeep` (used by `CorePrinter` for display and test assertions) and `collectMetas` (used by `Elaborator.generalize`). `walkType`, `generalize`, and `instantiate` were deleted by D-035. |
| Elaboration exception (Phase 1b) | `ElaborationException`: unchecked, fail-fast, wraps source location + optional `TypeError`. |
| Elaborator tests (Phase 1b + D-035 + Block A) | `ElaboratorTests.java` — 126 tests covering all Phase 1b/D-035 tests plus `spawn`/`when` type inference (channel type production, unwrapping, multi-binding scenarios, type errors for non-channel `when` bindings). |
| Runtime values (Phase 1c + Phase 2 + Block A + Block B + Block C + Block D) | `Value` sealed interface in `piescript.eval`: `IntegerVal`, `LongVal`, `DoubleVal`, `KeywordVal(String)`, `BooleanVal`, `NullVal`, `RecordVal`, `ListVal(List<Value>)`, `ClosureVal`, `BuiltinVal(name, arity, partialArgs)`, `ChannelVal(nodeId, channelId)`, `IndexVal(name, uuid, fieldTypes)`, `SearcherVal(SearcherState)`, `DocRefVal(docId, SearcherState)`. `ListVal` is the eagerly materialized list representation (renamed from `StreamVal` in Block B — D-043). `BuiltinVal` supports curried partial application. `ChannelVal` is a serializable channel reference (Block C — D-045). `IndexVal` is serializable (carries index metadata). `SearcherVal` and `DocRefVal` are **non-serializable** — they hold node-local Lucene state. Attempting to serialize them throws `IOException`. |
| ESQL value converter (Phase 2) | `EsqlValueConverter` in `piescript.eval`: converts `EsqlQueryResponse` rows to `ListVal`. Each row becomes a `RecordVal` (column names → field keys, cell values → field values via `instanceof` dispatch). Handles `Integer`, `Long`, `Double`, `String`, `Boolean`, `null`, multi-value fields (v0: first element only). |
| Evaluator (Phase 1c + D-035 + Phase 2 + Block A + Block B + Block C + Block D) | Uniformly async tree-walking de Bruijn environment machine, split across seven classes: `Evaluator` (core dispatch + `CoreExpr` cases), `EvalPrimOps` (arithmetic, comparison, boolean ops), `EvalBuiltins` (list processing — `map`/`filter`/`reduce`/`head`/`tail`/`length`/`isEmpty` via `SubscribableListener` chaining), `EvalCoordination` (`when` evaluation via `PositionalCollector`), `EvalTopology` (`topology` builtin implementation), `EvalShard` (`Shard.open`/`Shard.consume`/`Shard.read` implementations), `EvalDependencies` (bundling `Client`, `Executor`, `ClusterService`, `TransportService`, `IndicesService`, `ChannelRegistry`, `localNodeId`). `CoreQuery` fires `EsqlQueryAction` asynchronously. `CoreSpawn` registers a `SubscribableListener` in the `ChannelRegistry`, optionally forks body evaluation, returns `ChannelVal(nodeId, channelId)`. `CoreSend` evaluates channel and value, completes the channel in the registry (local dispatch; remote dispatch stubbed for C.3). `CoreWhen` uses positional collector to synchronize channels via registry lookup and extend the de Bruijn environment. `LitVal.IndexLit` resolves UUID from `ClusterService` at evaluation time. |
| PiescriptResponse (Phase 1c + Phase 2 + Block B) | Wrapper response: expression results (`{"type": ..., "result": ...}`), including `ListVal` serialized as JSON arrays. Implements `ChunkedToXContentObject` and `Releasable`. |
| Transport pipeline (Phase 2 + Block A) | Unified async pipeline: parse → index resolution pre-pass → elaborate → evaluate (via `ActionListener`). Runs on `ThreadPool.Names.GENERIC` (D-004 revision). The evaluator completes the transport `ActionListener` when done — including after async `spawn`/`when` resolution. |
| Evaluator tests (Phase 1c + Phase 2 + Block A + math builtins) | `EvaluatorTests.java` — 115 tests covering: all Phase 1c/Phase 2 tests plus `spawn`/`when` semantics, math builtins (`abs`, `floor`, `ceil`, `round`, `sqrt`, `log`, `min`, `max`, `pow`, `toInt`), async evaluation with real thread pools, and deterministic tests with `DIRECT_EXECUTOR_SERVICE`. |

## What Does Not Exist Yet

| Capability | Target Block | Notes |
|-----------|-------------|-------|
| Pattern matching | 1e (deferred) | No match expressions (deferred — not blocking Blocks A+; see D-029) |
| `Label` kind / type-level singletons | Tech debt | `DocRef r` and `Searcher r` use `Kind.TYPE` for `r` instead of `Kind.ROW`. Proper fix requires `Label` kind + `Project` type family. See D-050 § Future. |
| `RowType` as first-class `MonoType` | Tech debt (high priority) | Rows are separate from `MonoType`; `r` in `DocRef r` is unconstrained. Should be `Kind.ROW` with row-kinded metas/rigids. |
| `sort` / `take` combinators | Block D+ | No sorting or top-N selection within piescript. Must push into ESQL. |
| `groupBy` combinator | Block D+ | No grouping/aggregation semantics within piescript. Must push into ESQL. |
| Multi-value channels | Deferred | Block A/C channels are single-value only |
| String / list concat operators | Phase 1 tech debt | No `<>` (string concat) or `++` (list concat). See roadmap. |
| Wildcard / alias / data stream patterns in `topology` | Deferred | `topology` accepts exact index name only (D-044) |
| Multi-project support in `topology` | Deferred | Uses `ProjectId.DEFAULT` (D-044) |
| Non-STARTED shard states in `topology` | Deferred | Only STARTED shards included (D-044) |
| `writeTo` sink primitive | Block E | No mechanism to write results to an index |
| Scheduled async execution | Block E+ | No persistent task or scheduler |
| Push-down optimizer | Deferred | Typeclass-driven push-down to Lucene (future optimization) |
| Exchange integration | Deferred | No streaming data flow via ESQL's compute engine |
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

5. **`RowType` is not a first-class `MonoType` (D-050 deviation, high priority).** Row types are
   modeled separately from `MonoType`, so the type parameter `r` in `DocRef r` and `Searcher r`
   has `Kind.TYPE` instead of `Kind.ROW`. This means `DocRef Double` is well-kinded from the
   type system's perspective, which is unsound. The fix is to make `RowType` a `MonoType` variant
   with `Kind.ROW`, add row-kinded metas and rigids, and constrain `r` properly.

6. **No `Label` kind for type-safe field projection (D-050 future work).** `Shard.read` returns
   the full record `r` (wildcard read). Type-safe single-field projection (e.g.,
   `Shard.read "name" ref`) requires a `Label` kind with type-level string singletons and a
   `Project` type family. Documented as future work.

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

6. ~~**Integer-only arithmetic.**~~ **Resolved.** All numbers are now `Double`. Integer literals,
   ESQL numeric fields, arithmetic, and comparisons all use `Double`. Math builtins (`abs`, `floor`,
   `ceil`, `round`, `sqrt`, `log`, `min`, `max`, `pow`, `toInt`) are available in the Prelude.

7. **ES conventions tech debt.** Several items related to Elasticsearch plugin conventions and
   production readiness: no `TransportVersion` guards on serialization, no logging, older
   `ActionListener.wrap()` patterns, `ActionType` name scope review needed, and more. See
   [roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt)
   for the full list.

8. **Stack depth risk in evaluator and list combinators.** The evaluator is recursive with no
   trampoline. Pure expressions resolve synchronously via `delegateFailureAndWrap`, building real
   JVM stack depth. Deeply nested `let`-chains and `map`/`filter` over large ESQL result sets
   (thousands of rows) will hit `StackOverflowError`. The `SubscribableListener` chain in
   `EvalBuiltins` is O(n) upfront allocation and O(n) stack for synchronous completion (the code
   already notes this). Relevant to the MVP vertical slice since `map`/`filter` over query results
   is the primary use case. Consider a `ThrottledIterator`-style iterative rewrite or explicit
   trampoline before the vertical slice targets large result sets.

9. **Inconsistent `Map` implementation for `RecordVal` fields.** Some record construction sites use
   `Map.of()` (e.g., `TransportPiescriptSendAction.buildLocalNodeInfo`, `EvalTopology.resolveClusterTopology`)
   while others use `LinkedHashMap` (e.g., `EsqlValueConverter`, `evaluateRecord`, `CoreUpdate`).
   `Map.of()` has no guaranteed iteration order, so field ordering in topology/node-info records is
   nondeterministic across JVM runs. This causes inconsistent serialization behavior and could
   affect debugging output. Fix: use `LinkedHashMap` everywhere records are constructed.

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

Phases 0–2, Block A, Block B, Block C (C.1–C.5), and Block D are complete. The language supports
concurrent multi-index queries via `spawn`/`when`, cross-node code execution via `send`/inbox,
typed functional composition over query results, structured record output, cluster topology
discovery via `Cluster.topology`, list utilities (`List.head`/`List.tail`/`List.length`/
`List.isEmpty`), math builtins (`Math.abs`/`Math.floor`/`Math.ceil`/`Math.round`/`Math.sqrt`/
`Math.log`/`Math.min`/`Math.max`/`Math.pow`/`Math.toInt`), concise IR construction via the
`Exprs`/`Values`/`Types` builder DSL, all numbers unified to `Double`, and local data access via
`use`/`Shard.open`/`Shard.consume`/`Shard.read`. Multi-node integration tests
(`PiescriptMultiNodeIT`) prove cross-node execution and non-serializable value rejection on a
3-node cluster. See [mvp.md](mvp.md) for concrete examples.

**The MVP vertical slice is now feature-complete.** All blocks (A through D) required for the
distributed vertical slice are implemented. A piescript program can discover topology, ship
closures to data nodes, open Lucene searchers on shards, iterate and read documents, and
coordinate results back to the coordinator via channels.

**Next on the roadmap:**

1. **Block E** (stretch goal) — `writeTo` sink. Persist results to an index via Bulk API.
2. **High-priority tech debt** — `RowType` as first-class `MonoType` (see deviations §5).
3. **Data access architecture** — `Query a` typeclass with ESQL/ShardPlan/List instances.

**Phase 1 tech debt** (opportunistic):

- Replace `resolveDeep` in `CorePrinter` with environment-based Rigid resolution (D-032).
- Switch `zonkOrKeep` to an `Optional`-returning `zonk` API (D-032).
- `MonoType` → `Type` with `Forall` variant (D-038).
- `RowType` → `MonoType` variant with `Kind.ROW` (D-050 deviation).
- `Label` kind + `Project` type family for type-safe field projection (D-050 future).

See [roadmap.md § Phase 1 Outstanding Tech Debt](roadmap.md#phase-1--outstanding-tech-debt) for
the full consolidated list.

**Deferred work** (not on the vertical slice critical path):

- Multi-value channels (streaming patterns) — old Block B, deferred
- `writeTo` + scheduled execution — Block E stretch goal
- Typeclass-driven push-down (RawData → Lucene) — future optimization
- Exchange streaming (scale) — future, orchestrated explicitly by piescript
- Push-down to ESQL text — deprioritized (typeclass approach is more general)
- Wildcard / alias / data stream patterns in `topology` (D-044)
- Multi-project support in `topology` (`ProjectId.DEFAULT` used) (D-044)
- Non-STARTED shard states in `topology` (D-044)

Review:

- [mvp.md](mvp.md) for concrete examples of what piescript enables today
- [vision.md](vision.md) for the MVP goal and design philosophy
- [roadmap.md](roadmap.md) for the updated block breakdown and MVP milestone
- [decisions.md](decisions.md) for all architectural decisions (D-040 through D-050)

**Ref**: [Phase 2 completion session](303bcf3e-9eef-4719-a47d-24c1ff27a675),
[Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef),
[Block A implementation](14bf4826-a39e-4012-ab4c-d73ad902a95f),
[Distributed execution discussion](14bf4826-a39e-4012-ab4c-d73ad902a95f),
Block B implementation session,
[Block C.4/C.5, numeric unification, math builtins](c7b160cb-0062-4a7e-a930-c0ec2437d7ee),
[Block D implementation](a10ee773-3d32-4a32-ad8c-cb4bb9a1f9d1)
