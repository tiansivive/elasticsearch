---
tags: [coordination, language, serialization, mobility, implemented, pi-calculus, distributed]
refs:
  - adr:D-014
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Code Mobility

Lambdas and closures are "traveling code" — they can be serialized and shipped to remote nodes. Safe because the language is pure (D-014). Closed lambdas serialize as `CoreExpr` subtrees. Closures serialize as `(CoreExpr body, Value[] env)`. `ClosureVal` and `BuiltinVal` are fully serializable.

**Depends on**: [[purity.language]], [[serialization.infrastructure]], [[closure-val.language]]
**Enables**: [[inbox.infrastructure]], [[push-down-compilation.performance]]
**Connections**:
- related: [[name-passing.coordination]] — Sangiorgi's agent-passing paper shows code mobility reduces to name passing in pi-calculus
- related: [[non-serializable-types.types]] — `SearcherVal`, `DocRefVal`, `WriterVal` cannot travel
- related: [[data-locality.distributed]] — code mobility is the mechanism that enables data locality (ship closures to data nodes)
- related: [[nomadic-pict.coordination]] — Nomadic Pict provides the theoretical basis for typed code mobility with location tracking
- related: [[zero-copy-linear-transfer.performance]] — linear closures enable zero-copy remote transfer
