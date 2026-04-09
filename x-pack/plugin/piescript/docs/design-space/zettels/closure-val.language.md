---
tags: [language, runtime, serialization, mobility, implemented, documentation]
refs:
  - adr:D-014
  - adr:D-045
  - code:Value.java
  - code:ValueSerialization.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Closure Value

`ClosureVal(CoreExpr body, Value[] env)` — lambda closure with code + captured environment. Fully serializable (D-045). `BuiltinVal(name, arity, partialArgs)` supports curried partial application. Both travel across nodes via [[code-mobility.coordination]].

**Depends on**: [[core-ir.language]], [[de-bruijn-indices.language]]
**Enables**: [[code-mobility.coordination]], [[serialization.infrastructure]], [[nbe-compilation.esql]]
**Connections**:
- uses: [[purity.language]] — purity guarantees cloning the environment is safe
- implements: [[currying.language]] — BuiltinVal supports curried partial application; `map (fn r -> r.x)` is map applied to one arg, returning a `BuiltinVal` awaiting the second
- enables: [[code-mobility.coordination]] — serializable closures are the mechanism for shipping code to remote nodes
