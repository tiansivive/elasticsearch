---
tags: [compilation, types, technique, concept, lowering]
refs:
  - doc:references.md
---
# Monomorphization

Specializing polymorphic code to concrete types. Eliminates runtime type dispatch. Rust's approach (vs Haskell's dictionary passing). Trade-off: code size explosion vs runtime speed. Relevant for piescript compilation -- monomorphized code can target JVM bytecode directly without boxing/dispatch overhead. Alternative to dictionary passing for typeclass implementation.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- contrasts-with: [[runtime-dispatch.types]] -- monomorphization vs dictionary passing; the two approaches to runtime polymorphism
- prerequisite-for: [[bytecode-compilation.performance]] -- efficient bytecode needs monomorphized code
- prerequisite-for: [[interaction-nets.computation]] -- nets need concrete types for port wiring
- uses: [[typeclasses.types]] -- monomorphization is one implementation strategy for typeclasses
- extends: [[lowering-pass.performance]] -- monomorphization is a lowering pass
- tension-with: code size (polymorphic functions duplicated per type instantiation)
