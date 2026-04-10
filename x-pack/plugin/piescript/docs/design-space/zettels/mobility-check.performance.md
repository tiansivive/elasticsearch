---
tags: [performance, distributed, mobility, open]
refs:
  - adr:D-014
  - doc:archive/phase3_stream_runtime.plan.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Mobility Check

Determining which closures can safely travel to remote nodes. A closure that captures non-serializable values (`SearcherVal`, `DocRefVal`, `WriterVal`) cannot be shipped — it must stay on its creation node. Originally designed as a static analysis pass in Phase 3 (checking captured bindings against serialization capability). Currently deferred — non-serializable types throw `IOException` at the wire boundary (runtime enforcement).

Future options: static analysis during elaboration (check captured free variables), the `Local` kind (type-level serialization prevention), or linear types (ownership tracking prevents non-serializable captures).

**Depends on**: [[code-mobility.coordination]], [[non-serializable-types.types]]
**Enables**: (none directly)
**Connections**:
- related: [[local-kind.types]] — Local kind would make mobility checking compile-time
- related: [[serialization-boundary.infrastructure]] — current runtime enforcement
- related: [[purity-enforcement.language]] — mobility check is a specific case of effect/capability checking
