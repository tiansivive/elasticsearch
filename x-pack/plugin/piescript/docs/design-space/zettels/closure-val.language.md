---
tags: [language, runtime, serialization, mobility, implemented]
refs:
  - adr:D-014
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Closure Value

`ClosureVal(CoreExpr body, Value[] env)` -- lambda closure with code + captured environment. Fully serializable (D-045). `BuiltinVal(name, arity, partialArgs)` supports curried partial application. Both travel across nodes in closures.

**Depends on**: [[core-ir.language]], [[de-bruijn-indices.language]]
**Enables**: [[code-mobility.coordination]], [[serialization.infrastructure]], [[nbe-compilation.esql]]
**Connections**:
- related: [[purity.language]] — purity guarantees cloning the environment is safe
- related: partial application — `map (fn r -> r.x)` is map applied to one arg, returning a `BuiltinVal` awaiting the second
- related: [[currying.language]] — BuiltinVal supports curried partial application
