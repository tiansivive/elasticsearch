---
tags: [coordination, language, implemented, theoretical, pi-calculus, decision, concept]
refs:
  - adr:D-040
  - adr:D-015
  - session:f54fd3b6-dcf8-4af9-9af0-6a33818de6ef
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:EvalCoordination.java
---
# Join Calculus Coordination Model

The Join Calculus (Fournet & Gonthier) is the theoretical foundation for piescript's coordination model. It restricts the [[purity.language]] pi-calculus to primitives with efficient distributed implementations:

- Local synchronization (no distributed consensus)
- Asynchronous message passing via [[channels.infrastructure]]
- Reaction rules via [[when-synchronization.coordination]]

The surface primitives ([[spawn.coordination]], `when`, [[send.coordination]], channels) are directly derived from it. Replaced the earlier [[plan-graph.language]] architecture (D-040).

**Depends on**: [[purity.language]]
**Enables**: [[spawn.coordination]], [[when-synchronization.coordination]], [[send.coordination]], [[channels.infrastructure]], [[code-mobility.coordination]]
**Connections**:
- part-of: [[block-a.roadmap]]
- supersedes: [[plan-graph.language]] — superseded D-012 plan graph and par blocks
- implements: keyword `when` (not `join`) avoids SQL/ESQL collision (D-041)
- informs: [[locality-property.coordination]] — the core property that makes distributed join calculus work without consensus
- validates: [[jocaml.comparable]] — closest practical precedent: OCaml + Join Calculus primitives
