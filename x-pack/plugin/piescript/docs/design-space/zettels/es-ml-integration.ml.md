---
tags: [ml, external, exploration, someday]
refs:
  - thread:external-interaction
---
# ES ML Infrastructure Integration

ES has ML nodes with dedicated thread pools and trained model inference
capabilities. Piescript's `topology` + `send` could route closures to ML
nodes specifically, combining piescript's coordination model with ES's
inference infrastructure:

```
let mlNodes = topology "cluster" |> nodes |> filter (fn n -> n.role == "ml")
let target = head mlNodes
let resultCh = spawn!
in send target.inbox (fn () ->
  let data = from r in idx where r.needs_scoring
  let scored = infer "my-model" data
  in send resultCh scored
)
in when (resultCh results) -> writeTo scored_index results
```

This is an integration story, not an implementation story — piescript
orchestrates, ES's ML infrastructure executes.

**Depends on**: [[topology.infrastructure]], [[send.coordination]]
**Enables**: (none directly)
**Connections**:
- part-of: [[ml-workflow-integration.ml]]
- related: [[inference-orchestration.ml]] — this is the concrete ES-specific version
- related: [[plugin-spi.external]] — ML inference could also be exposed via plugin SPI
