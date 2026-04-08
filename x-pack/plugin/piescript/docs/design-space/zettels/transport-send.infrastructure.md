---
tags: [infrastructure, serialization, es-internals, implemented]
refs:
  - adr:D-045
  - adr:D-055
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Transport Send Action

`PiescriptSendAction` (`internal:compute/piescript/send`). Single handler for all cross-node communication. Request carries (`channelId`, serialized `Value`). Handler dispatches: regular channels -> complete listener, inbox -> validate closure -> fire-and-forget evaluation.

**Depends on**: [[channel-registry.infrastructure]], [[serialization.infrastructure]]
**Enables**: [[send.coordination]], [[inbox.infrastructure]]
**Connections**:
- related: value-agnostic, channel-agnostic — no separate "execute closure" handler
- related: [[send.coordination]] — transport send is the implementation of remote `send` operations
- related: [[inbox.infrastructure]] — dispatches inbox messages (validate closure, fire-and-forget evaluation)
- related: [[locality-property.coordination]] — routes messages to the channel's owner node
