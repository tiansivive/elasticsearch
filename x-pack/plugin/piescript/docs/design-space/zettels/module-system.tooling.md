---
tags: [tooling, open, feature, concept, someday]
refs:
  - roadmap:phase-7
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:language-expressiveness
---
# Module System

Named, reusable piescript definitions stored in the cluster. Like stored scripts but typed and composable. Import mechanism. [[content-addressed-code.tooling]] potential (Unison-style).

Open design questions:

- **Storage location** -- Small modules in cluster state (fast, replicated, bloats metadata). Larger modules in a system index (`.piescript-modules`), trading latency for unbounded storage. Same trade-off as stored scripts.
- **Versioning** -- Content-addressed (Unison-style, hash of elaborated AST IS the version) vs explicit semver. Content-addressed makes refactoring safe: references by hash, not name.
- **Import/export syntax** -- `import Module.function` (selective), `open Module` (brings all into scope), or qualified `Module.function`. Likely all three.
- **Scoping** -- Flat namespaces vs private/public visibility. Encapsulation vs simplicity.
- **Interaction with [[scheduled-execution.lifecycle]]** -- Modules must be resolvable at [[elaboration-architecture.types]] time on the scheduler node. Content-addressed scheme simplifies caching.

**Depends on**: [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- part-of: [[phase-7.roadmap]]
- related: [[content-addressed-code.tooling]] — Unison-style content-addressed storage mentioned as design option
- related: [[stored-functions.tooling]] — storage mechanism for module definitions
- related: [[elaboration-architecture.types]] — module imports must be resolved at elaboration time
- prerequisite-for: [[lsp.tooling]] — LSP benefits from module-aware navigation
