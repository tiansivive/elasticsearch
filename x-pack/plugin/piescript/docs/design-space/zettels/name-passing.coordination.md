---
tags: [coordination, pi-calculus, implemented, concept]
refs:
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:EvalCoordination.java
  - code:ChannelRegistry.java
---
# Name Passing

`Channel(Channel a)` -- passing channel references through [[channels.infrastructure]]. The pi-calculus name-passing pattern. Enables the "setup remote node" pattern:

- Send a closure to a data node via [[code-mobility.coordination]]
- The closure creates a local channel ([[spawn-bang.coordination]])
- Sends its reference back to the coordinator
- Sets up a [[when-synchronization.coordination]] handler
- The coordinator then routes messages to the remote channel via the received reference using [[locality-property.coordination]]

**Depends on**: [[channels.infrastructure]]
**Enables**: [[code-mobility.coordination]]
**Connections**:
- related: [[hindley-milner.types]] — falls out of HM inference automatically; Channel is just a regular type constructor, no special mechanism needed
- uses: [[spawn-bang.coordination]] — spawn! creates the local channel whose reference gets passed through other channels
- uses: [[locality-property.coordination]] — name-passing relies on locality for routing messages to the received channel reference
- uses: [[when-synchronization.coordination]] — when handlers on the received channel reference enable reactive patterns
