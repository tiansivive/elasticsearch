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
| [roadmap.md](roadmap.md) | Block-based development plan with status markers (Blocks A–E replace old Phases 3–5) |
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
