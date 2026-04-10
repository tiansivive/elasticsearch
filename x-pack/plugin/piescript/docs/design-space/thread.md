# Thread

Append-only paper trail of design and implementation work. Each block records a
session's path through the zettel graph — what was explored, what was decided,
what was deferred.

Format: edge lines (`[[A]] -- verb -> [[B]]`) and action lines (`ENQUEUE`, `RESOLVED`, `SPAWN`).
See [[thread-queue-system.meta]] for the full system design.

---

## session:4e5e689a — 2026-04-08 [design-space, meta, queues, threads]

Created the design space knowledge base (216 zettels, catalog script, tag taxonomy).
Then designed the thread & queue system itself.

[[tags-as-triples.meta]] -- inspired -> [[thread-queue-system.meta]]
[[channel-registry.infrastructure]] -- analogous_to -> [[thread-queue-system.meta]]
[[join-calculus.coordination]] -- synchronization_model_for -> [[thread-queue-system.meta]]

Queue system design: zettels as atoms, threads as graph paths, queues as FIFO pending
lists. Threads log edges and actions (ENQUEUE/RESOLVED/SPAWN). Queues synchronize
threads like channels synchronize spawned computations.

Cross-domain connections surfaced: event sourcing (action log as source of truth),
RDF triples (edge syntax), Luhmann's fleeting-notes box, org-mode capture/refile,
GTD methodology, Roam/Logseq daily notes.

ENQUEUE [[recursion.language]] — design recursion mechanism
ENQUEUE [[pattern-matching.language]] — Phase 1e, deferred twice
ENQUEUE [[recursive-types.types]] — iso-recursive vs equi-recursive
ENQUEUE [[runtime-dispatch.types]] — runtime polymorphism design
ENQUEUE [[type-narrowing.types]] — TypeScript-style refinement
ENQUEUE [[keyword-string.types]] — Keyword vs String unification
ENQUEUE [[datetime.types]] — DateTime/IP/GeoPoint handling
ENQUEUE [[numeric-precision.types]] — primitive type review
ENQUEUE [[transport-channels.infrastructure]] — ES transport as native channels
ENQUEUE [[string-concat.language]] — list/string concat operators

---

## session:es-internals-gap — 2026-04-09 [design-space, es-internals, reviews]

Ran Phase 1–2 of the ES-internals gap program; backlog and priorities live in
[[reviews/es-internals-gaps.md]]. Closed initial slices: [[doc-values.es-internals]] (refs +
edges), [[blockloader.data]] + [[circuit-breaker.infrastructure]] (ES `code:` + [[compute-engine.es]]),
[[transport-send.infrastructure]] + [[channel-registry.infrastructure]] (`TransportService`, mutual
edges). Added `python3 scripts/catalog.py --es-code-gaps` triage hint.

[[doc-values.es-internals]] -- informs -> [[compute-engine.es]]
[[blockloader.data]] -- informs -> [[compute-data-page-block.es]]

ENQUEUE [[lucene-segments.es-internals]] — add ES-tree `code:` refs + link read path cluster
ENQUEUE [[security-namespace.infrastructure]] — add server/x-pack `code:` anchors beyond piescript actions
ENQUEUE [[lucene-collectors.es-internals]] — same (cluster 5)

RESOLVED [[lucene-segments.es-internals]] — `Engine.java`, Lucene `SegmentInfos` resource; [[block-d.roadmap]] edge
RESOLVED [[security-namespace.infrastructure]] — RBAC + operator + REST/plugin refs (full `code:` paths)
RESOLVED [[lucene-collectors.es-internals]] — `QueryPhase` + ESQL Lucene operators; [[compute-engine.es]] edge
RESOLVED [[transport-channels.infrastructure]] — relationship to [[transport-layer.es]] documented; queue item closed

[[lucene-segments.es-internals]] -- informs -> [[block-d.roadmap]]
[[lucene-collectors.es-internals]] -- informs -> [[compute-engine.es]]
[[transport-channels.infrastructure]] -- contrasted_with -> [[transport-layer.es]] — implemented transport + registry stack vs speculative transport-only channels

---

## session:extraction-scripts — 2026-04-09 [design-space, scripts, tooling]

Created `scripts/` directory with shared zettel parsing library (PyYAML + Rich),
migrated `catalog.py` from `docs/design-space/`, and built four extraction scripts:
`tech_debt.py`, `roadmap_status.py`, `adr_index.py`, `vision_coverage.py`.

`scripts/lib/zettel.py` — shared parsing: PyYAML frontmatter, body regex (title,
description, Depends on / Enables / Connections edges), dependency graph builders,
tag/ref filters.

`scripts/lib/adr.py` — `decisions.md` parser: splits on `## D-NNN:` boundaries,
handles format variations (Phase line optional, casing, bold vs heading sections).

All scripts support dual output: Rich terminal tables by default, `--markdown` for
plain text. `pyproject.toml` declares pyyaml + rich as dependencies.

[[thread-queue-system.meta]] -- instantiated_by -> scripts/tech_debt.py, scripts/roadmap_status.py — queue items resolved via script automation

RESOLVED Tech-debt extraction script — `scripts/tech_debt.py`
RESOLVED Roadmap/ADR/vision generation scripts — `scripts/roadmap_status.py`, `scripts/adr_index.py`, `scripts/vision_coverage.py`

---

## session:thread-roadmap-restructure — 2026-04-09 [design-space, roadmap, threads, meta]

