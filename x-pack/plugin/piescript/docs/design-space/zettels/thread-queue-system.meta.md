---
tags: [meta, tooling, designed, concept]
refs:
  - session:4e5e689a-7ddc-4839-9440-05e441be028b
---
# Thread & Queue System

A lightweight work layer on top of the zettelkasten. Zettels are the atomic knowledge
units; threads and queues are workflows over them.

- **Thread** (`docs/design-space/thread.md`): an append-only paper trail of work across sessions. Structured as delimited blocks per session, each recording a path through the zettel graph using labeled edges (`[[A]] -- verb -> [[B]]`) and action annotations (`ENQUEUE`, `RESOLVED`, `SPAWN`). Like the ADR file -- a historical record, not a process engine.
- **Queue** (`docs/design-space/queue.md`): a flat FIFO list of pending work. Each item references a zettel. Items are resolved top-down. Ephemeral in attention (once all resolved, nobody looks), persistent in existence (the file stays as record).
- **Queues synchronize threads** the way [[channels.infrastructure]] synchronize spawned computations in piescript. An `ENQUEUE` action in a thread defers work; a `RESOLVED` action records completion. Blocking (`BLOCKED` + `RESOLVED` pair) and non-blocking defers are both supported.

**Cross-domain parallels:**
- Event sourcing: the action log is the source of truth; state is derived
- RDF triples: edge syntax `[[A]] -- verb -> [[B]]` is subject-predicate-object (see [[tags-as-triples.meta]])
- Luhmann's Zettelkasten: permanent notes (zettels) + fleeting notes (queue items)
- Org-mode: capture (enqueue) / refile (resolve to thread) / archive (mark done)
- GTD: queue = inbox, thread = project, review = scan open items

**Generalization potential:** this system is not piescript-specific. The thread/queue model, edge syntax, and action vocabulary are domain-agnostic. Future direction: an MCP server that actively manages threads, queues, and zettels -- command-based interface, cross-session coordination, computed queue views, async persistence.

**Depends on**: [[tags-as-triples.meta]]
**Connections**:
- analogous-to: [[join-calculus.coordination]] — queues synchronize threads like channels synchronize computations
- related: [[channel-registry.infrastructure]] — queue is a registry of pending work
- complements: [[universal-vs-topic.meta]] — thread/queue is another meta-level organization pattern
- uses: [[tags-as-triples.meta]] — edge syntax in threads is subject-predicate-object triples
- related: [[channels.infrastructure]] — queues synchronize threads the way channels synchronize computations
