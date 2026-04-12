---
tags: [types, language, syntax, open, feature, needs-design, later, concept]
refs:
  - thread:type-foundations
  - thread:language-expressiveness
---
# Type Aliases

User-defined type aliases for reducing boilerplate and naming common record shapes:

```
type Container a = { value: a, metadata: { created: Double } }
type Event = { timestamp: Double, source: Keyword, data: Keyword }
```

Parameterized aliases use the [[kind-system.types|kind system]]: `Container : Type -> Type` is
a type constructor. Non-parameterized aliases are kind `Type`. Aliases are transparent --
the elaborator expands them at use sites, leaving no trace in [[core-ir.language]].

**Design considerations:**
- Expansion during [[elaboration-architecture.types|elaboration]] (transparent) vs newtype (opaque, distinct)
- Recursive aliases: `type Tree a = { value: a, children: List (Tree a) }` -- needs
  [[recursive-types.types]] support
- Interaction with [[typeclasses.types]]: can typeclass instances be defined on aliases?
  Transparent aliases say no (they're just abbreviations); newtypes say yes.
- Scope: top-level only, or inside block expressions?

**Depends on**: [[kind-system.types]], [[f-omega-lite.types]]
**Enables**: (none directly)
**Connections**:
- informs: [[module-system.tooling]] -- type aliases are proto-module exports; reusable named types
- informs: [[typeclasses.types]] -- aliases and typeclasses are often co-designed (can you constrain an alias?)
- uses: [[elaboration-architecture.types]] -- alias expansion happens during elaboration
- uses: [[antlr-grammar.language]] -- `type` keyword would need grammar support
- informs: [[type-annotations.types]] -- aliases provide user-friendly names for annotation sites
- related: [[record-type.language]] -- most aliases will name record shapes
- related: [[recursive-types.types]] -- recursive type aliases need equi-recursive or iso-recursive support
- informs: [[target-users.principle]] -- aliases make types readable for non-PL-expert users
- informs: [[lsp.tooling]] -- alias expansion/navigation in IDE support
