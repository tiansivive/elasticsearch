---
tags: [infrastructure, coordination, mobility, channels, implemented]
refs:
  - adr:D-045
  - adr:D-047
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Inbox

Well-known "inbox" channel on every node. Receives closures, evaluates them asynchronously with local node info as the lambda argument. Fire-and-forget: transport response returns before evaluation. Evaluation errors logged at WARN on target node, never propagated to sender. Topology node records include an `inbox` field.

**Depends on**: [[channel-registry.infrastructure]], [[fire-and-forget.coordination]], [[closure-val.language]]
**Enables**: [[code-mobility.coordination]]
**Connections**:
- related: dependency injection via lambda abstraction — the inbox argument type can widen as the language evolves
- related: [[topology.infrastructure]] — topology node records include an `inbox` field for dispatch
- related: [[inbox-dependency-injection.coordination]] — the DI pattern for how inbox closures receive local capabilities
- related: [[transport-send.infrastructure]] — PiescriptSendAction handles inbox dispatch (validate closure, fire-and-forget evaluation)
