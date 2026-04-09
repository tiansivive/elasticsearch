---
tags: [infrastructure, serialization, es-internals, implemented, documentation]
refs:
  - adr:D-045
  - adr:D-055
  - code:PiescriptSendAction.java
  - code:TransportPiescriptSendAction.java
  - code:PiescriptSendRequest.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transport Send Action

`PiescriptSendAction` (`internal:compute/piescript/send`). Single handler for all cross-node communication:
- Request carries (`channelId`, serialized `Value`)
- Handler dispatches: regular [[channels.infrastructure]] -> complete listener, [[inbox.infrastructure]] -> validate [[closure-val.language]] -> [[fire-and-forget.coordination]] evaluation

**Depends on**: [[channel-registry.infrastructure]], [[serialization.infrastructure]]
**Enables**: [[send.coordination]], [[inbox.infrastructure]]
**Connections**:
- part-of: [[block-c.roadmap]]
- constrains: value-agnostic, channel-agnostic — no separate "execute closure" handler
- implements: [[send.coordination]] — transport send is the implementation of remote `send` operations
- implements: [[inbox.infrastructure]] — dispatches inbox messages (validate closure, fire-and-forget evaluation)
- uses: [[locality-property.coordination]] — routes messages to the channel's owner node
