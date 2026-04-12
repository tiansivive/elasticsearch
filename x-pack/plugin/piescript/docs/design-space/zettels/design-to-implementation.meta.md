---
tags: [meta, workflow, concept]
refs: []
---
# Design-to-Implementation Workflow

The path from design discussion to shipped code follows a consistent pipeline through the
zettelkasten. Agents should follow this workflow and load all `meta` and `workflow` zettels
at session start.

## Pipeline


1. Discussion → atomic zettels
   Surface a concept → create a zettel in zettels/ → tag, connect, thread

2. Hub zettel → organize
   When a topic grows beyond a single zettel → create a hub with includes edges.
   The hub is the full design picture — not scoped to one implementation phase.
   It connects to everything related: prerequisites, future extensions,
   theoretical foundations, comparable systems.

3. Thread log → paper trail
   Record session work in thread.md: edges traversed, actions taken, items
   enqueued/resolved/spawned.

4. Implementation plan → scope a phase
   When ready to build → create a plan (.cursor/plans/) from the hub.
   The plan scopes ONE implementation phase from the hub's larger picture.
   Plan tasks reference specific zettels. The hub lives on; the plan completes.
   For the full checklist (queue zettel, thread, session zettel, debug scripts, review stops),
   see [[implementation-plan-workflow.meta]] and copy `_TEMPLATE.plan.md` per [[cursor-plan-template.meta]].

5. Queue → track work
   Pending items in [[global-pending.queue]] or thread-specific queues.
   Implementation items from the plan go here too.

6. ADR → record decisions
   Non-trivial design choices during implementation → decisions.md entry.
   Cross-reference the ADR from relevant zettels via `adr:D-NNN` refs.

7. Update → close the loop
   After implementation: update current-state.md, tag zettels as `implemented`,
   mark thread/queue items resolved, append to thread.md.


## Key principle

**Hubs outlive plans.** A hub zettel captures the full design landscape of a concept —
past decisions, current implementation, future directions, theoretical connections. A plan
captures one scoped phase of implementation. When the plan completes, the hub gains
`implemented` connections but keeps its future/theoretical edges for the next phase.

**Agents should load meta zettels at session start** to understand the workflow, tag
vocabulary, edge conventions, and thread/queue system before doing design or implementation
work.

**Depends on**: [[thread-queue-system.meta]], [[tags-as-triples.meta]]
**Connections**:
- extends: [[thread-queue-system.meta]] — adds the hub→plan→implement pipeline on top of the thread/queue workflow
- complements: [[universal-vs-topic.meta]] — meta zettels document the process; topic zettels document the design
- refined-by: [[implementation-plan-workflow.meta]] — concrete plan authoring/execution checklist and template
