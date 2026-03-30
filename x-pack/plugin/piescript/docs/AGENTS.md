# Piescript — Agent Context

> **Purpose**: This file is the primary entry point for AI agents working on the piescript plugin.
> Read this first, then follow links to detailed docs as needed.

## What is Piescript?

Piescript is a **typed functional language for distributed computation** in Elasticsearch. It uses
Join Calculus primitives (`spawn`, `when`, channels) to coordinate asynchronous data pipelines
that run where the data lives. Pure functional expressions (lambdas, let-bindings, records)
evaluate locally; coordination primitives (`spawn` launches async work, `when` synchronizes on
channels) orchestrate concurrent execution. The surface keyword is `when` (not `join`) to avoid
collision with SQL/ESQL JOIN terminology — see D-041. User-defined functions travel to data nodes
as closures — safe because the language is pure and referentially transparent.

## Quick Orientation

| Doc | What it covers |
|-----|---------------|
| [vision.md](vision.md) | Long-term goals, Join Calculus coordination model, design philosophy, non-goals |
| [roadmap.md](roadmap.md) | Block-based development plan with status markers (Blocks A–F complete, D-053 F-omega complete, Blocks G–H planned) |
| [current-state.md](current-state.md) | What works **right now**, known limitations, immediate next steps |
| [architecture.md](architecture.md) | System design, Core IR, async evaluator, channel-based coordination |
| [data-access.md](data-access.md) | `Query a` typeclass, levels of control (ESQL/ShardPlan/LuceneM), use cases, comparable systems |
| [project-structure.md](project-structure.md) | File layout and what each module/file does |
| [decisions.md](decisions.md) | Key architectural decisions and their rationale |
| [references.md](references.md) | π-calculus papers, textbooks, implemented languages, and theory |
| [../README.md](../README.md) | Build, test, and manual-testing commands |

## Rules of Engagement for Agents

1. **Read `current-state.md` before proposing work.** It lists what's implemented, what's not, and
   known shortcuts. Avoid duplicating effort or re-solving decided questions.

2. **Check `decisions.md` before suggesting alternatives.** Many design choices (type system flavor,
   IR representation, null semantics, security model) were deliberated. If you want to revisit one,
   reference the existing decision and explain why.

3. **Check `roadmap.md` for phase boundaries.** Work should align with the current phase. If a
   feature belongs to a later phase, flag it as out-of-scope rather than implementing it.

4. **Keep these docs updated.** When you implement something, update `current-state.md` and
   `roadmap.md` status markers. When you make a non-trivial design decision, add it to
   `decisions.md`. These are living documents.

5. **Flag improvements proactively.** If you notice something that could be improved, contradicts
   the docs, or duplicates existing work, say so — referencing the relevant doc section.

## Key Design Constraints

- **Elasticsearch plugin**: Piescript lives inside the ES build system. It must follow ES
  conventions (formatting, testing, licensing, backwards compatibility).
- **ESQL dependency**: Piescript delegates data access to ESQL. It does not implement its own query
  engine. The `extendedPlugins = ['x-pack-esql']` relationship is intentional and permanent.
- **Phased delivery**: The language is built incrementally. Each phase is self-contained and
  testable. Do not jump ahead.
- **Type safety**: The language uses Hindley-Milner type inference with bidirectional checking.
  Types are inferred, not annotated. The type system is a core differentiator — do not compromise it
  for convenience.
- **Join Calculus coordination model**: `spawn` (async computation → channel), `when`
  (synchronize on channels). The keyword is `when` (not `join`) to avoid SQL/ESQL JOIN collision
  (D-041). Replaces the old plan graph / `par` architecture. See D-040.
- **Single-hierarchy Core IR**: `CoreExpr` includes coordination nodes (`CoreSpawn`, `CoreWhen`)
  alongside functional nodes. No separate `CoreProcess` hierarchy. D-013 is superseded by D-040.
- **Purity enables distribution**: The language is pure and referentially transparent. Closures can
  be shipped to remote nodes because captured values are immutable. See D-014.
- **Combinators are prelude built-ins**: `map`, `filter`, `reduce` are normal polymorphic functions,
  not Core IR nodes. They operate over materialized `ListVal` (renamed from `StreamVal` in
  Block B — D-043). This prepares for typeclasses (`map` → `Functor.fmap`). See D-016.
- **Channels backed by ES infrastructure**: `SubscribableListener<Value>` for single-value channels,
  positional collector (`AtomicArray` + `CountDown`) for `when` synchronization. See D-040, D-041.

## Coding Guidelines

These apply to all piescript-specific code (under `x-pack/plugin/piescript/`), on top of the
repo-wide conventions in the root `AGENTS.md`.

