# Design Space

A structured knowledge base of piescript's design landscape. Each item is an atomic note
in `items/`, tagged and linked. Items at all maturity levels — implemented, open,
theoretical — are included because the design space is the full picture, not just the
TODO list.

Compatible with [Obsidian](https://obsidian.md) — open `docs/design-space/` as a vault
for graph navigation.

## File naming

`thing.domain.md` — the primary concept first, domain qualifier second.
Examples: `topby.esql.md`, `numerical.typeclass.md`, `narrowing.types.md`,
`transport-channels.infrastructure.md`.

When no domain qualifier is needed: `recursion.md`, `error-handling.md`.

## Item format

```markdown
---
tags: [language, types, open, runtime, fault-tolerance]
refs:
  - adr:D-051
  - adr:D-047
  - session:e3b83171-9476-43b2-bbc3-fde5725a57a0
  - plan:compute_engine_streaming_f5db78f2
  - roadmap:block-h
  - vision:external-interaction-model
  - doc:data-access.md
  - code:EvalExchange.java
---
# Title

One-paragraph description of the concept.

**Depends on**: [[pattern-matching.language]], [[adts.types]]
**Enables**: [[actor-model.lifecycle]], [[plugin-spi.external]]
**Connections**: Free-form notes on how this relates to other items.
```

### Frontmatter fields

| Field | Required | Type | Description |
|-------|----------|------|-------------|
| `tags` | yes | list | Flat list of tags from any axis or free-form (see Axes below) |
| `refs` | no | list | References to external material using prefixed links (see Ref prefixes below) |

That's it. Two fields. Everything else is convention within these fields.

### Ref prefixes

References use a `prefix:value` convention. New prefixes can be added freely —
just document them here.

| Prefix | Points to | Example |
|--------|-----------|---------|
| `adr` | Decision record in `docs/decisions.md` | `adr:D-051` |
| `roadmap` | Section in `docs/roadmap.md` | `roadmap:block-h` |
| `vision` | Section in `docs/vision.md` | `vision:external-interaction-model` |
| `plan` | Plan file in `.cursor/plans/` | `plan:compute_engine_streaming_f5db78f2` |
| `session` | Chat session ID (any agent — Claude, Cursor, etc.) | `session:e3b83171-...` |
| `doc` | Other documentation file | `doc:data-access.md` |
| `code` | Source file | `code:EvalExchange.java` |

## Axes

Axes organize the tag vocabulary into dimensions. A tag belongs to an axis
by convention (documented below), but in the frontmatter it's just a flat list.
Adding a new axis is trivial: define it here, start using the tags.

### Concern (what area does this touch?)

| Tag | Description |
|-----|-------------|
| `language` | Core language features, syntax, semantics, control flow |
| `types` | Type system, inference, checking, kinds |
| `esql` | ESQL compilation, T-LINQ combinators |
| `data` | Direct data access, Lucene, shard ops, MV fields, materialization |
| `infrastructure` | Channels, exchange, transport, compute engine |
| `lifecycle` | Script lifecycle, actor model, scheduling, persistence |
| `external` | Outside-world interaction, plugins, FFI, SSE, HTTP |
| `tooling` | IDE, modules, stored programs, developer experience |
| `performance` | Compilation, optimization, push-down, bytecode |
| `security` | Auth, permissions, sandboxing, capabilities |
| `es-internals` | Elasticsearch infrastructure, transport layer, plugin system |

### Maturity (how baked is this?)

| Tag | Description |
|-----|-------------|
| `implemented` | In the codebase, tested, working |
| `designed` | Documented design exists, not yet implemented |
| `open` | Identified need, no settled design yet |
| `theoretical` | Informing our thinking, not a direct implementation target |
| `tech-debt` | Exists but needs fixing, refactoring, or hardening |
| `superseded` | Was planned, now replaced by a different approach |

### Domain tags (free-form, grow organically)

| Tag | Description |
|-----|-------------|
| `runtime` | Runtime behavior, evaluation, dispatch, polymorphism |
| `channels` | Channel mechanism, messaging, coordination |
| `row-types` | Row polymorphism, row operators, record types |
| `typeclasses` | Typeclass system and specific typeclass designs |
| `compute` | Computation model, distributed execution |
| `data-processing` | Data transformation, aggregation, enrichment |
| `fault-tolerance` | Error recovery, crash handling, supervision |
| `control-flow` | Loops, recursion, branching |
| `primitives` | Base types, literals, built-in values |
| `streaming` | Streaming data, backpressure, incremental output |
| `concurrency` | Parallel execution, synchronization |
| `serialization` | Wire format, cross-node data transfer |
| `resources` | Resource management, cleanup, lifecycle |
| `compilation` | Bytecode, JVM codegen, pure fragment detection |
| `push-down` | Query optimization, predicate push-down |
| `materialization` | Page-to-Value conversion, MV handling |
| `coordination` | Join Calculus, spawn/when/send patterns |
| `mobility` | Code shipping, closure serialization, remote evaluation |

New tags appear as needed. Document them here for vocabulary consistency.

## Dependency and enablement

- **Depends on**: items that must exist before this one can be implemented
- **Enables**: items that become possible once this is done
- **Connections**: free-form notes on relationships, tensions, or coupling

Use `[[filename-without-extension]]` for links (Obsidian-compatible).

## Scope

The design space includes everything:
- **Implemented features** — what exists and what it enables
- **Open design questions** — the active thinking
- **Theoretical foundations** — the research informing decisions
- **Tech debt** — things that work but need improvement
- **Superseded ideas** — preserved for history and context

See [metrics.md](metrics.md) for derived metrics (priority, staleness, clustering).
