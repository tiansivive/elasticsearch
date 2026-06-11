---
name: create-plan
description: >-
  Use when creating or executing a piescript implementation plan under x-pack/plugin/piescript:
  .cursor/plans/, zettels, queue, thread, session zettel, debug scripts, docs, review stops.
  Apply when the user asks for a plan, phased work, or block implementation.
---

# Piescript implementation plan (authoring & execution)

This skill is the **canonical procedure** for plan authoring and execution. Rationale and design
history live in [[implementation-plan-workflow.meta]] and [[cursor-plan-template.meta]] — they
link back here; do not duplicate this checklist there.

## When to use

- New or existing plan under `.cursor/plans/`.
- Executing an existing plan end-to-end or step by step.
- User wants zettels, queue, thread, session documentation with code.

## Companion docs

- Plan shape: copy `.cursor/plans/_TEMPLATE.plan.md` (section-by-section guidance in
  [[cursor-plan-template.meta]])
- Knowledge-base writes (zettels, thread, queue mechanics): the **zettelkasten skill**
  (`.claude/skills/zettelkasten/SKILL.md`)
- Context: `docs/AGENTS.md`, `CLAUDE.md`
- Interaction norms: `.cursor/rules/agent-interaction.mdc`, `docs/AGENTS.md` § *Interaction with the user*
- Coding: `docs/AGENTS.md` (Coding Guidelines)

## Agent behavior (non-negotiable)

- **Do not assume** user intent; **do not guess**. If unclear, **stop and ask**.
- **Stop and ask** if specs conflict, are ambiguous, or work would need large **unplanned** design
  not in the plan or linked zettels/ADRs.
- Honor **Review policy** in the plan (pause after each todo/milestone when set to yes).

## Authoring

1. Ground in `current-state.md`, `decisions.md`, catalog
   (`python3 docs/z-piescript/scripts/catalog.py --compact`).
2. Copy `_TEMPLATE.plan.md` → new `*.plan.md`.
3. Fill **Out of scope** vs **Deferred work**, **Acceptance criteria**, milestones in YAML `todos`
   (add extra implementation todos if needed).
4. Create **implementation zettel** (1:1 with plan) and **queue zettel** early; `plan:` ref,
   hub/design links (per the zettelkasten skill).
5. **Review policy** — When authoring, set **Review policy** in the plan to **incremental review** (stop after each todo/milestone) unless explicitly told otherwise.

## Executing

1. Sync the **queue zettel** with plan todos; mark items `[x]` as they complete.
2. Implement focused diffs; follow existing patterns in the codebase.
3. **Verification** per plan body + workflow (tests, `PiescriptIT` when REST matters, serialization
   when wire format changes).
4. Update **debug scripts** when behavior is demonstrable: `debug/test-dev.sh`, `debug/test-eval.sh`,
   `debug/test-multinode.sh` as appropriate. Update setup scripts when required.
5. Append a **session block** to `docs/z-piescript/thread.md` (`RESOLVED`, `SPAWN`, edges to zettels).
6. Update (or create) a **session zettel** for substantial work (`refs: session:<uuid>`, `produced:` links); link from thread.
7. Close-out **docs** per workflow: `current-state.md`, hub/zettel tags and connections, `decisions.md` for new ADRs.
8. **Zettelkasten reconciliation** — Summarize what shipped vs the plan; compare code, `current-state.md`, and `decisions.md` to every relevant zettel. **List discrepancies** (stale tags, wrong connections, behavior vs zettel mismatch, missing ADR links).
9. **New zettels** — Propose any new notes that should exist; **get user confirmation** on which to create and naming before writing files.

## Do not

- Skip design-space zettel/design lookups for non-trivial language semantics (see `docs/AGENTS.md` § Design Space Knowledge Base).
- Silently widen scope or conflate **out of scope** with **deferred work**.
- Mix `docs/z-piescript/` edits into elasticsearch commits (it is a nested git repository).
