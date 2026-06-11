# Piescript Plugin — Claude Code Context

## MANDATORY: Read Before Any Work

You MUST read the following files before doing any work on this plugin. Read them in order,
and follow any links they contain recursively until you have a complete picture of the project.

1. **[docs/AGENTS.md](docs/AGENTS.md)** — project context, design constraints, coding guidelines, chat history, design space instructions
2. **[docs/current-state.md](docs/current-state.md)** — what's implemented now, known limitations
3. **[docs/architecture.md](docs/architecture.md)** — system design, Core IR, async evaluator, channels
4. **[docs/decisions.md](docs/decisions.md)** — architectural decisions and rationale (55 ADRs — check before suggesting alternatives)
5. **Knowledge base entry points** — [docs/z-piescript/README.md](docs/z-piescript/README.md) (model, zettel format, ref prefixes), [docs/z-piescript/VOCABULARY.md](docs/z-piescript/VOCABULARY.md) (tag and edge vocabulary), [docs/z-piescript/WORKFLOW.md](docs/z-piescript/WORKFLOW.md) (threads, queues, paper trail)
6. **Meta zettels** — read ALL zettels tagged `meta` (knowledge-base conventions, design-to-implementation pipeline). Run `python3 docs/z-piescript/scripts/catalog.py meta` to find them.
7. **Thread hub zettels** — forward-looking roadmap via `thread`-tagged zettels; run `python3 docs/z-piescript/scripts/roadmap_status.py` to see all threads (old roadmap archived at [docs/archive/roadmap.pre-threads.md](docs/archive/roadmap.pre-threads.md))
8. **Design space catalog** — run `python3 docs/z-piescript/scripts/catalog.py --compact` to scan all tracked design topics
9. **Thread** — read `docs/z-piescript/thread.md` for the paper trail of prior work
10. **Queue** — run `python3 docs/z-piescript/scripts/queue.py` for all pending items, or check `docs/z-piescript/zettels/global-pending.queue.md` directly
11. **Procedure skills (canonical)** — the skills under `.claude/skills/` are the single source of truth for process; meta zettels keep the rationale and link to them via `skill:` refs. Use the **`zettelkasten`** skill (`.claude/skills/zettelkasten/SKILL.md`) when creating, updating, or connecting zettels, or recording thread/queue actions. Use the **`create-plan`** skill (`.claude/skills/create-plan/SKILL.md`) before creating or executing work from `.cursor/plans/` (zettels, queue, thread, session zettel, debug scripts, docs, review stops; at plan end reconcile the zettelkasten to shipped code/docs and confirm any new zettels with the user). Cursor agents reach the same files via `.cursor/skills/*/SKILL.md` symlinks.

If any of these files link to other documents (vision, data-access, references, plans, etc.),
read those too. The goal is to have the full project context before making any changes or
suggestions. Do not skip this step. Do not assume you know the project from a prior session.

**Design space lookups are mandatory.** Before proposing or implementing any design change,
search the catalog for related topics and read the relevant zettels. Follow their `Depends on`,
`Enables`, and `Connections` edges to understand the full context. Pay attention to `superseded`
tags and `supersedes`/`rejected-in-favor-of` edges — these mark ideas that were already
considered and deliberately dropped. Don't revisit settled decisions without new justification.
See [docs/AGENTS.md § Design Space Knowledge Base](docs/AGENTS.md) for the full workflow.

## Interaction with the user

Same rules as [docs/AGENTS.md § Interaction with the user](docs/AGENTS.md) and
`.cursor/rules/agent-interaction.mdc`: for **questions or process feedback**, answer in prose only—**no
repo edits** unless the user explicitly asks you to apply a change. **Confirm** before substantive
edits; **ask** when intent is unclear; **stop** on conflicting instructions instead of silently
“fixing.”

## Additional Documentation

- [docs/vision.md](docs/vision.md) — long-term goals, Join Calculus coordination model, external interaction model, design philosophy
- [docs/archive/data-access.pre-threads.md](docs/archive/data-access.pre-threads.md) — `Query a` typeclass, ESQL/ShardPlan/LuceneM levels (archived — see [[data-access-architecture.roadmap]])
- [docs/project-structure.md](docs/project-structure.md) — file layout and module responsibilities
- [docs/references.md](docs/references.md) — papers, textbooks, and theory
- [docs/z-piescript/metrics.md](docs/z-piescript/metrics.md) — derived metrics for the design space
- [docs/archive/mvp.md](docs/archive/mvp.md) — MVP definition (archived — MVP complete 2026-04-06)

## Implementation Plans

Detailed step-by-step plans live in [.cursor/plans/](.cursor/plans/). **Canonical workflow**: the
**create-plan skill** at [.claude/skills/create-plan/SKILL.md](.claude/skills/create-plan/SKILL.md)
(Cursor symlink: [.cursor/skills/create-plan/SKILL.md](.cursor/skills/create-plan/SKILL.md)).
**Template**: [.cursor/plans/_TEMPLATE.plan.md](.cursor/plans/_TEMPLATE.plan.md). Rationale and
design history live in [[implementation-plan-workflow.meta]] and [[cursor-plan-template.meta]].

| Plan | Scope |
|------|-------|
| `scripting_language_design_9286506e` | Overall language design |
| `phase0_plugin_scaffold` | Phase 0: plugin scaffold |
| `phase1_expression_language` | Phase 1: expression language |
| `phase2_row_types` | Phase 2: row types |
| `phase2_implementation` | Phase 2: implementation details |
| `phase_1d_open_rows_bb2dae6a` | Phase 1d: open rows |
| `block_a_implementation_2fdbab36` | Block A: spawn + single-value when |
| `block_c_cross-node_execution_7faf2b07` | Block C: cross-node execution |
| `block_d_local_data_ca4b90be` | Block D: local data access |
| `block_e_write_primitives_f1e74ffb` | Block E: write primitives |
| `block_f_linq_query_e7171607` | Block F: T-LINQ ESQL query compilation (NbE Symbol-based) |
| `f-omega_type_system_09acfb27` | F-omega type system: kinds-as-types, `force` normalizer, `&`/`Pick`/`Omit`, `ESQL.stats` |
| `compute_engine_streaming_f5db78f2` | Block G: compute engine streaming (Pages, Exchange, materialization) |
| `compute_engine_zettels_8b517c82` | Block G follow-up zettels |
| `pattern_matching_phase_1_6fd754ed` | Pattern matching Phase 1 (literals, vars, wildcards, records, lists) |
| `recursion_phase1` | Implicit recursion + fused `loop`/`repeat` |

## Build & Test

```bash
./gradlew :x-pack:plugin:piescript:javaRestTest    # run tests
./gradlew :x-pack:plugin:piescript:compileJava      # compile only
./gradlew :x-pack:plugin:piescript:check            # precommit checks
```
