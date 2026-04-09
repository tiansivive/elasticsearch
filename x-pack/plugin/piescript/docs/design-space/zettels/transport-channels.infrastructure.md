---
tags: [infrastructure, open, task]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transport Channels

Using ES `TransportService` directly as the channel mechanism instead of the `ConcurrentHashMap`-based [[channel-registry.infrastructure]]. `TransportService` already handles message routing, connection management, failure detection. Could eliminate the custom registry layer. Would integrate with [[transport-send.infrastructure]] at the transport level rather than going through a separate registry.

**Depends on**: [[channel-registry.infrastructure]]
**Enables**: (none directly)
**Connections**:
- alternative-to: [[channel-registry.infrastructure]] — currently `ChannelRegistry` is a thin layer; transport-native channels would be more ES-idiomatic
- related: [[transport-send.infrastructure]] — would change how PiescriptSendAction dispatches messages
