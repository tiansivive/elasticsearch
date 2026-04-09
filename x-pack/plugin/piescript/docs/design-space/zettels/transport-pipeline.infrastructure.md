---
tags: [infrastructure, async, evaluation, implemented, documentation]
refs:
  - adr:D-004
  - code:TransportPiescriptAction.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transport Pipeline

The full evaluation pipeline:
- Parse ([[antlr-grammar.language]])
- Index resolution pre-pass (async, [[field-caps-resolution.data]])
- Elaborate (bidirectional HM -> [[core-ir.language]])
- Evaluate (async tree-walking via [[evaluator.language]])

Runs on [[generic-thread-pool.infrastructure]] (D-004). The evaluator completes the transport `ActionListener` when done, including after async [[spawn.coordination]]/[[when-synchronization.coordination]] resolution.

**Depends on**: [[evaluator.language]], [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- part-of: [[phase-0.roadmap]]
- uses: [[generic-thread-pool.infrastructure]] — index resolution is async (field caps API); elaboration is synchronous; evaluation is uniformly async; async boundaries require careful thread pool management
- uses: [[antlr-grammar.language]] — first pipeline stage parses piescript source
- uses: [[field-caps-resolution.data]] — index resolution pre-pass resolves field types
- uses: [[elaboration-architecture.types]] — elaboration stage produces Core IR
- uses: [[core-ir.language]] — intermediate representation produced by elaboration, consumed by evaluation
