---
tags: [types, theoretical]
refs:
  - doc:references.md
  - roadmap:phase-6
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Session Types

Type-checked communication protocols on channels. Binary session types (Honda et al. 1998). Multiparty session types for multi-process coordination. Wadler's "Propositions as Sessions" — Curry-Howard for concurrency provides deadlock-freedom from the type system.

**Depends on**: [[qtt-linearity.types]], [[channels.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: [[qtt-linearity.types]] — requires linearity (channel endpoints used exactly once per protocol step); Phase 6