### Optional and Result over null for "find" operations

When a method tries to **find**, **look up**, or **resolve** something and there is a legitimate
chance the value is not there, the return type should communicate that branch:
- `Optional<T>` when absence is a normal outcome the caller must handle (e.g., `lookup(name)`
  returning empty for an unbound variable).
- A `Result`/`Either`-style sealed type when absence is expected but represents an error that
  carries context (e.g., a type error with a source location).

This is **not** a blanket "no nulls" rule. Nullable fields (e.g., `@Nullable String debugName` on
Core IR nodes) are perfectly fine — they represent genuinely optional metadata, not a "find"
operation that might fail. Use `@Nullable` with documentation for such fields.

**Elasticsearch framework conventions** that use `null` (e.g., `ActionRequestValidationException`
returning `null` for "no error", REST handler parsing loops) remain as-is. ANTLR-generated code
is also exempt.

### Prefer immutability

Design data structures as immutable by default. Mutable state should be:
1. Explicitly isolated (e.g., a dedicated class whose name signals mutability).
2. Minimal — only what genuinely requires shared mutation (e.g., the unification zonker, metavar
   supply counter).
3. Never mixed with immutable context. If a recursive descent carries both immutable context and
   mutable state, they should be separate parameters.

### Declarative, composable style

Prefer `Optional` pipelines (`map`, `flatMap`, `filter`, `orElse`) and `switch` expressions over
imperative if-chains and null checks. This applies to any branching over sealed hierarchies or
optional values. For example:

```java
// Preferred: pipeline + switch expression
return Optional.ofNullable(zonker.get(metaId)).flatMap(solution -> switch (solution) {
    case MonoType.Meta next when isSolved(next.id()) -> resolve(next.id());
    default -> Optional.of(solution);
});

// Avoid: imperative null check + instanceof chain
Object solution = zonker.get(metaId);
if (solution == null) return Optional.empty();
if (solution instanceof MonoType.Meta next && ...) return resolve(next.id());
return Optional.of(solution);
```

### Trust framework-managed resource lifecycles

Do not manually close, `decRef`, or wrap in `try-with-resources` any object received inside an
`ActionListener` callback from `client.execute(...)` or similar transport actions. The ES transport
framework owns the lifecycle of these responses (via `respondAndRelease`) and releases them after
your listener returns. Adding your own close is a **double-close** that triggers
`AssertionError: invalid decRef call: already closed` and kills the JVM.

More generally: if you find yourself writing defensive resource cleanup inside a framework-managed
async callback, question whether the framework already handles it. A well-designed async API does
not require callers to manually close resources it delivered — that would be a leaky abstraction.
If the pattern looks like "wrap in try-with-resources just in case", it's almost certainly wrong.

### Type safety over `Object`

Prefer sealed interfaces and pattern matching over `Object` casts and `instanceof` chains. When a
container must hold heterogeneous types (e.g., the zonker maps meta IDs to either `MonoType` or
`RowType`), document the invariant and consider a sealed wrapper.

## Implementation Plans

Detailed step-by-step implementation plans live in [../.cursor/plans/](../.cursor/plans/). These
were produced during each block/phase and contain granular task breakdowns, design rationale, and
completion status. Load the relevant plan when working on or extending a specific block:

| Plan file | Scope |
|-----------|-------|
| `scripting_language_design_9286506e` | Overall language design |
| `phase0_plugin_scaffold` | Phase 0: plugin scaffold |
| `phase1_expression_language` | Phase 1: expression language |
| `phase2_row_types` / `phase2_implementation` | Phase 2: row types and implementation |
| `phase_1d_open_rows_bb2dae6a` | Phase 1d: open rows |
| `block_a_implementation_2fdbab36` | Block A: spawn + single-value when |
| `block_c_cross-node_execution_7faf2b07` | Block C: cross-node execution |
| `block_d_local_data_ca4b90be` | Block D: local data access |
| `block_e_write_primitives_f1e74ffb` | Block E: write primitives |
| `block_f_linq_query_e7171607` | Block F: T-LINQ ESQL query compilation (NbE Symbol-based) |
| `f-omega_type_system_09acfb27` | F-omega type system: kinds-as-types, `force` normalizer, `&`/`Pick`/`Omit`, `ESQL.stats` |
| `compute_engine_streaming_f5db78f2` | Block G: compute engine streaming (Pages, Exchange, materialization) |

## Chat History Reference

Prior design discussions are preserved in agent transcripts:

- **Phase 0 scaffold**: `b0ac3e4f-db5e-4a03-85ec-a3016912512c` — plugin structure, security model,
  REST/transport patterns, ESQL integration.
