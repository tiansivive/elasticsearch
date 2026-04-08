---
tags: [coordination, language, implemented, pi-calculus]
refs:
  - adr:D-045
  - adr:D-047
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Send

`send channel value` delivers a value to a channel. Fire-and-forget: returns `Null` immediately (D-047). Local channels: completes `SubscribableListener` in `ChannelRegistry`. Remote channels: serializes value, sends transport message to owner node. Inbox sends always go through transport even locally.

**Depends on**: [[channels.infrastructure]], [[channel-registry.infrastructure]], [[fire-and-forget.coordination]], [[serialization.infrastructure]]
**Enables**: [[code-mobility.coordination]], [[inbox.infrastructure]]
**Connections**:
- related: [[fire-and-forget.coordination]] — error responsibility split: delivery errors -> initiator, evaluation errors -> target node (D-047)
- related: [[locality-property.coordination]] — send routes values to the channel's definition site based on the locality property
- related: [[transport-send.infrastructure]] — remote sends go through PiescriptSendAction transport handler
