---
tags: [language, implemented, syntax, documentation]
refs:
  - code:Value.java
  - code:Evaluator.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Currying

Piescript functions are curried — multi-argument functions are chains of single-argument functions.

- `BuiltinVal(name, arity, partialArgs)` in [[closure-val.language]] supports partial application: applying one argument returns a new `BuiltinVal` with the arg stored
- `List.map f` partially applies map, returning a function awaiting the list
- Multi-param lambda `fn x y -> body` desugars to `fn x -> fn y -> body`

**Depends on**: [[closure-val.language]]
**Enables**: [[prelude.language]]
**Connections**:
- uses: [[serialization.infrastructure]] — `BuiltinVal` serialization preserves partial args
- uses: [[closure-val.language]] — `BuiltinVal` is the runtime representation of curried partial application
- related: [[pattern-reuse.language]] — lambda params can use pattern destructuring (`fn { x, y } -> ...`); desugars to `fn $arg -> match $arg | { x, y } -> ...`
