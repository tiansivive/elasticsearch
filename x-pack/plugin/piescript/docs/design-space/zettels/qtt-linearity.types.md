---
tags: [types, theoretical]
refs:
  - adr:D-018
  - doc:references.md
  - roadmap:phase-6
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# QTT Linearity

QTT-style multiplicities (0, 1, ω) on bindings. Channel endpoints are linear (1). Streams and closures remain unrestricted (ω). Arrow types extended: A →_π B. Linear Haskell approach (Bernardy et al. 2018). Enables session types, safe mutable references, zero-copy optimization.

**Depends on**: [[hindley-milner.types]]
**Enables**: [[session-types.types]]
**Connections**:
- related: Phase 6 — D-018 established the directional decision; multiplicities are static annotations
- related: [[ownership.types]] — QTT enables ownership semantics
- related: [[non-serializable-types.types]] — linear types could enforce resource safety at compile time instead of runtime
- enables: [[zero-copy-linear-transfer.performance]] — linear closures can be moved not cloned
