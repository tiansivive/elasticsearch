# Roadmap

> **Living doc** — update status markers as work progresses. Add new phases/sub-phases as they are
> planned.
>
> **Revised**: 2026-03-17. Blocks B–E restructured around the distributed vertical slice (D-042).
> Block B is now ES topology; Block C is cross-node execution; Block D is local data access;
> Block E is `writeTo` (stretch goal). Old block definitions (multi-value channels, `writeTo` +
> scheduler, push-down compilation, Exchange integration) are deferred. See D-042 for rationale.
>
> Previous revision (2026-03-16): Phases 3–5 replaced by Blocks A–E based on Join Calculus (D-040).
> The pre-join-calculus roadmap is archived in `docs/archive/roadmap.pre-join-calculus.md`.

**Overall design**: [scripting language design](../../.cursor/plans/scripting_language_design_9286506e.plan.md)

## Status Legend

| Marker | Meaning |
|--------|---------|
| :white_check_mark: | Complete |
| :construction: | In progress |
| :memo: | Planned (design exists) |
| :thought_balloon: | Aspirational (no detailed design yet) |

---

## MVP Milestone — Distributed Vertical Slice

> See [vision.md § MVP](vision.md#mvp-distributed-vertical-slice) for the full rationale.
> See [mvp.md](mvp.md) for concrete examples of what piescript enables today and what's aspirational.
>
> **Revised**: 2026-03-17. The MVP target has shifted from "unified data pipelines" to
> "distributed computing vertical slice" — proving that piescript can discover topology, ship code
> to remote nodes, access local data, and coordinate results via channels. See D-042.

The MVP target is a piescript program that demonstrates **explicit distributed computation**: the
user discovers cluster topology, creates channels, ships closures to data nodes, the data nodes
execute Lucene queries locally, send results back to coordinator-owned channels, and the
coordinator processes the results. This proves the core value proposition: user-controlled
distributed computing with code mobility, coordinated by the Join Calculus.

**MVP scope** — the following must be complete:

| Block | What it contributes to the MVP | Status |
|-------|-------------------------------|--------|
| Phase 2 | Index resolution — typed query results, field-level type checking, eager evaluation | :white_check_mark: |
| Block A | `spawn` + single-value `when` — local async coordination | :white_check_mark: |
| Block B | ES topology as typed values — `topology`, node/shard records, `List` type rename, list utilities | :white_check_mark: |
| Block C | Cross-node code execution — `send`, `spawn!`, closure serialization, channel registry | :memo: |
| Block D | Local data access — `scan` on data nodes inside shipped closures | :memo: |

**Stretch goal** (valuable but not required for the vertical slice):

| Block | What it adds | Status |
|-------|-------------|--------|
| Block E | `writeTo` — persist results to an index (Bulk API). Transform replacement story. | :memo: |

**Post-MVP enhancements**:

- Multi-value channels (streaming patterns, fold-as-join)
- Scheduled execution (persistent tasks)
- Typeclasses + RawData → Lucene push-down (principled optimization via type system)
- Exchange streaming (scale via compute engine, orchestrated explicitly by piescript)
- Push-down to ESQL text (deprioritized — typeclass approach is more general)
- Phase 6: QTT multiplicities, session types
- Phase 7: Module system (stored programs with imports)
- Phase 8: IDE tooling

---

## Phase 0 — Plugin Scaffold :white_check_mark:

Minimal viable plugin that passes ESQL queries through to the ESQL engine via a new REST endpoint.
Establishes the plugin skeleton, build configuration, security integration, and test infrastructure.

| Task | Status |
|------|--------|
| Plugin registration (`PiescriptPlugin`) | :white_check_mark: |
| REST endpoint `POST /_piescript/eval` | :white_check_mark: |
| Transport action with ESQL delegation | :white_check_mark: |
| Request validation (`query ... ;` wrapper) | :white_check_mark: |
| Security integration (RBAC, operator privileges) | :white_check_mark: |
| Java REST integration tests (6 cases) | :white_check_mark: |
| README with build/test/manual-testing instructions | :white_check_mark: |

**Ref**: [Phase 0 plan](../../.cursor/plans/phase0_plugin_scaffold.plan.md), [scaffold discussion](b0ac3e4f-db5e-4a03-85ec-a3016912512c)

---

## Phase 1 — Expression Language :construction:

Core functional expression language with type inference. Subdivided into sub-phases:

### Phase 1a — Parser :white_check_mark:

Lexer and parser that produce a CST from piescript source text.

| Task | Status |
|------|--------|
| ANTLR grammar (lexer + parser) | :white_check_mark: |
| Full surface syntax per D1.17 (literals, let, lambda, application, operators, records, projections, updates, if/then/else, blocks, pipes, types, comments) | :white_check_mark: |
| Parser entry point (`PiescriptParser.java`) | :white_check_mark: |
| Parser unit tests (`PiescriptParserTests.java` — every syntax form + error cases) | :white_check_mark: |
| Dev endpoint `POST /_piescript/dev` (CST inspection) | :white_check_mark: |
| Error reporting with source locations | :white_check_mark: |

**Ref**: [Phase 1 plan](../../.cursor/plans/phase1_expression_language.plan.md), [parser implementation](3cd2a822-792c-4179-a00e-0ba98b875f52)

### Phase 1b — Type Checker :white_check_mark:

Bidirectional Hindley-Milner type inference with zonker-based elaboration.

| Task | Status |
|------|--------|
| Type data structures (`Kind`, `MonoType`, `RowType`, `TypeScheme`, `LitVal`, `Op`) | :white_check_mark: |
| Core IR node types (`CoreExpr` sealed hierarchy extending `Node`) | :white_check_mark: |
| Elaboration state (context, metavar supply, binding level, zonker) | :white_check_mark: |
| Unification (Robinson, occurs check, null-as-bottom) | :white_check_mark: |
| Bidirectional elaborator (infer mode + desugaring) | :white_check_mark: |
| Bidirectional elaborator (check mode — D-036) | :memo: |
| `MonoType` → `Type` with `Forall` variant (D-038) | :construction: |
| Environment-carrying instantiation (D-037) | :bulb: |
| Let-generalization (binding-level-based) | :white_check_mark: |
| De Bruijn index representation | :white_check_mark: |
| Null semantics (v0: Null unifies with Any) | :white_check_mark: |
| Elaborator tests | :white_check_mark: |
| Dev endpoint wired to elaborator (`CorePrinter`, tree + core + type) | :white_check_mark: |

**Ref**: [Phase 1 plan](../../.cursor/plans/phase1_expression_language.plan.md), [1b implementation](36ef4cb3-4c3b-439e-a83a-aae069ca551c),
[System F Core IR](8f5cc3a8-4c26-4f71-8fb0-1ea3c17f527b),
[bidirectional elaborator](3308f68e-e239-4a60-912c-47cfba6eabcc),
[bidir refinements & D-038](303bcf3e-9eef-4719-a47d-24c1ff27a675)

### Phase 1c — Evaluator + Wiring :white_check_mark:

Tree-walking interpreter and end-to-end pipeline integration.

| Task | Status |
|------|--------|
| Runtime value types (`Value` sealed interface, `EvaluationException`) | :white_check_mark: |
| Tree-walking evaluator (de Bruijn environment machine) | :white_check_mark: |
| `PiescriptResponse` (expression result + ESQL query wrapper) | :white_check_mark: |
| Dual-dispatch transport action (query passthrough + expression pipeline) | :white_check_mark: |
| Evaluator unit tests (50 tests) | :white_check_mark: |
| Integration tests (expression eval + query passthrough, 12 tests) | :white_check_mark: |
| Dev endpoint with eval stage | :white_check_mark: |
| Deferred elaborator tests (occurs check, cross-type arithmetic, lambda mismatch) | :white_check_mark: |

**Ref**: [Phase 1 plan](../../.cursor/plans/phase1_expression_language.plan.md)

### Phase 1d — Open Rows & Row Polymorphism :white_check_mark:

Open-row unification, row polymorphism, rigid type variables, and type annotation elaboration.
Supersedes D-021 (closed-row accessor/update sugar). Pulled forward from Phase 2 because row
polymorphism is foundational for record-handling expressiveness — without it, `.x` only works
on records with exactly one field. See D-028–D-034 for design decisions.

| Task | Status |
|------|--------|
| Add `MonoType.Rigid(int id, Kind kind)` variant (D-031) | :white_check_mark: |
| `TypeScheme`: change `quantified` from `Set<Integer>` to `Map<Integer, Kind>` | :white_check_mark: |
| ANTLR lexer split: `UPPER_IDENT` / `LOWER_IDENT` (D-033) | :white_check_mark: |
| `ElaborationState`: `zonkOrKeep` + `resolveRow` (D-032 partial — see deviations below) | :white_check_mark: |
| Open-row unification in `Unifier` (Leijen-style, D-030) + Rigid handling | :white_check_mark: |
| Kind-aware `instantiate` and `collectMetas` in `TypeWalker` | :white_check_mark: |
| `resolveTypeAnnotation` returns `TypeScheme`; checking rule for universal types (D-034) | :white_check_mark: |
| Update accessor/update sugar to use open rows (supersedes D-021) | :white_check_mark: |
| Projection and update via unification (not direct field lookup) | :white_check_mark: |
| `CorePrinter`: handle `Rigid` | :white_check_mark: |
| Tests: open-row unification, type variables, row polymorphism, end-to-end | :white_check_mark: |

**Known deviations** (tracked as tech debt for D-035):
- D-032: `resolveDeep` not removed; `zonkOrKeep` uses old semantics instead of `Optional`.
- D-035: `CoreTypeAbs`/`CoreTypeApp` not yet added; instantiation uses `walkType` substitution.

**Ref**: [D-028](decisions.md#d-028)–[D-035](decisions.md#d-035), [Phase 1d plan](../../.cursor/plans/phase_1d_open_rows_bb2dae6a.plan.md)

### D-035 — Core IR System F Refactor :white_check_mark:

Added unary `CoreTypeAbs` and `CoreTypeApp` to the `CoreExpr` sealed hierarchy. Introduced
deferred constraint solving: the elaborator emits `Constraint` records, solved incrementally at
generalization boundaries. Rewrote `generalize` and `instantiateAndWrap` inside `Elaborator`,
deleted `TypeWalker.walkType`, `TypeWalker.generalize`, and `TypeWalker.instantiate`.
`resolveDeep` remains for `CorePrinter` display (future: environment-based Rigid resolution).
Dev endpoint now exposes `core_raw`, `constraints`, and `zonker` for debugging.

| Task | Status |
|------|--------|
| Add unary `CoreTypeAbs(int rigidId, Kind kind, CoreExpr body, MonoType type)` to `CoreExpr` | :white_check_mark: |
| Add unary `CoreTypeApp(CoreExpr polyExpr, MonoType typeArg, MonoType type)` to `CoreExpr` | :white_check_mark: |
| Deferred constraint emission (`Constraint` record + `ElaborationState` accumulator) | :white_check_mark: |
| Elaborator: emit `CoreTypeAbs` at generalization sites | :white_check_mark: |
| Elaborator: emit `CoreTypeApp` at instantiation sites (`instantiateAndWrap`) | :white_check_mark: |
| Wire constraint solver (`solveConstraints` at generalization + end of program) | :white_check_mark: |
| Rewrite `generalize` in `Elaborator` (solves metas to Rigids in zonker) | :white_check_mark: |
| Rewrite `instantiate` as `instantiateAndWrap` (shared fresh metas, body walk) | :white_check_mark: |
| Delete `TypeWalker.walkType`, `.generalize`, `.instantiate` | :white_check_mark: |
| `Evaluator`: handle `CoreTypeAbs`/`CoreTypeApp` (erase at runtime) | :white_check_mark: |
| `CorePrinter`: raw IR printer, constraint printer, zonker printer | :white_check_mark: |
| Dev endpoint: `core_raw`, `constraints`, `zonker` fields | :white_check_mark: |
| Update all tests (332 passing) | :white_check_mark: |

**Remaining tech debt**:
- `resolveDeep` still used by `CorePrinter` — replace with env-based Rigid resolution
- `zonkOrKeep` returns meta-on-miss, not `Optional` (D-032 deviation)

**Ref**: [D-035](decisions.md#d-035), [D-032](decisions.md#d-032), [D-005](decisions.md#d-005)

### Phase 1e — Pattern Matching :fast_forward: (deferred)

Pattern matching as the primary control-flow mechanism. `if/then/else` desugars to `match`.
Postponed from original Phase 1d position — row polymorphism was more pressing (D-029).
**Deferred again**: not blocking the MVP-critical path (Blocks A–C). Will be picked up when
control flow is needed by downstream work, or opportunistically between blocks.

| Task | Status |
|------|--------|
| Match expression syntax | :memo: |
| Pattern types (literal, variable, wildcard, constructor) | :memo: |
| Exhaustiveness checking | :memo: |
| `if/then/else` as sugar for `match` on Boolean | :memo: |

**Ref**: [Phase 1 language discussion](3cd2a822-792c-4179-a00e-0ba98b875f52)

---

### Phase 1 — Outstanding Tech Debt

Consolidated list of known deviations, limitations, and deferred work from Phase 1. These are
tracked here for visibility; they do not block Block A. Items may be addressed opportunistically
or when downstream work requires them.

| Item | Ref | Notes |
|------|-----|-------|
| `resolveDeep` still used by `CorePrinter` | D-032 | Should be replaced with environment-based Rigid resolution in downstream passes (printer, optimizer, lowering). |
| `zonkOrKeep` returns meta-on-miss instead of `Optional` | D-032 | D-032 specifies an `Optional`-returning `zonk` API. Current implementation preserves old semantics. |
| Bidirectional checking mode partially implemented | D-036 | Elaborator has `elaborate` (synthesis) and `check` modes, but polytype ascription at expression level does not work correctly (see D-038). |
| `MonoType` cannot represent polytypes (`∀a. τ`) | D-038 | `CoreTypeAbs.type()` returns body monotype with bare rigids. Fix: rename `MonoType` → `Type`, add `Forall` variant. Related tests are `@AwaitsFix`. |
| Pattern matching deferred (Phase 1e) | D-010, D-029 | `match` expressions, exhaustiveness checking, `if/then/else` as sugar — all deferred. Not blocking Blocks A+. |
| Integer-only arithmetic | D-020 | `Long` and `Double` literals exist but cannot participate in arithmetic. Requires coercion rules or type classes. |
| Null semantics unsound | D-007 | `Null` unifies with any type. Proper `Option` type requires ADTs (Phase 1e+). |
| `KeywordVal` uses `String`, not `BytesRef` | D-026 | Reverse conversion needed when piescript values flow into ESQL query parameters (Phase 2+). |
| Double `EsqlBodyParser.parse()` call | T2.6 | Index pattern extracted once in `IndexResolutionPrePass.collectQueries()` and again in `Queries.query()`. Consequence of opaque `ESQL_BODY` token approach. Goes away when ANTLR grammar structurally captures the `FROM` clause. |
| Opaque `ESQL_BODY` lexer token | T2.1 | ESQL body captured as backtick-delimited raw text (`` query `FROM ...` ``); index pattern extracted via Java string parsing. Future: parse `FROM <pattern>` structurally in the ANTLR grammar. |
| Empty mapping diagnostics | — | When `buildRowFields` produces an empty row (index exists but field caps returns no usable fields), emit a diagnostic on `ElaborationState` rather than silently producing `List { }`. Downstream type errors ("missing fields … in `{ }`") are confusing when the real issue is a missing or unmapped index. |

See also [General Tech Debt — ES Conventions & Plugin Infrastructure](#general-tech-debt--es-conventions--plugin-infrastructure)
for cross-cutting items (TransportVersion, logging, ActionType naming, thread pool, endpoint merge).

---

## Phase 2 — Index Resolution + Concrete-Row Constraints :white_check_mark:

Programs containing `query` expressions are typechecked against real ES index mappings. Cross-index
type conflicts and unmapped fields produce precise errors at the field-access site. Builds on the
open-row unification infrastructure from Phase 1d.

| Task | Status |
|------|--------|
| ANTLR grammar — `queryExpr` production with `ESQL_MODE` lexer mode | :white_check_mark: |
| `CoreQuery` variant in `CoreExpr` sealed hierarchy | :white_check_mark: |
| `List` type constructor (originally `Stream`, renamed in Block B — D-043), `DataTypeMapping` utility | :white_check_mark: |
| Index resolution pre-pass (`IndexResolver` integration, `ResolvedMapping`) | :white_check_mark: |
| Concrete-row constraint processing (cross-index conflict detection via `InvalidMappedField`) | :white_check_mark: |
| Query expression typing (`QueryExpr` → `CoreQuery` with `List { ... }`) | :white_check_mark: |
| `map`/`filter`/`reduce` as built-in typed functions (module-level free variables, `CoreFree` IR node, `Prelude`) | :white_check_mark: |
| Eager evaluation (fire `EsqlQueryAction`, convert rows to `RecordVal`s, `map`/`filter`/`reduce` over streams) | :white_check_mark: |
| Transport pipeline refactor (remove passthrough, unified dev/eval pipeline) | :white_check_mark: |
| Integration tests (query type-checking, eager eval end-to-end, map/filter/reduce) | :white_check_mark: |
| Documentation updates | :white_check_mark: |

**Ref**: [Phase 2 plan](../../.cursor/plans/phase2_implementation.plan.md), [Phase 2 row types (original)](../../.cursor/plans/phase2_row_types.plan.md), [Phase 2 completion](303bcf3e-9eef-4719-a47d-24c1ff27a675)

---

## Block A — `spawn` + Single-Value `when` (Async Coordination) :white_check_mark:

> Replaces the old Phase 3 (plan graph) and Phase 4 (`par` blocks). See D-040, D-041.

Introduces asynchronous coordination via the Join Calculus model. `spawn` launches a computation
asynchronously and returns a channel. `when` synchronizes on one or more channels — the `when`
body fires when all specified channels have delivered their values. This is the core concurrency
primitive that replaces the old `par` block.

The surface keyword is `when` (not `join`) to avoid collision with SQL/ESQL JOIN terminology.
See D-041 for rationale.

**Implementation strategy**: Leverage Elasticsearch's existing async infrastructure. A channel is
a `SubscribableListener<Value>` (single-completion future). `spawn` runs the body on
`threadPool.executor(GENERIC)` and writes the result to the channel. `when` uses a positional
collector (`AtomicArray<Value>` + `CountDown`) to fire the `when` body when all channels complete,
preserving binding order for de Bruijn indexing.

**Evaluator model**: Uniformly async — every `evaluate` call takes an `ActionListener<Value>`.
Pure expressions complete synchronously (callback fires inline). No separate sync/async code
paths. See D-041.

| Task | Status |
|------|--------|
| `Channel τ` type constructor in the type system | :white_check_mark: |
| `CoreSpawn` and `CoreWhen` variants in `CoreExpr` sealed hierarchy | :white_check_mark: |
| `spawn` and `when` in ANTLR grammar | :white_check_mark: |
| `SpawnVal(SubscribableListener<Value>)` in `Value` hierarchy | :white_check_mark: |
| Uniformly async evaluator refactor (CPS / ActionListener-based evaluation) | :white_check_mark: |
| `spawn` evaluation: fork to GENERIC thread pool, return `SpawnVal` | :white_check_mark: |
| `when` synchronization: positional collector for all arities | :white_check_mark: |
| `TransportPiescriptAction` async wiring (ActionListener pipeline) | :white_check_mark: |
| Error propagation through channels (spawn failure → channel failure) | :white_check_mark: |
| Unit tests (spawn/when semantics, concurrent queries, error propagation) | :white_check_mark: |
| Integration tests (concurrent ESQL queries via spawn + when) | :memo: |

**Key architectural decisions:**

- Join Calculus model replaces plan graph (D-040)
- `when` keyword instead of `join` to avoid SQL JOIN collision (D-041)
- Channels are `SubscribableListener<Value>` — single-value, future-like (Block A)
- `spawn` + `when` replace `par` as the coordination primitives (D-040)
- Uniformly async evaluator — no separate sync/async code paths (D-041)
- Positional collector for `when`, not `GroupedActionListener` (D-041)
- The evaluator is the interpreter; no separate planner/executor split needed (D-040)
- Stream combinators (`map`, `filter`, `reduce`) remain as eager built-ins over materialized
  `ListVal` for now (renamed from `StreamVal` in Block B — D-043)

**What carries forward from old plans:**

- D-005 (HM type system), D-006 (de Bruijn), D-014 (traveling closures), D-016 (combinators as
  prelude built-ins) — all still apply unchanged
- Mobility check concept — deferred to push-down compilation (deprioritized, see D-042)

**What is superseded:**

- D-012 (plan graph, not direct interpretation) — superseded by D-040. The evaluator now interprets
  directly with async coordination via channels.
- D-013 (two-layer IR: CoreExpr / CoreProcess) — superseded by D-040. `spawn` and `when` are
  `CoreExpr` nodes, not a separate `CoreProcess` hierarchy.
- D-015 (join calculus informing future design) — subsumed: join calculus is now the primary model,
  not just an influence.

**Ref**: [Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef),
[Block A plan](../../.cursor/plans/block_a_implementation_2fdbab36.plan.md),
[Block A implementation](14bf4826-a39e-4012-ab4c-d73ad902a95f)

---

## Block B — ES Topology & Node Types :white_check_mark:

> **Revised**: 2026-03-17. Replaces old Block B (multi-value channels). See D-042, D-043, D-044.

Make the cluster visible as typed piescript values. This is the entry point to distributed
execution — piescript can describe ES infrastructure as first-class values before it can send
code anywhere.

**What it delivers:**
- `topology` builtin function (D-044) that takes an index name and returns a record with both
  shard-centric and node-centric views of the cluster topology.
- Plain record types — nodes and shards are records with typed fields.
- `Stream` → `List` rename throughout (D-043): `TCon("List")`, `Value.ListVal`, prelude signatures.
- List utility builtins: `head`, `tail`, `length`, `isEmpty`.
- `EvalDependencies` context object bundling `Client`, `Executor`, `ClusterService`.
- `EvalTopology` class implementing the `topology` builtin.
- Implementation reads `ClusterState` → `RoutingTable` → `IndexRoutingTable` → `ShardRouting`
  → `DiscoveryNode` and converts to `RecordVal`/`ListVal`.

| Task | Status |
|------|--------|
| Inject `ClusterService` into piescript transport action | :white_check_mark: |
| `Stream` → `List` rename (type system, values, prelude, tests) — D-043 | :white_check_mark: |
| `topology` builtin: index name → topology record (both views) — D-044 | :white_check_mark: |
| Return type design: shard-centric + node-centric record structure | :white_check_mark: |
| Type the builtin in `Prelude` with concrete return type | :white_check_mark: |
| `EvalDependencies` context object (`Client`, `Executor`, `ClusterService`) | :white_check_mark: |
| `EvalTopology` class (topology resolution logic) | :white_check_mark: |
| List utility builtins (`head`, `tail`, `length`, `isEmpty`) | :white_check_mark: |
| Unit tests (topology resolution, record structure, list utilities) | :white_check_mark: |
| Integration test (real cluster topology via the endpoint) | :white_check_mark: |

**Deferred items** (not required for Block B):
- Wildcard / alias / data stream resolution in `topology` (exact index name only — D-044)
- Multi-project support (`ProjectId.DEFAULT` used — D-044)
- Non-STARTED shard states (only STARTED shards included — D-044)

---

## Block C — Cross-Node Code Execution (`send` + `spawn!`) :memo:

> **Revised**: 2026-03-17. Replaces old Block C (`writeTo` + scheduler). See D-042.

The core distributed computing story. Ship a closure to a remote node, get a result back. This is
the hardest and most important block.

**What it delivers:**
- `spawn!` — bare channel creation (`new SubscribableListener<>()` in a `SpawnVal`). Creates a
  channel without executing a body. User completes it via explicit `send`.
- `send` primitive — locally: `listener.onResponse(value)`. Cross-node: transport message routed
  to the channel's owner node via the channel registry.
- Closure serialization — `(CoreExpr body, Value[] captured_env)` over the wire. Core IR is a tree
  of records. Values are recursively serializable. Channel references serialize as
  `ChannelRef(ownerNodeId, channelId)`.
- Channel registry — `ConcurrentHashMap<String, SubscribableListener<Value>>` per node. Channels
  named as `<ownerNodeId>:<channelUuid>`.
- Remote evaluator — transport action on data nodes: accept serialized closure, evaluate, send
  result on specified channel.
- Two transport handlers:
  - `piescript/execute_closure` — coordinator → data node
  - `piescript/channel_message` — data node → coordinator (or any node → channel owner)

**Verification:** A `local_node` builtin returns the current node's identity. Remote closures
return `{ ran_on: local_node, result: ... }` to prove code crossed nodes. Integration tests use
multi-node clusters.

**Likely sub-blocks** (to be detailed when implementation begins):
- C.1: `spawn!` + local `send` (semantics without transport)
- C.2: `Value` serialization (`Writeable` for all `Value` variants)
- C.3: `CoreExpr` serialization (Core IR over the wire)
- C.4: Transport handlers + channel registry (cross-node wiring)
- C.5: Multi-node integration test

| Task | Status |
|------|--------|
| `spawn!` — bare channel creation (grammar + Core IR + evaluator) | :memo: |
| `send` primitive — local channel completion | :memo: |
| `Value` serialization (`Writeable` implementations) | :memo: |
| `CoreExpr` serialization (Core IR tree over transport) | :memo: |
| Channel registry (`ConcurrentHashMap` per node) | :memo: |
| Transport handler: `piescript/execute_closure` | :memo: |
| Transport handler: `piescript/channel_message` | :memo: |
| Remote evaluator (evaluate closure on data node) | :memo: |
| `local_node` builtin (for verification) | :memo: |
| Multi-node integration tests (prove cross-node execution) | :memo: |

---

## Block D — Local Data Access (`scan`) :memo:

> **Revised**: 2026-03-17. Replaces old Block D (push-down compilation). See D-042.

Access data on a data node without going through ESQL. Completes the distributed vertical slice.

**What it delivers:**
- `scan` as a builtin function — takes a shard reference (from Block B topology records), returns
  data. For the vertical slice: returns `ListVal` (materialized).
- Implementation: `IndexSearcher` / Lucene on the local shard. Runs inside closures shipped via
  `send`.
- The `RawData` lazy type (typeclass-driven push-down to Lucene) is a future optimization, not
  required for the vertical slice.

| Task | Status |
|------|--------|
| `scan` builtin function (grammar or prelude) | :memo: |
| Shard-local Lucene query execution | :memo: |
| Result conversion to `ListVal` | :memo: |
| Integration test: `send` closure with `scan` to data node, verify results | :memo: |

**Full vertical slice example** (after Blocks B+C+D):

```
let topo = topology "my-index"
in let target = head topo.shards
in let ch = spawn!
in send target.node.inbox (fn () ->
  let data = scan target |> filter (fn r -> r.status == "active")
  in send ch data
)
in when (ch results) ->
  results |> map (fn r -> { id: r.id, status: r.status })
```

---

## Block E — Writing Sinks (`writeTo`) :memo: (Stretch Goal)

> **Revised**: 2026-03-17. Moved from old Block C. Not required for the distributed vertical slice.

Persist piescript results to an index via the Bulk API. Makes piescript a replacement for ES
Transforms. Depends on the distributed vertical slice being complete (Blocks B–D) for the full
story, but could be implemented independently for coordinator-only use.

| Task | Status |
|------|--------|
| `writeTo` sink primitive (Core IR + grammar + typing) | :memo: |
| Bulk API integration (batch writes from stream results) | :memo: |
| Integration tests (write-back, error handling) | :memo: |

Scheduled execution (persistent tasks, REST API for managing piescript jobs, checkpointing) is
deferred until `writeTo` lands and the scheduler story is needed.

---

## Deferred: Multi-Value Channels :thought_balloon:

> Old Block B, reworked. See D-042.

Block A's channels are single-value (one completion). Multi-value channels carry streams of
messages over time — needed for streaming patterns, fold-as-join, and event handling. Single-value
`send` (completing a `spawn!`) is covered by Block C. Multi-value channels are about repeated
messages on the same channel.

| Task | Status |
|------|--------|
| `newchan` primitive (explicit multi-value channel creation) | :thought_balloon: |
| Multi-value channel implementation (concurrent queue + notification) | :thought_balloon: |
| Join automaton for pattern matching over multi-value channels | :thought_balloon: |
| Channel completion / close semantics | :thought_balloon: |
| Backpressure mechanism | :thought_balloon: |

---

## Deferred: Typeclass-Driven Push-Down (RawData → Lucene) :thought_balloon:

> Replaces old Block D (push-down to ESQL text). See D-042.

Typeclasses specialize generic functions based on data representation:

```
instance Filterable RawData where
  filter pred rawdata = rawdata.addLuceneFilter(compilePredicate pred)

instance Filterable List where
  filter pred list = list.filter(pred)
```

When `scan` returns `RawData` (a description, not data), typeclass instances push operations into
the description (filter → Lucene query, project → stored fields). Materialization to `Page`/`Block`
happens only when actual data is needed. This is more general than the old Block D (compile to
ESQL text) because it works with piescript's own data access path (`scan`), not just ESQL queries.

Requires: typeclasses in the language (significant), `RawData` type, compiler instance resolution,
`LuceneQuery` type.

---

## Deferred: Exchange Streaming (Scale) :thought_balloon:

> Reframed from old Block E. See D-042.

For large data volumes, piescript orchestrates Exchange setup **explicitly via channels** — the
Exchange is ES infrastructure that piescript talks to, not infrastructure piescript is built on:

1. Send closure to data node (via `send`)
2. Data node scans, materializes into Exchange sink, sends back metadata (exchange ID)
3. Coordinator connects Exchange source, signals to begin streaming
4. Pages stream with back-pressure via Exchange
5. Coordinator converts Pages to Values at the boundary (or processes directly)

This is an explicit coordination protocol written in piescript (or in a library), not hidden
runtime magic. Piescript doesn't abstract over scale decisions — the user chooses.

---

## Deprioritized: Push-Down to ESQL Text :thought_balloon:

> Old Block D. Deprioritized by D-042 — typeclass approach is more general.

Compile piescript lambdas into ESQL expression strings (`filter pred` → `WHERE`, `map f` →
`EVAL`). Still useful as an optimization for the `query \`ESQL\`` path, but no longer on the
critical path. Significant compiler work (closure conversion, lambda lifting, mobility analysis)
for limited scope (only works with ESQL, not with piescript's own `scan`).

---

## Phase 6 — QTT Multiplicities, Explicit Channels & Session Types :thought_balloon:

> Design rationale: `6c10d690-5758-49da-88f5-4c38f2f9cd72`

Introduces QTT-style multiplicities {0, 1, ω} on bindings (D-018). Channel endpoints are linear
(multiplicity 1), enabling session types with deadlock-freedom. Streams and all other values
remain unrestricted (ω). Builds on the channel primitives from Block C (`send`, `spawn!`).

- QTT multiplicity annotations on function types (`A →_π B`)
- Usage tracking in the type checker (count how many times each binding is used)
- Session types: type-checked communication protocols on channels
- Deadlock-freedom from the type system (Wadler's Propositions as Sessions)
- Linear closures as an optimization: move instead of clone (zero-copy)

**Key references:** Linear Haskell (Bernardy et al. 2018), Idris 2 / QTT (Brady 2021).
See [references.md](references.md).

---

## Phase 7 — Module System :thought_balloon:

Stored, reusable piescript definitions.

- Named program storage (cluster state or dedicated index)
- Import/reference between programs
- Versioning and backwards compatibility

---

## Phase 8 — Tooling :thought_balloon:

Developer experience beyond the REST API.

- Language Server Protocol implementation
- Syntax highlighting definitions
- REPL / interactive evaluation mode

---

## General Tech Debt — ES Conventions & Plugin Infrastructure

Cross-cutting tech debt related to Elasticsearch conventions, plugin infrastructure, and
production readiness. These are not tied to any specific phase — they apply to the plugin as a
whole and should be addressed before merging to main or shipping.

Identified by reviewing the plugin against `docs/internal/GeneralArchitectureGuide.md`,
`docs/internal/DistributedArchitectureGuide.md`, and `docs/internal/Versioning.md`.

| Item | Severity | Notes |
|------|----------|-------|
| No backwards-compatibility versioning | High | `PiescriptRequest`/`PiescriptResponse` do not use `TransportVersion` checks. Required before shipping — any serialization field added after the initial version needs a version guard. See `Versioning.md`. |
| `ActionType` name scope review | Medium | `PiescriptAction.NAME` is `"indices:data/read/piescript"`. Piescript is a language evaluation endpoint that may or may not touch indices. `cluster:admin/piescript/eval` or `cluster:data/read/piescript` might be more semantically accurate per `GeneralArchitectureGuide.md` § Transport Layer naming conventions. Needs a deliberate decision — `indices:` scope is defensible given `CompositeIndicesRequest` and ESQL delegation. |
| No logging in transport/eval layer | Medium | `TransportPiescriptAction`, `Evaluator`, and supporting classes have no logging. Should add `private static final Logger logger = LogManager.getLogger(...)` with WARN for unexpected failures and DEBUG for pipeline stage timing, per ES logging conventions. |
| `ActionListener.wrap()` usage | Low | Several call sites use `ActionListener.wrap(onResponse, onFailure)` instead of the preferred `delegateFailureAndWrap()` pattern. Instances in `TransportPiescriptAction` (dev pipeline), `EvalCoordination.PositionalCollector`, and `IndexResolutionPrePass.resolve`. Some are intentional (dev pipeline converts failures to response fields), but others should be modernized. |
| Dedicated thread pool | Low | Transport action and evaluator run on `ThreadPool.Names.GENERIC`. Fine for a prototype, but a dedicated thread pool (via `Plugin.getExecutorBuilders()`) would provide better isolation and tunability for a production language runtime. |
| Merge `/_piescript/eval` and `/_piescript/dev` | Low | Both REST handlers dispatch to `TransportPiescriptAction` with a `dev` flag. Future: single `/_piescript/eval?dev` endpoint, eliminating `RestPiescriptDevAction`. |
| Duplicate `parseProgram()` in REST handlers | Low | `RestPiescriptAction` and `RestPiescriptDevAction` have identical `parseProgram()` methods. Should be extracted to a shared utility. Goes away when the endpoints are merged. |
| Identical catch blocks in dev pipeline | Low | `TransportPiescriptAction.elaborateAndEvaluateDev` catches `ElaborationException` and `Exception` separately but handles both identically. Should be collapsed to a single `Exception` catch. |

---

## Speculative: Ownership, Mutable References & Beyond :thought_balloon:

> Design rationale: `6c10d690-5758-49da-88f5-4c38f2f9cd72`

> **Caveat:** These are exploratory ideas. They represent potential directions that QTT
> multiplicities could unlock if Phase 6 succeeds, but they are NOT planned, NOT committed, and
> may turn out to be impractical. Recorded here to inform long-term design thinking.

If QTT multiplicities prove successful for channels, the same machinery could potentially support:

- **Mutable shared references** — owned (linear) values that can be exclusively mutated by one
  process at a time, approaching Rust-like ownership semantics
- **Persistent in-memory resources** — shared counters, lookup tables, caches that live beyond a
  single query pipeline
- **Incremental computation** — update aggregations incrementally rather than recomputing
- **Safe write-back** — linearly-owned write buffers for eventual index writes
- **Long-lived processes** — with OTP-style supervision patterns (inspired by Erlang/BEAM)

The open questions are substantial: borrow checking vs. QTT alone, distributed ownership
protocols, resource reclamation across nodes, and ergonomics for non-PL-specialist users. See
[vision.md § Speculative](vision.md) for discussion.
