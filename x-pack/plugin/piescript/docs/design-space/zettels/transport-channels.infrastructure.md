---
tags: [infrastructure, concept, designed]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transport Channels

**Status**: Speculative alternative — not the current implementation.

Using ES **`TransportService`** alone as the channel mechanism instead of the `ConcurrentHashMap`-based [[channel-registry.infrastructure]] would lean entirely on transport routing and connection management. The **implemented** path is layered: [[transport-layer.es]] documents `TransportService` + piescript actions; [[channel-registry.infrastructure]] holds owner-node listeners; [[transport-send.infrastructure]] completes them via **`TransportPiescriptSendAction`**. A transport-native design would integrate [[transport-send.infrastructure]] at a lower level and might drop the explicit registry — that remains future exploration if we need fewer moving parts.

**Depends on**: [[channel-registry.infrastructure]]
**Enables**: (none directly)
**Connections**:
- alternative-to: [[channel-registry.infrastructure]] — today `ChannelRegistry` is the coordination layer; this zettel tracks replacing it with transport-only routing
- related: [[transport-layer.es]] — documents the actual ES transport stack piescript uses today
- related: [[transport-send.infrastructure]] — would change how `PiescriptSendAction` dispatches relative to registry listeners
