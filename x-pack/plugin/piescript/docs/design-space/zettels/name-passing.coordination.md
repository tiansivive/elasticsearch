---
tags: [coordination, pi-calculus, implemented]
refs:
  - adr:D-045
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Name Passing

Channel(Channel a) — passing channel references through channels. The pi-calculus name-passing pattern. Enables the "setup remote node" pattern: send a closure to a data node, the closure creates a local channel (spawn!), sends its reference back to the coordinator, and sets up a when handler. The coordinator then routes messages to the remote channel via the received reference.

**Depends on**: [[channels.infrastructure]]
**Enables**: [[code-mobility.coordination]]
**Connections**:
- related: [[hindley-milner.types]] — falls out of HM inference automatically; Channel is just a regular type constructor, no special mechanism needed
- related: [[spawn-bang.coordination]] — spawn! creates the local channel whose reference gets passed through other channels
- related: [[locality-property.coordination]] — name-passing relies on locality for routing messages to the received channel reference
