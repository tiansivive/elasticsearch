---
tags: [ml, external, coordination, exploration, concept]
refs: []
---
# Inference Orchestration

Fan out model application across nodes, collect and post-process results. ES
already has trained model inference (the `_inference` API and ML nodes).
Piescript would not implement inference kernels, but could coordinate them:
query data, route batches to inference endpoints, collect predictions, join
with source data, write enriched results.

Combines piescript's `topology` + `send` with ES's existing ML infrastructure.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[ml-workflow-integration.ml]]
- related: [[es-ml-integration.ml]] — routing closures to ML nodes specifically
- uses: [[topology.infrastructure]] — discover ML nodes via topology
- uses: [[send.coordination]] — ship inference requests to ML nodes
