# Roadmap

> **Living doc** — update status markers as work progresses. Add new phases/sub-phases as they are
> planned.
>
> **Revised**: 2026-03-16. Phases 3–5 have been replaced by Blocks A–E based on the Join Calculus
> model (D-040). The previous phase-based roadmap is archived in
> `docs/archive/roadmap.pre-join-calculus.md`. Old plan files (`phase3_stream_runtime.plan.md`,
> `phase4_process_primitives.plan.md`) are archived in `docs/archive/`.

**Overall design**: [scripting language design](../../.cursor/plans/scripting_language_design_9286506e.plan.md)

## Status Legend

| Marker | Meaning |
|--------|---------|
| :white_check_mark: | Complete |
| :construction: | In progress |
| :memo: | Planned (design exists) |
| :thought_balloon: | Aspirational (no detailed design yet) |

---

## MVP Milestone — Unified Data Pipelines

> See [vision.md § MVP](vision.md#mvp-unified-data-pipelines) for the full rationale.

The MVP target is a piescript program that replaces the combination of ES Transforms, enrich
policies, enrich processors, and ingest pipeline chains with a single typed program. The MVP
demonstrates expressiveness (arbitrary user-defined logic), type safety (compile-time field and type
checking across the entire pipeline), concurrency (parallel queries via `spawn` + `join`), and
unification (one language replacing multiple chained features).

**MVP scope** — the following must be complete:

| Block | What it contributes to the MVP | Status |
|-------|-------------------------------|--------|
| Phase 1e | Pattern matching — control flow in transforms | Deferred — not blocking Blocks A+ |
| Phase 2 | Index resolution — typed query results, field-level type checking, eager evaluation | :white_check_mark: |
| Block A | `spawn` + single-value `when` — concurrent multi-query coordination | :memo: |
| Block B | Multi-value channels — full Join Calculus runtime | :memo: |
| Block C | `writeTo` sink + scheduler — persistence and scheduled execution | :memo: |

**Post-MVP enhancements** (not required for the MVP demonstration):

- Block D: Push-down compilation (piescript lambdas → ESQL expressions)
- Block E: Exchange integration (streaming performance via ESQL's compute engine)
- Phase 6: QTT multiplicities, session types, explicit user-facing channels
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
| `DIRECT_EXECUTOR_SERVICE` | D-004 | Parse, elaborate, evaluate all run synchronously on the calling thread. Needs a dedicated thread pool when computation becomes heavier. |
| `KeywordVal` uses `String`, not `BytesRef` | D-026 | Reverse conversion needed when piescript values flow into ESQL query parameters (Phase 2+). |
| No backwards-compatibility versioning | — | `PiescriptRequest`/`PiescriptResponse` do not use `TransportVersion` checks. |
| Double `EsqlBodyParser.parse()` call | T2.6 | Index pattern extracted once in `IndexResolutionPrePass.collectQueries()` and again in `Queries.query()`. Consequence of opaque `ESQL_BODY` token approach. Goes away when ANTLR grammar structurally captures the `FROM` clause. |
| Opaque `ESQL_BODY` lexer token | T2.1 | ESQL body captured as backtick-delimited raw text (`` query `FROM ...` ``); index pattern extracted via Java string parsing. Future: parse `FROM <pattern>` structurally in the ANTLR grammar. |
| Merge `/_piescript/eval` and `/_piescript/dev` | T2.9 | Both REST handlers dispatch to `TransportPiescriptAction` with a `dev` flag. Future: single `/_piescript/eval?dev` endpoint, eliminating `RestPiescriptDevAction`. |
| Empty mapping diagnostics | — | When `buildRowFields` produces an empty row (index exists but field caps returns no usable fields), emit a diagnostic on `ElaborationState` rather than silently producing `Stream { }`. Downstream type errors ("missing fields … in `{ }`") are confusing when the real issue is a missing or unmapped index. |

---

## Phase 2 — Index Resolution + Concrete-Row Constraints :white_check_mark:

Programs containing `query` expressions are typechecked against real ES index mappings. Cross-index
type conflicts and unmapped fields produce precise errors at the field-access site. Builds on the
open-row unification infrastructure from Phase 1d.

| Task | Status |
|------|--------|
| ANTLR grammar — `queryExpr` production with `ESQL_MODE` lexer mode | :white_check_mark: |
| `CoreQuery` variant in `CoreExpr` sealed hierarchy | :white_check_mark: |
| `Stream` type constructor, `DataTypeMapping` utility | :white_check_mark: |
| Index resolution pre-pass (`IndexResolver` integration, `ResolvedMapping`) | :white_check_mark: |
| Concrete-row constraint processing (cross-index conflict detection via `InvalidMappedField`) | :white_check_mark: |
| Query expression typing (`QueryExpr` → `CoreQuery` with `Stream { ... }`) | :white_check_mark: |
| `map`/`filter`/`reduce` as built-in typed functions (module-level free variables, `CoreFree` IR node, `Prelude`) | :white_check_mark: |
| Eager evaluation (fire `EsqlQueryAction`, convert rows to `RecordVal`s, `map`/`filter`/`reduce` over streams) | :white_check_mark: |
| Transport pipeline refactor (remove passthrough, unified dev/eval pipeline) | :white_check_mark: |
| Integration tests (query type-checking, eager eval end-to-end, map/filter/reduce) | :white_check_mark: |
| Documentation updates | :white_check_mark: |

**Ref**: [Phase 2 plan](../../.cursor/plans/phase2_implementation.plan.md), [Phase 2 row types (original)](../../.cursor/plans/phase2_row_types.plan.md), [Phase 2 completion](303bcf3e-9eef-4719-a47d-24c1ff27a675)

---

## Block A — `spawn` + Single-Value `when` (Async Coordination) :memo:

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
| `Channel τ` type constructor in the type system | :memo: |
| `CoreSpawn` and `CoreWhen` variants in `CoreExpr` sealed hierarchy | :memo: |
| `spawn` and `when` in ANTLR grammar | :memo: |
| `SpawnVal(SubscribableListener<Value>)` in `Value` hierarchy | :memo: |
| Uniformly async evaluator refactor (CPS / ActionListener-based evaluation) | :memo: |
| `spawn` evaluation: fork to GENERIC thread pool, return `SpawnVal` | :memo: |
| `when` synchronization: positional collector for all arities | :memo: |
| `TransportPiescriptAction` async wiring (ActionListener pipeline) | :memo: |
| Error propagation through channels (spawn failure → channel failure) | :memo: |
| Unit tests (spawn/when semantics, concurrent queries, error propagation) | :memo: |
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
  `StreamVal` for now (no change from Phase 2)

**What carries forward from old plans:**

- D-005 (HM type system), D-006 (de Bruijn), D-014 (traveling closures), D-016 (combinators as
  prelude built-ins) — all still apply unchanged
- Mobility check concept — deferred to Block D (push-down compilation)

**What is superseded:**

- D-012 (plan graph, not direct interpretation) — superseded by D-040. The evaluator now interprets
  directly with async coordination via channels.
- D-013 (two-layer IR: CoreExpr / CoreProcess) — superseded by D-040. `spawn` and `when` are
  `CoreExpr` nodes, not a separate `CoreProcess` hierarchy.
- D-015 (join calculus informing future design) — subsumed: join calculus is now the primary model,
  not just an influence.

**Ref**: [Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef),
[Block A plan](../../.cursor/plans/block_a_implementation_2fdbab36.plan.md)

---

## Block B — Multi-Value Channels (Full Join Calculus) :memo:

> Extends Block A with streaming channels and explicit send/receive.

Block A's channels carry a single value (the final result of a `spawn`ed computation). Block B
introduces multi-value channels that carry streams of messages, enabling:

- **Fold-as-join**: aggregate results incrementally as values arrive on a channel.
- **Streaming intermediate results**: one computation produces values over time, another consumes
  them concurrently.
- **General event handling**: react to sequences of events, not just single completions.

**Implementation strategy**: Introduce a lightweight piescript-native multi-value channel
(`Queue<Value>` + notification mechanism), distinct from ESQL's `Exchange`. The join automaton
matches patterns over these channels — firing the join body each time the pattern is satisfied.

| Task | Status |
|------|--------|
| `newchan` and `send` primitives (Core IR + grammar) | :memo: |
| Multi-value channel implementation (concurrent queue + notification) | :memo: |
| Join automaton for pattern matching over multi-value channels | :memo: |
| Join semantics: `&` (all channels) and `|` (any channel — if feasible) | :memo: |
| Channel completion / close semantics | :memo: |
| Backpressure mechanism (optional, may defer) | :memo: |
| Unit and integration tests | :memo: |

---

## Block C — `writeTo` Sink + Scheduler :memo:

Adds persistence and scheduled execution. `writeTo` writes stream results to a target index
(via the Bulk API). The scheduler runs piescript programs as persistent tasks on a configurable
schedule — the "Transform replacement" use case.

| Task | Status |
|------|--------|
| `writeTo` sink primitive (Core IR + grammar + typing) | :memo: |
| Bulk API integration (batch writes from stream results) | :memo: |
| Stored program representation (simple precursor to Phase 7 module system) | :memo: |
| Persistent task implementation for piescript execution | :memo: |
| REST API for creating/managing scheduled piescript jobs | :memo: |
| Status/progress reporting via the tasks API | :memo: |
| Checkpointing for incremental/resumable execution | :memo: |
| Integration tests (scheduled execution, write-back, failure recovery) | :memo: |

---

## Block D — Push-Down Compilation (Optimization) :thought_balloon:

Optimizes performance by compiling mobile piescript lambdas into ESQL expressions. A `map` with
a simple field projection becomes an ESQL `EVAL` clause; a `filter` with a simple predicate
becomes a `WHERE` clause. This is significant compiler work — not a simple string concatenation.

**Complexity**: Requires closure conversion, lambda lifting, and a mobility analysis to determine
which lambdas can be expressed in ESQL's expression language. Recursion, higher-order functions,
closures over complex values, and sub-queries all present challenges. Semantic divergence (piescript
vs. ESQL behavior for the same operation) must be carefully managed.

| Task | Status |
|------|--------|
| Mobility analysis (which lambdas are ESQL-expressible?) | :thought_balloon: |
| Core IR → ESQL text compiler backend | :thought_balloon: |
| Closure conversion / lambda lifting for mobile closures | :thought_balloon: |
| Push-down optimization pass (rewrite queries with fused transforms) | :thought_balloon: |
| `groupBy` + `reduce` → ESQL `STATS` compilation | :thought_balloon: |
| Semantic equivalence testing (piescript eval vs. ESQL execution) | :thought_balloon: |

---

## Block E — Exchange Integration (Streaming Performance) :thought_balloon:

Integrates piescript with ESQL's `Exchange` mechanism for high-throughput, distributed streaming
data flow. Instead of materializing full query results before processing, piescript operates on
`Page`s/`Block`s incrementally as they stream through the Exchange pipeline.

| Task | Status |
|------|--------|
| Piescript as Exchange consumer (process Pages incrementally) | :thought_balloon: |
| Piescript as Exchange producer (emit Pages to downstream operators) | :thought_balloon: |
| `Value` ↔ `Block`/`Page` conversion layer | :thought_balloon: |
| Cross-node channel implementation via Exchange | :thought_balloon: |

---

## Phase 6 — QTT Multiplicities, Explicit Channels & Session Types :thought_balloon:

> Design rationale: `6c10d690-5758-49da-88f5-4c38f2f9cd72`

Introduces QTT-style multiplicities {0, 1, ω} on bindings (D-018). Channel endpoints are linear
(multiplicity 1), enabling session types with deadlock-freedom. Streams and all other values
remain unrestricted (ω). User-visible channel primitives beyond `spawn`/`join`/`newchan`/`send`.

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
