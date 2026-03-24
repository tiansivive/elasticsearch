# Piescript Plugin — Claude Code Context

Start with [docs/AGENTS.md](docs/AGENTS.md) for full project context, design constraints, and coding guidelines.

## Essential Reading

Before doing any work, load these into context:

- [docs/current-state.md](docs/current-state.md) — what's implemented now, known limitations
- [docs/roadmap.md](docs/roadmap.md) — block-based development plan with status markers
- [docs/decisions.md](docs/decisions.md) — architectural decisions and rationale (check before suggesting alternatives)
- [docs/architecture.md](docs/architecture.md) — system design, Core IR, async evaluator, channels

## Additional Documentation

- [docs/vision.md](docs/vision.md) — long-term goals, Join Calculus coordination model, design philosophy
- [docs/data-access.md](docs/data-access.md) — `Query a` typeclass, ESQL/ShardPlan/LuceneM levels
- [docs/project-structure.md](docs/project-structure.md) — file layout and module responsibilities
- [docs/references.md](docs/references.md) — papers, textbooks, and theory
- [docs/mvp.md](docs/mvp.md) — MVP definition

## Implementation Plans

Detailed step-by-step plans live in [.cursor/plans/](.cursor/plans/):

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
| `compute_engine_streaming_f5db78f2` | Compute engine streaming |

## Build & Test

```bash
./gradlew :x-pack:plugin:piescript:javaRestTest    # run tests
./gradlew :x-pack:plugin:piescript:compileJava      # compile only
./gradlew :x-pack:plugin:piescript:check            # precommit checks
```
