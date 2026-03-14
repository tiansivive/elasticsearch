# Roadmap

> **Living doc** — update status markers as work progresses. Add new phases/sub-phases as they are
> planned.

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
checking across the entire pipeline), performance (push-down into ESQL's distributed engine), and
unification (one language replacing multiple chained features).

**MVP scope** — the following phases must be complete:

| Phase | What it contributes to the MVP |
|-------|-------------------------------|
| Phase 1e | Pattern matching — control flow in transforms |
| Phase 2 | Index resolution — typed query results, field-level type checking |
| Phase 3 | Stream runtime + plan graph + combinators + push-down optimizer + `writeTo` + `groupBy` |
| Phase 4 (partial) | `par` — merging results from multiple queries |
| New: Scheduler | Persistent task wrapper for scheduled async execution |

**Post-MVP enhancements** (not required for the MVP demonstration):

- Phase 5: Full distributed executor (serialize closures, ship to data nodes)
- Phase 6: QTT multiplicities, channels, session types
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

**Ref**: [Phase 0 scaffold discussion](b0ac3e4f-db5e-4a03-85ec-a3016912512c)

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

**Ref**: [Phase 1a parser implementation](3cd2a822-792c-4179-a00e-0ba98b875f52)

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

**Ref**: [Phase 1b implementation](36ef4cb3-4c3b-439e-a83a-aae069ca551c)

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

**Ref**: [Phase 1c plan](../../.cursor/plans/phase_1c_evaluator_wiring_7ec117b6.plan.md)

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

### Phase 1e — Pattern Matching :memo:

Pattern matching as the primary control-flow mechanism. `if/then/else` desugars to `match`.
Postponed from original Phase 1d position — row polymorphism was more pressing (D-029).

| Task | Status |
|------|--------|
| Match expression syntax | :memo: |
| Pattern types (literal, variable, wildcard, constructor) | :memo: |
| Exhaustiveness checking | :memo: |
| `if/then/else` as sugar for `match` on Boolean | :memo: |

**Ref**: [Phase 1 language discussion](3cd2a822-792c-4179-a00e-0ba98b875f52)

---

## Phase 2 — Index Resolution + Concrete-Row Constraints :memo:

Programs containing `query` expressions are typechecked against real ES index mappings. Cross-index
type conflicts and unmapped fields produce precise errors at the field-access site. Builds on the
open-row unification infrastructure from Phase 1d.

| Task | Status |
|------|--------|
| Concrete-row constraint processing (cross-index conflict detection) | :memo: |
| Index resolution pre-pass (`IndexResolver` integration) | :memo: |
| `query` expression typing (returns `Stream (Record ρ)`) | :memo: |
| `map`/`filter` as built-in typed functions | :memo: |
| DataType → TCon mapping table | :memo: |
| Integration tests (index conflicts, unmapped fields, polymorphic propagation) | :memo: |

---

## Phase 3 — Stream Runtime & Plan Graph :memo:

Introduces the plan graph architecture — the centerpiece that enables distributed execution. The
evaluator builds plan graph nodes for process-level operations; the v0 executor runs them locally.
Stream combinators (`map`, `filter`, `fold`, `groupBy`) are prelude built-in functions that
construct plan graph nodes when applied to streams. The push-down optimizer compiles compatible
transforms into ESQL expressions (map → EVAL, filter → WHERE, groupBy + fold → STATS), so they
run on data nodes via ESQL's distributed engine. The `writeTo` sink primitive writes stream results
to a target index via the bulk API.

**This phase is the core of the MVP.** It delivers the stream runtime, plan graph, combinators,
push-down optimization for distributed performance, and write-back — the pieces needed to express
full data pipelines as piescript programs.

| Task | Status |
|------|--------|
| `CoreProcess` IR layer (`Query`, `WriteTo` — combinators are built-ins) | :memo: |
| Plan graph IR (free monad over π effects) | :memo: |
| Evaluator/planner split (functional → evaluate, process → plan) | :memo: |
| Prelude built-in functions: `map`, `filter`, `fold` (D-016) | :memo: |
| `groupBy` combinator (grouping semantics for aggregation) | :memo: |
| `writeTo` sink primitive (write stream results to an index via bulk API) | :memo: |
| Mobility check (can this lambda travel?) | :memo: |
| Core IR to ExpressionEvaluator compiler (fast path) | :memo: |
| Push-down optimizer (map → EVAL, filter → WHERE, groupBy + fold → STATS) | :memo: |
| v0 local executor (runs plan on coordinator) | :memo: |
| Query delegation to ESQL | :memo: |
| Result serialization for streams | :memo: |
| Integration tests (stream fan-out, push-down, write-back) | :memo: |

**Key architectural decisions:**
- Plan graph, not direct interpretation (D-012)
- Two-layer IR: `CoreExpr` / `CoreProcess` (D-013)
- Closures as traveling code (D-014)
- Stream combinators as prelude built-ins, not Core IR nodes (D-016)
- Stream fan-out via DAG, streams are unrestricted (D-017)
- Mobility check = "can this code be compiled to ExpressionEvaluator?" (v0), "can it be serialized
  and shipped?" (future)
- Push-down into ESQL provides distributed execution for the MVP without a custom distributed
  executor — transforms that compile to ESQL expressions run on data nodes, vectorized, parallel
  across shards

---

## Phase 4 — Process Primitives & Plan Composition :memo:

Extends the plan graph with parallel composition (`par` blocks). Multiple queries dispatch
concurrently as independent plan branches. First real use of π-calculus foundations.

| Task | Status |
|------|--------|
| `Par` in `CoreProcess` IR | :memo: |
| `par` in ANTLR grammar | :memo: |
| `Par` typing rule (concurrent let, independent bindings) | :memo: |
| `ParPlanNode` in plan graph | :memo: |
| Plan optimizer: dead-branch elimination, push-down into branches | :memo: |
| Async execution of parallel branches (ActionListeners) | :memo: |
| Integration tests | :memo: |

**Key architectural decisions:**
- `par` builds plan nodes, not fires async queries (D-012)
- Channels are implicit plan graph edges (v0); explicit channels are future work
- Join calculus informs primitive selection (D-015)

---

## Phase 4b — Scheduler & Async Execution :memo:

Scheduled, persistent execution of piescript programs within Elasticsearch. A stored piescript
program runs as a persistent task on a configurable schedule, enabling batch data pipelines
(the "transform" use case). Leverages ES's existing persistent task infrastructure.

| Task | Status |
|------|--------|
| Stored program representation (simple precursor to Phase 7 module system) | :memo: |
| Persistent task implementation for piescript execution | :memo: |
| REST API for creating/managing scheduled piescript jobs | :memo: |
| Status/progress reporting via the tasks API | :memo: |
| Checkpointing for incremental/resumable execution | :memo: |
| Integration tests (scheduled execution, failure recovery) | :memo: |

---

## Phase 5 — Distributed Executor :thought_balloon:

The plan graph executor dispatches plan fragments to data nodes. Transforms co-located with data.
Channels between nodes implemented as Exchange operators. This is where piescript becomes a truly
distributed computation language.

- Plan fragment serialization (traveling closures over the wire)
- Placement strategy (co-locate computation with data, leverage ESQL shard routing)
- Cross-node channels via Exchange mechanism
- Location-aware plan optimization
- Fault tolerance for distributed plan execution

---

## Phase 6 — QTT Multiplicities, Explicit Channels & Session Types :thought_balloon:

> Design rationale: `6c10d690-5758-49da-88f5-4c38f2f9cd72`

Introduces QTT-style multiplicities {0, 1, ω} on bindings (D-018). Channel endpoints are linear
(multiplicity 1), enabling session types with deadlock-freedom. Streams and all other values
remain unrestricted (ω). User-visible channel primitives: `new`, `send`, `recv`.

- QTT multiplicity annotations on function types (`A →_π B`)
- Usage tracking in the type checker (count how many times each binding is used)
- `new`/`send`/`recv` as `CoreProcess` nodes with session-typed channels
- Session types: type-checked communication protocols on channels
- Deadlock-freedom from the type system (Wadler's Propositions as Sessions)
- Join patterns (Fournet & Gonthier) for multi-way synchronization
- Producer-consumer patterns via explicit channels
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
