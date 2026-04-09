---
tags: [es-internals, compute-engine, columnar, infrastructure, documentation, implemented]
refs:
  - adr:D-054
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/operator/exchange/ExchangeService.java
  - code:EvalExchange.java
---
# Compute Exchange: ExchangeService

**`ExchangeService`** manages **exchange** endpoints for streaming **`Page`** batches between producers and consumers (local or remote). Sinks and sources are created from a serializable exchange identity; piescript’s **`Exchange.*`** builtins delegate here via `EvalExchange` and injected **`ExchangeService`** on [[eval-dependencies.language]].

**Depends on**: [[compute-engine.es]], [[compute-data-page-block.es]]

**Enables**: [[exchange-streaming.infrastructure]]

**Connections**:
- part-of: [[compute-engine.es]]
- implements: [[exchange-streaming.infrastructure]] — ES backend for piescript exchange builtins
- constrains: [[column-name-derivation.types]] — runtime column list vs row type
- uses: [[serialization.infrastructure]] — exchange descriptors cross the wire
