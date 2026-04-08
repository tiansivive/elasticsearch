---
tags: [tooling, open]
refs:
  - roadmap:phase-7
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Module System

Named, reusable piescript definitions stored in the cluster. Like stored scripts but typed and composable. Import mechanism. Content-addressed code potential (Unison-style).

Open design questions:

**Storage location** — Small modules could live in cluster state (fast access, replicated automatically, but bloats cluster state metadata). Larger modules suit a system index (`.piescript-modules`), trading slightly higher access latency for unbounded storage. This is the same trade-off as stored scripts.

**Versioning** — Two models: content-addressed (Unison-style) where the hash of the module's elaborated AST IS the version — any change produces a new hash, and old references continue to resolve to the old definition. Or explicit semver, where authors declare versions and dependents pin ranges. Content-addressed code makes refactoring safe: renaming a function does not break dependents because references are by hash, not by name. The downside is that tooling must provide a human-readable layer on top of hashes.

**Import/export syntax** — `import Module.function` (selective import, avoids namespace pollution), `open Module` (brings everything into scope, convenient but risks collisions), or qualified access (`Module.function` without import). Likely all three, with `open` discouraged in larger programs.

**Scoping** — Are modules merely namespaces (flat bags of definitions) or do they introduce new scopes with private/public visibility? Private definitions would enable encapsulation, but add complexity to the module system.

**Interaction with scheduled execution** — Can a scheduled program import from a module? If so, modules must be resolvable at elaboration time on whichever node runs the scheduler. Are elaborated modules cached per-node, or re-fetched on every script submission? A content-addressed scheme simplifies caching: if you have the hash, the content is immutable.

**Depends on**: [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: Phase 7 on roadmap — enables library ecosystem
- related: [[content-addressed-code.tooling]] — Unison-style content-addressed storage mentioned as design option
- related: [[stored-functions.tooling]] — storage mechanism for module definitions
