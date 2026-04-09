---
tags: [language, syntax, implemented, concept]
refs:
  - adr:D-050
  - code:Prelude.java
  - code:Elaborator.java
---
# Qualified Builtin Namespacing

Builtins use the `Namespace.name` convention: `Math.abs`, `Shard.open`, `List.map`, `Exchange.connect`, `Page.toList`. The [[antlr-grammar.language|parser]] supports dotted identifiers and resolves them against the [[prelude.language|Prelude's]] qualified names. This keeps the global namespace clean while giving builtins discoverable, self-documenting names. The convention is a stepping stone toward a full [[module-system.tooling|module system]] -- today the namespaces are hardcoded in the Prelude, but the syntax and user mental model are already module-shaped.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[block-d.roadmap]]
- prerequisite-for: [[module-system.tooling]] -- the qualified-name convention establishes the syntax that a module system will generalize
- uses: [[prelude.language]] -- all qualified builtins are registered in the Prelude module
