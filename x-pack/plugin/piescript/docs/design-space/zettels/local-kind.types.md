---
tags: [types, open, serialization, resources, kinds]
refs:
  - adr:D-050
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Local Kind

Proposed `Local` kind for types that cannot cross node boundaries. `SearcherVal`, `DocRefVal`, `WriterVal` hold JVM-local resources -- currently their non-serializability is enforced at runtime (`IOException` on serialization attempt). A `Local` kind would make this a compile-time guarantee: the type system would reject programs that try to capture Local-kinded values in closures sent to remote nodes.

**Depends on**: [[kind-system.types]], [[non-serializable-types.types]]
**Enables**: (none directly)
**Connections**:
- related: [[code-mobility.coordination]] — would prevent a class of runtime errors; ties into the mobility check concept (currently deferred)
- related: [[qtt-linearity.types]] — linear types (Phase 6) could also track resource ownership
- related: [[ownership.types]] — ownership semantics subsumes Local kind
