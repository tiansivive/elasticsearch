---
tags: [coordination, pi-calculus, distributed, implemented, concept, motivation]
refs:
  - adr:D-040
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:ChannelRegistry.java
---
# Locality Property

Messages travel to their channel's definition site. A `send ch value` on a remote node routes the value to the node where `ch` was created. This is the core [[join-calculus.coordination]] property that makes distributed coordination work without distributed consensus. Implemented via [[channel-registry.infrastructure]]: local sends complete the listener directly; remote sends go through [[transport-send.infrastructure]] to the owner node.

**Depends on**: [[join-calculus.coordination]], [[channel-registry.infrastructure]]
**Enables**: [[send.coordination]], [[code-mobility.coordination]]
**Connections**:
- informs: [[when-synchronization.coordination]] — `when` only works on local channels because synchronization happens at the channel's definition site, not the sender's site
- informs: [[data-locality.distributed]] — locality property is the theoretical foundation for the compute-to-data pattern
- informs: [[transport-send.infrastructure]] — remote sends route through transport to the channel's owner node
