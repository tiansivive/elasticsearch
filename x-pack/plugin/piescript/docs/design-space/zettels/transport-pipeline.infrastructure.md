---
tags: [infrastructure, async, evaluation, implemented]
refs:
  - adr:D-004
  - code:TransportPiescriptAction.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transport Pipeline

The full evaluation pipeline: parse (ANTLR) -> index resolution pre-pass (async, field caps) -> elaborate (bidirectional HM -> Core IR) -> evaluate (async tree-walking). Runs on GENERIC thread pool (D-004). The evaluator completes the transport `ActionListener` when done, including after async spawn/when resolution.

**Depends on**: [[evaluator.language]], [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: [[generic-thread-pool.infrastructure]] — index resolution is async (field caps API); elaboration is synchronous; evaluation is uniformly async; async boundaries require careful thread pool management
