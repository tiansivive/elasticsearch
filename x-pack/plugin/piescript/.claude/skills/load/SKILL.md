---
name: load
description: Load all mandatory project documentation
disable-model-invocation: true
user-invocable: true
---

Read the following project documentation files in order, following any links they contain recursively until you have a complete picture of the project (all paths relative to `x-pack/plugin/piescript/`):

1. docs/AGENTS.md
2. docs/decisions.md
3. docs/z-piescript/README.md — design space knowledge base: model, zettel format, ref prefixes
4. docs/z-piescript/VOCABULARY.md — tag and edge vocabulary
5. docs/z-piescript/WORKFLOW.md — threads, queues, paper trail
6. docs/vision.md
7. docs/z-piescript/thread.md — append-only paper trail of prior work
8. All meta zettels — read every zettel tagged `meta` (knowledge-base conventions, design-to-implementation pipeline). Find them with:

```bash
python3 docs/z-piescript/scripts/catalog.py meta
```

Then run the scripts to get an overview of all tracked design topics and pending work:

```bash
python3 docs/z-piescript/scripts/catalog.py --compact
```

```bash
python3 docs/z-piescript/scripts/queue.py
```

```bash
python3 docs/z-piescript/scripts/roadmap_status.py
```

**Procedure skills** (canonical how-to — read when the corresponding work comes up, not necessarily at load time):

- `zettelkasten` skill (`.claude/skills/zettelkasten/SKILL.md`) — creating, updating, and connecting zettels; thread/queue/paper-trail actions.
- `create-plan` skill (`.claude/skills/create-plan/SKILL.md`) — authoring and executing implementation plans.

After reading, confirm you have the full project context with a brief summary of current status, including any open queue items that may be relevant.

**User interaction**: follow `docs/AGENTS.md` § *Interaction with the user* and `CLAUDE.md` § *Interaction with the user* (and `.cursor/rules/agent-interaction.mdc`): do not edit the repo in response to questions or meta feedback unless the user explicitly asks you to; confirm before substantive changes.
