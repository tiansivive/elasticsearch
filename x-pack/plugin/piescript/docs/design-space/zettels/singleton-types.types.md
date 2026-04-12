---
tags: [types, theoretical, concept, exploration]
refs:
  - doc:references.md
---
# Singleton Types

Literal types / singleton types: types inhabited by exactly one value. `let x: 42 = 42` is
well-typed; `let x: 42 = 43` is a type error. The type `42` has exactly one inhabitant.

**How this connects to piescript:**
- Falls out of [[bidir-checking.types|bidirectional checking]]: in checking mode, a literal
  expression is checked against a literal type, and the values must match
- Already implicit in the [[label-kind.types|Label kind]] design: type-level string singletons
  like `"name"` in field projection (`Project "name" r`) are singleton types at the kind level
- Boolean singletons (`true`/`false` as types) would make [[match-type-checking.language|match
  exhaustiveness]] trivially decidable for boolean patterns

**Connection to dependent types:**
Singleton types are the simplest form of [[dependent-types.types|dependent types]] -- the type
depends on a specific value. They don't require a full dependent type system; they work within
System F + bidirectional checking. GHC's `DataKinds` promotes term-level literals to types,
achieving singleton types without full dependency.

**Practical value for piescript:**
- Type-safe field projection: `project : Label l -> Record { l: a | r } -> a`
- Exhaustiveness checking in match: singletons enumerate the value space
- Compile-time configuration validation: literal types as schema constraints

**Depends on**: [[bidir-checking.types]]
**Enables**: (none directly)
**Connections**:
- extends: [[bidir-checking.types]] -- checking a literal against a literal type is singleton type checking
- informs: [[label-kind.types]] -- Label kind uses type-level string singletons for field projection
- informs: [[dependent-types.types]] -- singleton types are the minimal dependent type feature
- related: [[kind-system.types]] -- promoted literals live at the kind level (DataKinds-style)
- informs: [[match-type-checking.language]] -- singleton types would strengthen exhaustiveness checking
- related: [[f-omega-lite.types]] -- singleton types fit within F-omega without requiring full dependency
- related: [[type-narrowing.types]] -- matching on a singleton type narrows the type in branches
- analogous-to: TypeScript literal types, GHC DataKinds, Scala singleton types
