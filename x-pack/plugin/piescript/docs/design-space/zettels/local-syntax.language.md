---
tags: [language, syntax, mobility, safety, open, concept]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Local Syntax

Lua-style `local` keyword for node-local bindings (values that cannot leave the node). A `local x = ...` declaration marks `x` as [[non-serializable-types.types]] -- any attempt to capture it in a closure sent to another node via [[code-mobility.coordination]] is a compile-time error. This provides an explicit, user-facing mechanism for the same guarantee that the [[local-kind.types]] provides implicitly through the type system.

**Depends on**: [[local-kind.types]], [[non-serializable-types.types]], [[code-mobility.coordination]]
**Enables**: explicit local declaration without understanding kind system
**Connections**:
- contrasts-with: [[local-kind.types]] — kind-based is implicit (type-driven); this is explicit (declaration-driven)
- alternative-to: [[typeclasses.types]] — could also model as `Serializable a` constraint on closures
