---
tags: [meta, workflow, tooling, concept]
refs:
  - doc:AGENTS.md
---
# Implementation Plan Workflow

How to **author** and **execute** a scoped implementation plan for piescript. Plans live in
[`.cursor/plans/`](../../../../.cursor/plans/); they translate one phase of a hub (or thread)
into shippable work. This workflow ties plans to the zettelkasten, the thread paper trail,
review gates, and repo hygiene.

**Canonical checklist** (do not skip without explicit user direction):

1. **Design grounding** — Read `decisions.md`, relevant zettels, and `current-state.md` before
   locking scope. Hub zettels outlive plans; the plan scopes a single phase.

2. **Artifacts at plan start**
   - **Implementation zettel** — Atomic note linking the plan, hub, and code-facing design
     (`refs: plan:…`, connections to hub and language zettels).
   - **Queue zettel** — Checklist mirroring the plan todos; tick items as you go; link to the
     implementation zettel and hub. Prefer a dedicated queue zettel per phase when the work is
     non-trivial.

3. **Plan document** — Use the structure in [[cursor-plan-template.meta]] (copy
   `_TEMPLATE.plan.md`). Include **acceptance criteria**, **out of scope** (planned non-goals) vs
   **deferred work** (postponed mid-flight, e.g. tech debt—not the same thing), design notes with
   sketches or diagrams when helpful, and **review policy** after guardrails. YAML `todos` are
   milestones (setup, implementation, verification, paper-trail)—not a full duplicate of every
   close-out substep; see the template meta zettel for why.

4. **Execution**
   - **Stop after each plan step** when the user requested stepwise review; otherwise batch sensibly and still keep commits/diffs focused.
   - **Do not assume user intent** or guess when specs are unclear—**ask** (see plan guardrails and root `AGENTS.md` on ambiguity).
   - Follow **coding guidelines** in [AGENTS.md](../../AGENTS.md) (immutability, declarative
     style, type safety, ES lifecycle rules). Align new Core IR and elaboration with existing
     patterns in the codebase.

5. **Thread** — Append a session block to `docs/design-space/thread.md`: edges, `RESOLVED` /
   `ENQUEUE` / `SPAWN` as appropriate, links to zettels touched.

6. **Session zettel** — For substantial work, add a session zettel with `refs: session:<id>`,
   summary, and `produced:` / `implements:` connections. Link from the thread block (`SPAWN`).

7. **Debug scripts** — When behavior is user-visible or cross-node, add minimal examples to
   `debug/test-dev.sh`, `debug/test-eval.sh`, and `debug/test-multinode.sh` as appropriate.

8. **Docs close-out** — Update `current-state.md`, hub/thread zettels (maturity, connections),
   and `decisions.md` if new ADRs apply.

9. **Tests** — Unit tests for new types and passes; integration tests (`PiescriptIT`) when the
   REST pipeline should prove the feature; serialization round-trips when `Writeable` paths change.

10. **Zettelkasten reconciliation** — At the **end** of the plan (after code and mechanical docs updates), the agent should **review what was done** and compare **reality** (code,
    tests, `current-state.md`, `decisions.md`) to the **zettel graph** (implementation zettel,
    hub, design zettels, queue). **Surface discrepancies** in prose: outdated tags or connections,
    zettels that no longer match behavior, missing ADR refs, contradictions between docs and
    implementation, etc.—do not paper over gaps.

11. **New zettels — user confirmation** — List any **new** zettels that ought to exist (deferred
    work, newly surfaced concepts, follow-ups). **Confirm with the user** which to create and how
    to name them before adding files; avoid inventing or bulk-creating notes without agreement.

Agents using Cursor should load the project skill **create-plan** (`.cursor/skills/create-plan/SKILL.md`,
a symlink to `.claude/skills/create-plan/SKILL.md`) for the same workflow in imperative form.

**Depends on**: [[thread-queue-system.meta]], [[design-to-implementation.meta]]
**Enables**: (none directly)
**Connections**:
- refines: [[design-to-implementation.meta]] — step 4 (plan) and following steps in concrete checklist form
- uses: [[thread-queue-system.meta]] — thread and queue conventions during execution
- specifies: [[cursor-plan-template.meta]] — plan file shape and frontmatter
