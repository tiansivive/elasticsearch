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
| Bidirectional elaborator (infer / check modes, desugaring) | :white_check_mark: |
| Let-generalization (binding-level-based) | :white_check_mark: |
| De Bruijn index representation | :white_check_mark: |
| Null semantics (v0: Null unifies with Any) | :white_check_mark: |
| Elaborator tests | :white_check_mark: |
| Dev endpoint wired to elaborator (`CorePrinter`, tree + core + type) | :white_check_mark: |

**Ref**: [Phase 1b implementation](36ef4cb3-4c3b-439e-a83a-aae069ca551c)

### Phase 1c — Evaluator + Wiring :memo:

Tree-walking interpreter and end-to-end pipeline integration.

| Task | Status |
|------|--------|
| Runtime value types (`Eval`, `Value`, `Closure`) | :memo: |
| Tree-walking evaluator | :memo: |
| Value serialization (scalar → 1x1 table, record → 1-row table) | :memo: |
| Wire pipeline into transport action (replace Phase 0 passthrough) | :memo: |
| Integration + unit tests (vertical slice + deferred elaborator tests) | :memo: |

### Phase 1d — Pattern Matching :memo:

Pattern matching as the primary control-flow mechanism. `if/then/else` desugars to `match`.

| Task | Status |
|------|--------|
| Match expression syntax | :memo: |
| Pattern types (literal, variable, wildcard, constructor) | :memo: |
| Exhaustiveness checking | :memo: |
| `if/then/else` as sugar for `match` on Boolean | :memo: |

**Ref**: [Phase 1 language discussion](3cd2a822-792c-4179-a00e-0ba98b875f52)

---

## Phase 2 — Row Types + Index Resolution :memo:

The type system's unique feature. Programs containing `query` expressions are typechecked against
real ES index mappings. Cross-index type conflicts and unmapped fields produce precise errors at
the field-access site. Row polymorphism enables functions over partial document structure.

| Task | Status |
|------|--------|
| Row unification algorithm (open rows, Rémy-style) | :memo: |
| Concrete-row constraint processing (cross-index conflict detection) | :memo: |
| Index resolution pre-pass (`IndexResolver` integration) | :memo: |
| `query` expression typing (returns `Stream (Record ρ)`) | :memo: |
| `map`/`filter` as built-in typed functions | :memo: |
| DataType → TCon mapping table | :memo: |
| Integration tests (row polymorphism, index conflicts, unmapped fields) | :memo: |

---

## Phase 3 — Stream Runtime & Plan Graph :memo:

Introduces the plan graph architecture — the centerpiece that enables future distributed execution.
The evaluator builds plan graph nodes for process-level operations; the v0 executor runs them
locally. Stream combinators (`map`, `filter`, `fold`) are prelude built-in functions that construct
plan graph nodes when applied to streams.

| Task | Status |
|------|--------|
| `CoreProcess` IR layer (`Query` only — combinators are built-ins) | :memo: |
| Plan graph IR (free monad over π effects) | :memo: |
| Evaluator/planner split (functional → evaluate, process → plan) | :memo: |
| Prelude built-in functions: `map`, `filter`, `fold` (D-016) | :memo: |
| Mobility check (can this lambda travel?) | :memo: |
| Core IR to ExpressionEvaluator compiler (fast path) | :memo: |
| v0 local executor (runs plan on coordinator) | :memo: |
| Query delegation to ESQL | :memo: |
| Result serialization for streams | :memo: |
| Integration tests (incl. stream fan-out) | :memo: |

**Key architectural decisions:**
- Plan graph, not direct interpretation (D-012)
- Two-layer IR: `CoreExpr` / `CoreProcess` (D-013)
- Closures as traveling code (D-014)
- Stream combinators as prelude built-ins, not Core IR nodes (D-016)
- Stream fan-out via DAG, streams are unrestricted (D-017)
- Mobility check = "can this code be compiled to ExpressionEvaluator?" (v0), "can it be serialized
  and shipped?" (future)

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
