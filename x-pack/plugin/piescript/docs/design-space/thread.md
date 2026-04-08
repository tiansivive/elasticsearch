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
