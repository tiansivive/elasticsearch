---
tags: [es-internals, transport-layer, infrastructure, documentation, concept, implemented]
refs:
  - adr:D-004
  - adr:D-045
  - adr:D-055
  - code:server/src/main/java/org/elasticsearch/transport/TransportService.java
  - code:server/src/main/java/org/elasticsearch/action/support/HandledTransportAction.java
  - code:x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/TransportPiescriptAction.java
  - code:x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/TransportPiescriptSendAction.java
---
# Elasticsearch Transport Layer (piescript touchpoints)

Elasticsearch’s **`org.elasticsearch.transport`** stack moves **requests between nodes** over the binary transport protocol. **`TransportService`** is the coordinator: outbound **`sendRequest`**, inbound **handler registration** for `TransportRequest`/`TransportResponse` pairs, and connection lifecycle. User-visible work often flows through **`HandledTransportAction`** (subclasses run on the appropriate thread pool); **internal** actions use the same machinery with `internal:` action names.

Piescript installs **two** transport actions: **`TransportPiescriptAction`** for **`cluster:compute/piescript`** ([[transport-pipeline.infrastructure]] — parse → index resolution → elaborate → evaluate), and **`TransportPiescriptSendAction`** for **`internal:compute/piescript/send`** ([[transport-send.infrastructure]] — complete a channel or inbox on the **owner** node). Both receive **`TransportService`** via Guice. Remote **`send`** resolves the target node and calls **`TransportService.sendRequest`** so the message executes where the channel was created ([[locality-property.coordination]]). See [[transport-pipeline-evolution.infrastructure]] for how the eval pipeline landed on GENERIC threads (D-004), [[security-namespace.infrastructure]] for cluster vs internal namespaces (D-055), and [[transport-versioning.infrastructure]] for missing **`TransportVersion`** guards on piescript payloads.

**Depends on**: (none)

**Enables**: (none — hub)

**Connections**:
- documents: [[transport-pipeline.infrastructure]] — main eval entry (`TransportPiescriptAction`)
- documents: [[transport-send.infrastructure]] — internal send (`TransportPiescriptSendAction`)
- documents: [[channel-registry.infrastructure]] — handler completes registry listeners
- informs: [[block-c.roadmap]] — cross-node stack
- constrains: [[transport-versioning.infrastructure]] — wire evolution story
- related: [[serialization.infrastructure]] — `PiescriptSendRequest` body
- related: [[security-namespace.infrastructure]] — action naming and auth
- related: [[generic-thread-pool.infrastructure]] — pool choice vs transport threads (D-004)
