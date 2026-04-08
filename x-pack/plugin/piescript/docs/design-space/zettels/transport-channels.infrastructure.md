---
tags: [infrastructure, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transport Channels

Using ES `TransportService` directly as the channel mechanism instead of the `ConcurrentHashMap`-based `ChannelRegistry`. `TransportService` already handles message routing, connection management, failure detection. Could eliminate the custom registry layer.

**Depends on**: [[channel-registry.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: [[channel-registry.infrastructure]] — currently `ChannelRegistry` is a thin layer; transport-native channels would be more ES-idiomatic