Replaced the 960-line monolithic `roadmap.md` with a thread-based system.
Five parallel work concerns, each a hub zettel tagged `thread` with ordered
sequences, `includes` edges for membership, and priority/readiness tags.

Design discussion: roadmap was 80%+ completed work, mixed concerns, redundant
with design space zettels. Error handling identified as broader than ADTs +
pattern matching — full spectrum from source locations to OTP supervision.
Thread-based structure captures parallel independent concerns with sequencing
and dependency annotations per thread.

New index.md tag groups: Structure (`thread`, `queue`, `hub`, `paper-trail`,
`note`) and Priority (`now`, `next`, `later`, `someday`, `ready`, `blocked`,
`needs-design`). `adr` added as alias of `decision`. `includes` edge verb
for thread membership. `thread:` and `queue:` ref prefixes.

[[roadmap-hub.roadmap]] -- replaced_by -> [[error-handling.thread]], [[language-expressiveness.thread]], [[data-completeness.thread]], [[distributed-coordination.thread]], [[type-foundations.thread]]
[[thread-queue-system.meta]] -- extended_by -> thread-based roadmap (Structure tags, thread hubs)

SPAWN [[error-handling.thread]] — error provenance to OTP supervision
SPAWN [[language-expressiveness.thread]] — recursion to module system
SPAWN [[data-completeness.thread]] — Block G tests to numeric precision
SPAWN [[distributed-coordination.thread]] — MV channels to long-lived computations
SPAWN [[type-foundations.thread]] — Forall type to session types
SPAWN [[empty-mapping-diagnostics.data]] — new zettel for field caps diagnostic gap
SPAWN [[global-pending.queue]] — queue migrated from queue.md to zettel

RESOLVED "Revise roadmap.md" — roadmap archived to `docs/archive/roadmap.pre-threads.md`, replaced by thread system
RESOLVED Queue items extracted into threads: [[recursion.language]], [[pattern-matching.language]], [[recursive-types.types]], [[runtime-dispatch.types]], [[type-narrowing.types]], [[keyword-string.types]], [[datetime.types]], [[numeric-precision.types]], [[string-concat.language]]

~50 member zettels updated with `thread:` refs and priority tags.
`roadmap_status.py` rewritten for thread-based reporting.
`index.md`, `AGENTS.md`, `CLAUDE.md`, `current-state.md` updated.

---

## session:thread-roadmap-continued — 2026-04-09 [design-space, threads, vision, meta]

Extended the thread system with 2 additional threads, design principle zettels,
ML workflow zettels, and vision.md trim.

SPAWN [[external-interaction.thread]] — plugin SPI to Transform/Watcher unification
SPAWN [[ownership-resources.thread]] — ownership to incremental computation

[[external-interaction-model.roadmap]] -- superseded_by -> [[external-interaction.thread]]
[[type-foundations.thread]] -- related -> [[ownership-resources.thread]]
[[distributed-coordination.thread]] -- related -> [[external-interaction.thread]]

Design principles extracted from vision.md into atomic zettels:
SPAWN [[design-principles.hub]] — hub for the 6 foundational constraints
SPAWN [[inferred-types.principle]] — types inferred, not annotated
SPAWN [[esql-data-layer.principle]] — ESQL is the data layer
SPAWN [[functional-distributed.principle]] — functional by default, distributed by design
SPAWN [[incremental-delivery.principle]] — phased, self-contained delivery
SPAWN [[es-native.principle]] — ES conventions, deep integration
[[join-calculus.coordination]] -- part_of -> [[design-principles.hub]]

ML workflow zettels split from vision.md brainstorming:
SPAWN [[model-evaluation.ml]] — precision/recall/F1 via shard-local folds
SPAWN [[data-preparation.ml]] — sampling, normalization, train/test splits
SPAWN [[inference-orchestration.ml]] — fan out to ML nodes
SPAWN [[ml-non-goals.ml]] — what piescript does NOT do (GPUs, training, tensors)
SPAWN [[es-ml-integration.ml]] — topology + send to ML nodes
[[ml-workflow-integration.ml]] -- promoted_to -> hub (includes edges added)

SPAWN [[risk-scoring-incremental.example]] — full actor + Kafka + SSE example

New tags: `principle`, `example` (added to Purpose group in index.md).

vision.md trimmed from 791 to 192 lines. Kept: identity, motivation,
distributed computation model, design philosophy (now linking to principle
zettels), what piescript is not. Replaced: long-term aspirations, data access
architecture, speculative sections, ML workflows, external interaction model
— all now in zettels and thread hubs.

RESOLVED "Revise vision.md" — speculative sections moved to zettels

## session:block-g-integration-tests — 2026-04-10 [data-completeness, infrastructure, testing]

Implemented the missing integration tests for Block G Exchange streaming.
Discovered and fixed a bug in `EvalExchange.java` where `Exchange.connect` was hardcoded to use `getLocalNodeConnection()`, making cross-node exchange impossible.
Updated `ExchangeVal` to include the producer's `nodeId` so the consumer can establish a remote transport connection to the correct node.
Added `testLocalExchangeStreaming` to `PiescriptIT.java` and `testRemoteExchangeStreaming` to `PiescriptMultiNodeIT.java`.

[[block-g.roadmap]] -- validates -> [[exchange-streaming.infrastructure]]
[[exchange-streaming.infrastructure]] -- implements -> [[exchange-remote-testing.infrastructure]]

RESOLVED [[exchange-remote-testing.infrastructure]] — added cross-node tests
RESOLVED Block G integration tests — updated `data-completeness.thread.md`