- **Phase 1 expression language**: `3cd2a822-792c-4179-a00e-0ba98b875f52` — typing rules
  (bidirectional HM), de Bruijn indices, zonker-based elaboration, parser grammar, null semantics,
  literal alignment with ESQL DataType.
- **Distributed computation & π-calculus**: `6c10d690-5758-49da-88f5-4c38f2f9cd72` — plan graph
  architecture (free monad over π effects), two-layer IR (CoreExpr/CoreProcess), evaluator/planner
  split, traveling closures, mobility check, join calculus influence, code-as-data model, the IO
  monad / delimited continuations analogy. Stream combinators as prelude built-ins (D-016), stream
  fan-out via DAG (D-017), linearity roadmap with QTT for channels (D-018), BEAM/Erlang
  comparison, speculative ownership model. Resulted in D-012 through D-018 and reframing of
  Phases 3–6.
- **System F Core IR**: `8f5cc3a8-4c26-4f71-8fb0-1ea3c17f527b` — explicit CoreTypeAbs/CoreTypeApp
  nodes (D-035), type annotations as TypeScheme (D-034), deferred constraint solving, generalize
  and instantiate refactoring.
- **Bidirectional elaborator**: `3308f68e-e239-4a60-912c-47cfba6eabcc` — discovery of missing
  check mode (D-036), implementation of bidirectional checking for lambdas/records/let/blocks,
  extraction of Polymorphism.java and Applications.java, environment-carrying instantiation
  proposal (D-037), method renaming in TypeAnnotations.
- **Bidir refinements & D-038**: `303bcf3e-9eef-4719-a47d-24c1ff27a675` — test fixes, polytype
  ascription bug discovery (CoreTypeAbs cannot express its own type), MonoType→Type with Forall
  variant decision (D-038), TypeScheme retained for future qualified types.
- **Join Calculus redesign**: `f54fd3b6-dcf8-4af9-9af0-6a33818de6ef` — critical re-evaluation of
  Phase 3 plan graph architecture. Analysis of Join Calculus (Fournet & Gonthier) and π-calculus
  (Sangiorgi). Redesign: `spawn`/`when`/channels replace `par`/plan graph. Mapping to ES
  infrastructure (`SubscribableListener`, positional collector, `threadPool.executor(GENERIC)`).
  Multi-value channels, ESQL Exchange analysis. New Block-based phasing (A–E). D-040 decision.
- **F-omega type system design**: `846bd5a8-3b35-4321-848a-c9b17a22f109` (Cursor) — kinds-as-types
  (GHC TypeInType-style), `force` NbE normalizer, `&`/`Pick`/`Omit` row operators, `ESQL.stats`/
  `ESQL.statsBy` type design, aggregate builtins with plain output types, closure-based
  `ESQL.keep`/`ESQL.drop`. Resulted in D-053, Phases 1–2 implementation.
- **F-omega implementation (Phases 3–5)**: Claude Code session 2026-03-24 — `Pick`/`Omit`
  reduction rules in `force`, closure-based `ESQL.keep`/`ESQL.drop` NbE compilation,
  `ESQL.stats`/`ESQL.statsBy`/aggregate builtins implementation, tests, docs, debug scripts.
  Completed all 5 phases of the F-omega plan.
- **Block H design (multi-value fields)**: Claude Code session 2026-03-25 — MV-as-default value
  model (APL scalar pervasion), cartesian product for MV×MV (ESQL semantics), `Single a` boxing,
  `MV.*` rank-reducing builtins, user-controlled read/materialization boundary (ties into Block G).
  Design only — not yet implemented.
- **Block G implementation (streaming data access)**: Claude Code session 2026-03-26 —
  `Shard.stream` via compute Block builders, `Page.toList`/`Page.count` materialization,
  `PageVal`/`ExchangeSinkVal`/`ExchangeSourceVal` value types, `Page`/`Sink`/`Source`/`Exchange`
  type constructors. Exchange API design: `Exchange r` as serializable descriptor,
  `Exchange.open`/`sink`/`connect`/`addPage`/`poll`/`finish`, callback-based `poll`, unified
  local/remote via `ExchangeService`. D-054 decision record. Column name gap identified
  (runtime `List Keyword` not statically verifiable against row type `r`).
  Continued 2026-03-30: `ESQL.top`/`ESQL.values` MV-returning aggregates with type-driven
  materialization. `force` function threaded to evaluator via `EvalDependencies`. Risk score
  query pattern now works end-to-end. `BooleanBlock` fix for Shard.stream. `Shard.write`
  upsert fix (`autoGeneratedTimestamp = -1`). `ExchangeService` integration (replaced local
  registry). `Task` threading for exchange child requests.
