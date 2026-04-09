---
tags: [coordination, language, implemented, pi-calculus, documentation]
refs:
  - adr:D-045
  - adr:D-047
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Send

`send channel value` delivers a value to a [[channels.infrastructure|channel]].

- [[fire-and-forget.coordination|Fire-and-forget]]: returns `Null` immediately (D-047).
- Local channels: completes `SubscribableListener` in [[channel-registry.infrastructure|ChannelRegistry]].
- Remote channels: [[serialization.infrastructure|serializes]] value, sends transport message to owner node.
- [[inbox.infrastructure|Inbox]] sends always go through transport even locally.

**Depends on**: [[channels.infrastructure]], [[channel-registry.infrastructure]], [[fire-and-forget.coordination]], [[serialization.infrastructure]]
**Enables**: [[code-mobility.coordination]], [[inbox.infrastructure]]
**Connections**:
- part-of: [[block-c.roadmap]]
- implements: [[fire-and-forget.coordination]] — error responsibility split: delivery errors -> initiator, evaluation errors -> target node (D-047)
- uses: [[locality-property.coordination]] — send routes values to the channel's definition site based on the locality property
- implements: [[transport-send.infrastructure]] — remote sends go through PiescriptSendAction transport handler
