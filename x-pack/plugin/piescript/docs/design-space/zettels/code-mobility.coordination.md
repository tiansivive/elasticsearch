---
tags: [coordination, language, serialization, mobility, implemented, pi-calculus, distributed, concept]
refs:
  - adr:D-014
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Code Mobility

Lambdas and closures are "traveling code" — they can be serialized and shipped to remote nodes.

- Safe because the language is pure (D-014, [[purity.language]])
- Closed lambdas serialize as [[core-ir.language]] subtrees
- Closures serialize as `(CoreExpr body, Value[] env)` via [[closure-val.language]]
- `ClosureVal` and `BuiltinVal` are fully serializable

**Depends on**: [[purity.language]], [[serialization.infrastructure]], [[closure-val.language]]
**Enables**: [[inbox.infrastructure]], [[push-down-compilation.performance]]
**Connections**:
- part-of: [[block-c.roadmap]]
- specializes: [[name-passing.coordination]] — Sangiorgi's agent-passing paper shows code mobility reduces to name passing in pi-calculus
- tension-with: [[non-serializable-types.types]] — `SearcherVal`, `DocRefVal`, `WriterVal` cannot travel
- implements: [[data-locality.distributed]] — code mobility is the mechanism that enables data locality (ship closures to data nodes)
- inspired-by: [[nomadic-pict.coordination]] — Nomadic Pict provides the theoretical basis for typed code mobility with location tracking
- related: [[zero-copy-linear-transfer.performance]] — linear closures enable zero-copy remote transfer
- cites: [[sangiorgi-agent-passing.paper]]
