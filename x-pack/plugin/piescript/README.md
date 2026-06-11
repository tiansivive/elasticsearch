# Piescript

Typed functional language for distributed computation in Elasticsearch. Join Calculus
coordination primitives (`spawn`, `when`, `send`, channels) orchestrate asynchronous pipelines
that run where the data lives; pure functional expressions (lambdas, records, pattern matching,
recursion) evaluate locally; data access delegates to ESQL and shard-level primitives.

## Documentation

| Doc | Purpose |
|-----|---------|
| [`docs/AGENTS.md`](docs/AGENTS.md) | Agent entry point — start here for full project context |
| [`docs/current-state.md`](docs/current-state.md) | What's implemented now, limitations, next steps |
| [`docs/architecture.md`](docs/architecture.md) | System design, components, data flow |
| [`docs/vision.md`](docs/vision.md) | Long-term goals and design philosophy |
| [`docs/decisions.md`](docs/decisions.md) | Architectural decision records (D-001+) |
| [`docs/project-structure.md`](docs/project-structure.md) | File layout and module responsibilities |
| [`docs/references.md`](docs/references.md) | Papers, textbooks, and theory |
| [`docs/z-piescript/`](docs/z-piescript/) | Design-space zettelkasten (separate repo — see setup below) |

The forward-looking roadmap lives in thread hub zettels inside the zettelkasten:
`python3 docs/z-piescript/scripts/roadmap_status.py`.

## Workflow Setup

Assumes you already build and run Elasticsearch (see the repo root `BUILDING.md`). The
piescript-specific pieces:

**1. Clone the design-space knowledge base** into its nested location (the path is gitignored
here — it is a standalone repo in the [z-loom](https://github.com/tiansivive/z-loom) federation):

```bash
# from x-pack/plugin/piescript/
git clone https://github.com/tiansivive/z-piescript.git docs/z-piescript
```

**2. Install the script dependencies** (catalog, queue, roadmap, ADR-index views):

```bash
pip3 install --user pyyaml rich
# sanity check:
python3 docs/z-piescript/scripts/catalog.py --compact | head
```

**3. Agent tooling.** Open your agent with **this directory** (`x-pack/plugin/piescript/`) as
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
