---
tags: [paper, pi-calculus, coordination, theoretical, concurrency, language, channels, reference]
refs:
  - doc:references.md
  - resource:http://www.lfcs.inf.ed.ac.uk/reports/89/ECS-LFCS-89-85/
---
# Milner, Parrow, Walker -- A Calculus of Mobile Processes

Robin Milner, Joachim Parrow, and David Walker. "A Calculus of Mobile Processes, Parts I and II." *Information and Computation*, 1992.

The founding pi-calculus paper. Part I defines the calculus and strong bisimulation. Part II covers weak bisimulation and equational theory. Introduces the pi-calculus as an extension of CCS that naturally expresses processes with changing communication structure through name passing -- the key innovation is that channel names can be communicated, allowing the topology of communication to evolve dynamically.

This is the parent theory for everything in piescript's coordination model. The join calculus (Fournet & Gonthier) restricts pi-calculus to primitives with efficient distributed implementations. Piescript's `spawn`, `send`, `when`, and channel primitives all trace their lineage to this paper. The concept of scope extrusion -- a private name escaping its original scope by being communicated -- is the theoretical basis for piescript's channel reference passing.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- formalizes: [[join-calculus.coordination]] -- the join calculus is a restriction of pi-calculus for distributed implementation
- informs: [[channels.infrastructure]] -- channels as first-class names that can be communicated
- informs: [[spawn.coordination]] -- process creation in pi-calculus
- informs: [[send.coordination]] -- output action in pi-calculus
- informs: [[name-passing.coordination]] -- name passing is the defining feature of pi-calculus
- informs: [[code-mobility.coordination]] -- Sangiorgi later showed agent passing encodes in name passing
- part-of: [[papers.hub]]
- related: [[sangiorgi-agent-passing.paper]] -- Sangiorgi builds on this to show higher-order pi encodes in first-order
- related: [[honda-session-types.paper]] -- Honda adds typing discipline to pi-calculus channels
