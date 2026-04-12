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

---

## session:pattern-matching-design — 2026-04-10 [language-expressiveness, error-handling, design]

Design discussion for pattern matching as the critical unblock for recursion.

Started from recursion — discovered the false dependency chain: D-010 tied `if/then/else`
to `match`, `match` was incorrectly listed as depending on ADTs, so recursion appeared
blocked by the full ADT + pattern matching stack. Fixed: basic pattern matching (Boolean,
literals, wildcards, records, lists) is independent of ADTs. Constructor patterns come
later with ADTs, but neither blocks the other.

Created [[pattern-matching.hub]] with 6 sub-zettels splitting the old monolithic
[[pattern-matching.language]] (now deleted):
- [[match-syntax.language]] — ML-style `match x | pat -> body`, `if/then/else` as sugar
- [[pattern-types.language]] — 7 pattern forms including record/list tail with `|`
- [[match-type-checking.language]] — elaboration algorithm via unification
- [[pattern-reuse.language]] — extending patterns to lambda/when/let (future sugar)
- [[type-level-matching.types]] — future type families generalizing `force`
- [[core-match.language]] — CoreMatch IR node + Pattern sealed hierarchy

Key design decisions:
- `|` for tails (records and lists), consistent with row type syntax and Erlang cons
- `...` reserved for record spread in expressions (new zettel: [[record-spread.language]])
- Record tail `| rest` binds a **record** at value level (rows are type-level only)
- Nested patterns are not a special case — Pattern hierarchy is recursive by definition
- No exhaustiveness checking in v1; runtime error if no match
- Pattern infrastructure designed for reuse across match/lambda/when/let

[[recursion.language]] -- blocked-by -> [[pattern-matching.hub]]
[[pattern-matching.hub]] -- complements -> [[adts.types]]
[[pattern-matching.hub]] -- enables -> [[recursion.language]]
[[pattern-matching.hub]] -- specializes -> [[curry-narrowing.language]]
[[pattern-matching.hub]] -- specializes -> [[cham-patterns.coordination]]
[[pattern-matching.hub]] -- enhances -> [[when-synchronization.coordination]]
[[pattern-matching.hub]] -- enhances -> [[currying.language]]
[[nbe-dual-pattern.types]] -- analogous-to -> [[type-level-matching.types]]

Created [[record-spread.language]] — `...` operator for record expression merging
Created [[design-to-implementation.meta]] — workflow: zettels → hub → plan → queue → ADR

Updated CLAUDE.md, AGENTS.md, /load skill — agents now read all `meta` zettels at session start.
Updated [[language-expressiveness.thread]] — pattern matching bumped to position 1 (priority `now`).
Updated [[error-handling.thread]] — pattern matching no longer depends on ADTs.
Added `enhances` verb to index.md edge vocabulary.
Updated 13 zettels to point from [[pattern-matching.language]] to [[pattern-matching.hub]].

SPAWN [[pattern-matching.hub]] — hub zettel with 6 sub-zettels
SPAWN [[record-spread.language]] — `...` spread operator for record expressions
SPAWN [[design-to-implementation.meta]] — workflow meta zettel

---

## session:data-access-restructure — 2026-04-10 [data, architecture, meta]

Archived the monolithic `data-access.md` document and restructured the data access design space into atomic zettels.

Promoted `data-access-architecture.roadmap` to a proper hub representing the full data access landscape (ESQL, physical, streaming, Query typeclass) rather than just the `Query a` vision. Extracted the equational vs sequential argument into `data-access-rationale` and the mermaid architecture into `data-access-diagram`.

Refined file naming guidelines in `index.md` to avoid redundant qualifiers (e.g., `data-access-hierarchy.md` instead of `data-access-hierarchy.data.md`) and clarified multiple qualifier usage.

[[data-access-architecture.roadmap]] -- includes -> [[data-access-rationale]], [[data-access-diagram]], [[data-access-hierarchy]]
[[data-access-rationale]] -- overlaps -> [[data-access-hierarchy]]

SPAWN [[data-access-rationale]] — equational vs sequential argument
SPAWN [[data-access-diagram]] — mermaid architecture diagram
SPAWN [[data-access-restructure.session]] — session zettel

RESOLVED Archive `data-access.md` to `docs/archive/data-access.pre-threads.md`

## session:5f90891b-2661-476c-b4fe-575b7d34ec22 — 2026-04-10 [language, control-flow, implementation]

Implemented Phase 1 of pattern matching. Added `match` expressions with `Alternative` arms and a sealed `Pattern` hierarchy (literal, variable, wildcard, open-row record, exact list, cons list). `if/then/else` is now sugar over Boolean `match`.

- **IR & Types**: `CoreMatch` and `Alternative` added. `Pattern` hierarchy implemented.
- **Elaborator**: `Matches.java` handles pattern type inference (unifying scrutinee type with pattern type) and `if` desugaring.
- **Evaluator**: `EvalMatch.java` implements top-to-bottom arm dispatch and recursive pattern matching. Record patterns bind fields in alphabetical order.
- **Serialization**: `TAG_MATCH` and recursive `Pattern` serialization added to `CoreExprSerialization`.
- **Debug Scripts**: Added `match` and `if/else` examples to `test-dev.sh`, `test-eval.sh`, and `test-multinode.sh`.

SPAWN [[pattern-matching-phase1.session]] — session zettel
RESOLVED [[pattern-matching.hub]] — Phase 1 complete (basic patterns, no ADTs, no exhaustiveness)
RESOLVED [[core-match.language]] — IR node implemented
RESOLVED [[match-syntax.language]] — ML-style syntax implemented
RESOLVED [[pattern-types.language]] — basic patterns implemented
RESOLVED [[match-type-checking.language]] — unification-based inference implemented
RESOLVED [[if-as-match-sugar.language]] — desugaring implemented

---