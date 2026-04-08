---
tags: [language, implemented, syntax]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Currying

Piescript functions are curried -- multi-argument functions are chains of single-argument functions. `BuiltinVal(name, arity, partialArgs)` supports partial application: applying one argument returns a new `BuiltinVal` with the arg stored. `List.map f` partially applies map, returning a function awaiting the list. Multi-param lambda `fn x y -> body` desugars to `fn x -> fn y -> body`.

**Depends on**: [[closure-val.language]]
**Enables**: [[prelude.language]]
**Connections**:
- related: partial application is pervasive — `min 100` creates a clamping function, `List.map inc` creates a list transformer
- related: [[serialization.infrastructure]] — `BuiltinVal` serialization preserves partial args
