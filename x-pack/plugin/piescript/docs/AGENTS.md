# Piescript — Agent Context

> **Purpose**: This file is the primary entry point for AI agents working on the piescript plugin.
> Read this first, then follow links to detailed docs as needed.

## What is Piescript?

Piescript is a **typed functional language for distributed computation** in Elasticsearch. It uses
π-calculus process primitives to orchestrate data pipelines that run where the data lives. Pure
functional expressions (lambdas, let-bindings, records) evaluate locally; process primitives
(queries, parallel composition, stream transforms) produce a **plan graph** that is optimized and
dispatched by the executor. User-defined functions travel to data nodes as closures — safe because
the language is pure and referentially transparent.

## Quick Orientation

| Doc | What it covers |
|-----|---------------|
| [vision.md](vision.md) | Long-term goals, distributed computation model, design philosophy, non-goals |
| [roadmap.md](roadmap.md) | Phased development plan with status markers |
| [current-state.md](current-state.md) | What works **right now**, known limitations, immediate next steps |
| [architecture.md](architecture.md) | System design, two-layer IR, plan graph, evaluator/planner split |
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
- **Two-layer IR**: Core IR is split into `CoreExpr` (functional, evaluated directly) and
  `CoreProcess` (process effects, build plan graph nodes). Effects do not leak into the functional
  layer. See D-013.
- **Plan graph architecture**: Process primitives produce plan graph nodes, not runtime effects.
  The plan graph is optimized and then dispatched by the executor. This is the foundation for
  distributed execution. See D-012.
- **Purity enables distribution**: The language is pure and referentially transparent. Closures can
  be shipped to remote nodes because captured values are immutable. See D-014.

## Chat History Reference

Prior design discussions are preserved in agent transcripts:

- **Phase 0 scaffold**: `b0ac3e4f-db5e-4a03-85ec-a3016912512c` — plugin structure, security model,
  REST/transport patterns, ESQL integration.
- **Phase 1 expression language**: `3cd2a822-792c-4179-a00e-0ba98b875f52` — typing rules
  (bidirectional HM), de Bruijn indices, zonker-based elaboration, parser grammar, null semantics,
  literal alignment with ESQL DataType.
- **Distributed computation & π-calculus** — plan graph architecture (free monad over π effects),
  two-layer IR (CoreExpr/CoreProcess), evaluator/planner split, traveling closures, mobility check,
  join calculus influence, code-as-data model, the IO monad / delimited continuations analogy.
  Resulted in D-012 through D-015 and reframing of Phases 3–4.
