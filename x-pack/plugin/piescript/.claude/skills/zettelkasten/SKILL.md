---
name: zettelkasten
description: >-
  Use when creating, updating, or connecting zettels in the piescript design space
  (docs/z-piescript/), recording work in thread.md, or managing queue items. Canonical
  procedure for all knowledge-base writes — meta zettels hold the rationale, this skill
  holds the how-to.
---

# Piescript zettelkasten (knowledge-base writes)

This skill is the **single source of truth for the procedure**. Reference material lives in
the knowledge base itself and is not duplicated here:

- `docs/z-piescript/README.md` — model, zettel format, ref prefixes, file naming
- `docs/z-piescript/VOCABULARY.md` — tag vocabulary, tag aliases, tag groups, edge vocabulary
- `docs/z-piescript/WORKFLOW.md` — threads, queues, paper-trail system
- Rationale: [[design-to-implementation.meta]], [[thread-queue-system.meta]],
  [[tags-as-triples.meta]], [[universal-vs-topic.meta]]

**Note**: `docs/z-piescript/` is a nested git repository (z-loom federation). Edits there are
committed separately from the elasticsearch repo — never include them in elasticsearch commits.

## Principles

- **Free-form, liberal use.** Zettels, tags, axes, and refs are tools for noting down, not
  structures for constraining. Add tags as they apply; invent new edge verbs when no existing
  verb fits (then add them to `VOCABULARY.md`).
- **Semantic connections.** Discover edges by understanding meaning, not by grepping for
  `[[wiki-links]]`. When you touch a zettel, consider what else it genuinely relates to.
- **Don't re-litigate.** Check `superseded` tags and `supersedes`/`rejected-in-favor-of` edges
  before proposing; don't revive a dropped idea without acknowledging why it was dropped.
- **Hubs outlive plans.** A hub captures the full design landscape; plans scope one phase.

## Creating a zettel

1. **Check for an existing zettel first**: `python3 docs/z-piescript/scripts/catalog.py <keyword>`.
   Update the existing one rather than creating a near-duplicate.
2. **Name the file** per README § File naming: `concept.qualifier.md`, concept first, qualifier
   as a visual grouping hint only (tags classify, qualifiers don't). Avoid redundant qualifiers.
3. **Write frontmatter**: `tags` (required — flat list; aim for one Concern tag and one Maturity
   tag, per VOCABULARY § Tag groups) and `refs` (optional — prefixed links: `adr:`, `plan:`,
   `session:`, `code:`, `doc:`, `resource:`, `thread:`, `queue:`, `skill:`).
4. **Write the body**: title, one-paragraph description, then edges:
   - `**Depends on**:` / `**Enables**:` — structural edges (the dependency DAG)
   - `**Connections**:` — `- verb: [[link]] — optional note`, active voice
     ("this zettel [verb] that zettel"); write the inverse on the other zettel if needed
5. **Connect both ways where it matters**: add/adjust edges on related zettels, hubs
   (`includes`), and thread hubs.
6. **In plan close-outs**: propose new zettels and **confirm with the user** before creating
   them (no bulk creation without agreement). Mid-discussion, creating a zettel for a newly
   surfaced concept is fine — tell the user what you created.

## Updating a zettel

- **Maturity changes**: update the Maturity tag (`open` → `designed` → `implemented`;
  `superseded`/`obsolete`/`archived` when dropped). When superseding, add a `supersedes` or
  `rejected-in-favor-of` edge naming the replacement.
- **Session trace**: add `session:<id>` to `refs` when a session substantially discusses the
  zettel.
- **ADRs**: when a decision lands in `docs/decisions.md`, add `adr:D-NNN` to related zettels.
- **Priority/state tags** (`now`/`next`/`later`/`someday`, `ready`/`blocked`/`needs-design`):
  keep them current; remove state tags (`blocker`, `deferred`) when no longer true.
- **Never delete content for tidiness** — zettels are the durable knowledge layer. Merge
  duplicates (use a `duplicates` edge first if unsure), archive rather than delete.

## Hubs and threads

- When a topic outgrows one zettel, create a **hub** (tag `hub`) with `includes` edges to its
  members. The hub is the full design picture, not one implementation phase.
- **Thread hubs** (tag `thread`) are the roadmap: an ordered item sequence with dependency
  annotations and priority tags. Members point back via `thread:<stem>` frontmatter refs.
- After implementing or re-prioritizing, update the thread hub sequence and the member tags;
  verify with `python3 docs/z-piescript/scripts/roadmap_status.py`.

## Paper trail — thread.md

Append (never rewrite) a session block to `docs/z-piescript/thread.md` for every session that
does design or implementation work:

```
## session:<id-or-slug> — YYYY-MM-DD [tags]

One-paragraph summary of what was explored/decided.

[[zettel-a]] -- verb -> [[zettel-b]]            # edges traversed or created
SPAWN [[new-zettel]] — why it now exists
ENQUEUE [[zettel]] — deferred work, one line
RESOLVED [[zettel-or-item]] — what closed it
```

## Queue

- **Defer work**: ensure the zettel exists → `ENQUEUE` line in thread.md → `- [ ]` item in
  `[[global-pending.queue]]` (or a scoped queue zettel). Proactively enqueue when discussion
  surfaces future work — notify the user.
- **Resolve**: mark `[x]` (or `[~]` dropped) → `RESOLVED` line in thread.md.
- Per-phase implementation work gets its own queue zettel (see the `create-plan` skill).
- Verify with `python3 docs/z-piescript/scripts/queue.py`.

## Vocabulary maintenance

New tags, aliases, edge verbs, ref prefixes, or tag groups go into `VOCABULARY.md` (tags/edges)
or `README.md` (ref prefixes) when first used — keep the tables in sync with practice.

## Do not

- Duplicate reference tables (tags, edges, ref prefixes) into other docs or this skill.
- Put procedural checklists into meta zettels — they link here via `skill:zettelkasten`.
- Mix z-piescript edits into elasticsearch commits (separate repos).
- Rewrite or reorder existing thread.md blocks — append only.
