---
tags: [meta, workflow, tooling]
refs: []
---
# Cursor Plan Template

The canonical **shape** for piescript implementation plans under `.cursor/plans/`. Copy
`_TEMPLATE.plan.md` when starting a new plan.

## Frontmatter (YAML)

- **`name`** — Short title.
- **`overview`** — Delivers + **planned non-goals** (see body **Out of scope**); headline acceptance hint is fine here; do not conflate with **Deferred work** (mid-flight postponements).
- **`todos`** — **Milestone list** for agents and the queue zettel: stable `id`, `content`,
  `status` (`pending` / `in_progress` / `completed`). Typical pattern:
  - `setup-zettels` — implementation zettel (1:1 with plan), queue zettel, `plan:` ref, hub/design
    links; session ref when applicable.
  - `milestone-implementation` — one or more entries for substantive work (duplicate the block
    in YAML if the phase needs several steps).
  - `verification` — tests and manual checks (details in body **Verification**).
  - `paper-trail-close-out` — thread, session zettel, debug scripts, `current-state`, zettel tags,
    ADRs, then **reconciliation** (code/docs vs zettelkasten) and **user-confirmed** new zettels.
    **Full canon** in [[implementation-plan-workflow.meta]]; the todo **reminds** agents to run it.

**Why not put every close-out substep only in YAML?** The workflow meta zettel and AGENTS are the
**single source of truth** for process; repeating fifteen bullets in every plan’s frontmatter
drifts when process changes. Use the body **Close-out** section for **phase-specific** extras only.

- **`isProject`** — Present for Cursor plan metadata compatibility with other plans in this repo (not consumed by Gradle). Keep `false` unless Cursor docs say otherwise.

## Body sections

- **Agent guardrails** — When to stop; **never assume user intent**; default to asking.
- **Review policy** — Immediately after guardrails; pause after each todo if requested.
- **Scope** — In scope + design links.
- **Out of scope** — Planned non-goals from the start.
- **Deferred work** — Postponed **during** implementation (e.g. tech debt); not the same as out of scope.
- **Acceptance criteria** — Definition of done.
- **Work breakdown** — Freeform; aligns with todos.
- **Design notes** — Optional sketches, pseudo-code, Mermaid.
- **Risks / breaking changes** — Complications and compatibility.
- **Plan drift** — Optional execution deltas.
- **Verification** — Phase-specific tests/commands.
- **Close-out** — Mechanical checklist per workflow meta, then **zettelkasten reconciliation**
  (discrepancies) and **confirm new zettels** with the user; phase-specific bullets only.
- **Design decisions** — Optional ADR/zettel pointers.

**File location**: [`.cursor/plans/_TEMPLATE.plan.md`](../../../../.cursor/plans/_TEMPLATE.plan.md)

**Depends on**: [[implementation-plan-workflow.meta]]
**Enables**: (none directly)
**Connections**:
- template-for: [[implementation-plan-workflow.meta]] — plans created under this shape
