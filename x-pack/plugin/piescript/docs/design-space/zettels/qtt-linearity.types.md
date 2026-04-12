---
tags: [types, theoretical, someday]
refs:
  - adr:D-018
  - doc:references.md
  - roadmap:phase-6
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:type-foundations
---
# QTT Linearity

QTT-style multiplicities (0, 1, omega) on bindings.

- [[channels.infrastructure|Channel]] endpoints are linear (1). Streams and [[closure-val.language|closures]] remain unrestricted (omega).
- Arrow types extended: `A ->_pi B`. Linear Haskell approach (Bernardy et al. 2018).
- Enables [[session-types.types|session types]], safe mutable references, [[zero-copy-linear-transfer.performance|zero-copy optimization]].

**Depends on**: [[hindley-milner.types]]
**Enables**: [[session-types.types]]
**Connections**:
- part-of: [[future-type-system.roadmap]]
- part-of: [[phase-6.roadmap]]
- implements: Phase 6 — D-018 established the directional decision; multiplicities are static annotations
- prerequisite-for: [[ownership.types]] — QTT enables ownership semantics
- solves: [[non-serializable-types.types]] — linear types could enforce resource safety at compile time instead of runtime
- enables: [[zero-copy-linear-transfer.performance]] — linear closures can be moved not cloned
- contrasts-with: [[stream-fan-out.language]] — linearity for channels, not for streams/lists
- cites: [[linear-haskell.paper]], [[granule-graded-modal.paper]]
