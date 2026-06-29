# Piescript

Typed functional language for distributed computation in Elasticsearch. Join Calculus
coordination primitives (`spawn`, `when`, `send`, channels) orchestrate asynchronous pipelines
that run where the data lives; pure functional expressions (lambdas, records, pattern matching,
recursion) evaluate locally; data access delegates to ESQL and shard-level primitives.

## Documentation

| Doc | Purpose |
|-----|---------|
| [`docs/AGENTS.md`](docs/AGENTS.md) | Agent entry point — start here for full project context |
| [`docs/vision.md`](docs/vision.md) | Long-term goals and design philosophy |
| [`docs/decisions.md`](docs/decisions.md) | Architectural decision records (D-001+) |
| [`docs/demos/`](docs/demos/) | Presentation narrative and annotated use-case examples |

Most design knowledge — architecture, current state, file layout, references, roadmap — now
lives in the **z-piescript** knowledge base described below, not in flat docs.

## Design-space knowledge base (`docs/z-piescript/`)

The project's design knowledge — architectural exploration, the forward-looking roadmap,
references, and worked examples — lives in **z-piescript**, a standalone zettelkasten kept in
its own external repository. Its primary mode of interaction is through an AI coding agent: the
`load` and `zettelkasten` skills (see *Workflow Setup*) walk and write the knowledge base, and
the scripts below give structured read-only views. It is intentionally a separate, gitignored
repo — clone it into `docs/z-piescript/`:

```bash
# from x-pack/plugin/piescript/
git clone https://github.com/tiansivive/z-piescript.git docs/z-piescript
```

The scripts require `pyyaml` and `rich` (`pip3 install --user pyyaml rich`):

```bash
python3 docs/z-piescript/scripts/catalog.py --compact     # all tracked design topics
python3 docs/z-piescript/scripts/queue.py                 # pending work items
python3 docs/z-piescript/scripts/roadmap_status.py        # forward-looking roadmap (thread hubs)
python3 docs/z-piescript/scripts/adr_index.py             # architectural-decision index
python3 docs/z-piescript/scripts/references.py            # regenerate references from paper zettels
```

## Workflow Setup

Assumes you already build and run Elasticsearch (see the repo root `BUILDING.md`) and have
cloned the knowledge base (see *Design-space knowledge base* above).

**Agent tooling.** Open your agent with **this directory** (`x-pack/plugin/piescript/`) as
the working directory — not the repo root — so the project context loads:

- **Claude Code**: `CLAUDE.md`, the skills in [`.claude/skills/`](.claude/skills/) (`load`,
  `zettelkasten`, `create-plan`), and a session-start hook that reminds you to run `/load`.
  Always run `/load` before doing any work — it walks the full project context.
- **Cursor**: the same skills via [`.cursor/skills/`](.cursor/skills/) symlinks, plus
  [`.cursor/rules/agent-interaction.mdc`](.cursor/rules/agent-interaction.mdc) (always applied).

Implementation plans live in [`.cursor/plans/`](.cursor/plans/); the plan workflow is the
`create-plan` skill, knowledge-base writes follow the `zettelkasten` skill.

## Build & Test

```bash
./gradlew :x-pack:plugin:piescript:javaRestTest      # integration tests
./gradlew :x-pack:plugin:piescript:compileJava       # compile only
./gradlew :x-pack:plugin:piescript:check             # precommit checks + tests
./gradlew :x-pack:plugin:piescript:spotlessApply     # auto-fix formatting
```

Gradle handles incremental upstream builds — you never need to build all of Elasticsearch
explicitly.

## Manual Testing

Start a local cluster with a trial license (includes piescript):

```bash
./gradlew run -Drun.license_type=trial
```

Elasticsearch comes up on `localhost:9200` with security enabled (`elastic-admin` /
`elastic-password`; disable with `-Dtests.es.xpack.security.enabled=false`).

```bash
# evaluate an expression
curl -u elastic-admin:elastic-password -X POST "localhost:9200/_piescript/eval" \
  -H "Content-Type: application/json" \
  -d '{"program": "let double = fn x -> x * 2 in double 21"}'
# -> {"type":"Double","result":42}
```

The [`debug/`](debug/) scripts cover the full surface — typed ESQL queries (`query ESQL.from
idx |> ... ;`), `spawn`/`when` coordination, cross-node closure shipping, shard reads/writes,
pattern matching, recursion: `debug/test-eval.sh`, `debug/test-dev.sh` (CST/Core IR/type
debugging via `POST /_piescript/dev`), and `debug/test-multinode.sh` (see
[`debug/README.md`](debug/README.md) for cluster setup).

See [`docs/current-state.md`](docs/current-state.md) for capabilities and known limitations.
