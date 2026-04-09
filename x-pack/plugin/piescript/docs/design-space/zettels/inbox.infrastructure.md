---
tags: [infrastructure, coordination, mobility, channels, implemented, documentation]
refs:
  - adr:D-045
  - adr:D-047
  - code:TransportPiescriptSendAction.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Inbox

Well-known "inbox" [[channels.infrastructure]] on every node. Receives [[closure-val.language]], evaluates them asynchronously with local node info as the lambda argument.
- [[fire-and-forget.coordination]]: transport response returns before evaluation
- Evaluation errors logged at WARN on target node, never propagated to sender
- [[topology.infrastructure]] node records include an `inbox` field

**Depends on**: [[channel-registry.infrastructure]], [[fire-and-forget.coordination]], [[closure-val.language]]
**Enables**: [[code-mobility.coordination]]
**Connections**:
- part-of: [[block-c.roadmap]]
- implements: [[inbox-dependency-injection.coordination]] — dependency injection via lambda abstraction; the inbox argument type can widen as the language evolves
- uses: [[topology.infrastructure]] — topology node records include an `inbox` field for dispatch
- uses: [[inbox-dependency-injection.coordination]] — the DI pattern for how inbox closures receive local capabilities
- uses: [[transport-send.infrastructure]] — PiescriptSendAction handles inbox dispatch (validate closure, fire-and-forget evaluation)
