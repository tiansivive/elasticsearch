---
tags: [paper, pi-calculus, mobility, distributed, serialization, theoretical, coordination, concurrency, reference]
refs:
  - doc:references.md
  - resource:https://www.semanticscholar.org/paper/pi-Calculus%2C-Internal-Mobility%2C-and-Agent-Passing-Sangiorgi/80159843149f602792d36d6c3e65f72bc8b48822
---
# Sangiorgi -- Pi-Calculus, Internal Mobility, and Agent-Passing Calculi

Davide Sangiorgi. "Pi-Calculus, Internal Mobility, and Agent-Passing Calculi." *Theoretical Computer Science*, 1996.

Shows that higher-order pi-calculus (where processes/code are sent over channels, not just names) can be faithfully encoded in the first-order pi-calculus. This means code mobility is just name passing -- traveling closures do not require a fundamentally new calculus. Directly relevant to piescript: `ClosureVal` serialization and code shipping to data nodes is theoretically justified as name passing within standard pi-calculus.

The paper distinguishes internal mobility (scope extrusion of bound names) from external mobility (agent/process passing), showing both reduce to the same expressive power. This validates piescript's approach of treating code mobility as a derived concept rather than a primitive.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- formalizes: [[code-mobility.coordination]] -- theoretical justification: code mobility reduces to name passing
- formalizes: [[name-passing.coordination]] -- name passing as the primitive from which agent passing derives
- informs: [[closure-val.language]] -- traveling closures are the concrete realization of agent passing
- informs: [[serialization.infrastructure]] -- serialization is the implementation of agent passing over a network
- informs: [[channels.infrastructure]] -- channels carry both names and (encoded) agents
- part-of: [[papers.hub]]
- related: [[milner-pi-calculus.paper]] -- builds on Milner's foundational pi-calculus
- related: [[nomadic-pict.coordination]] -- Nomadic Pict implements the agent-passing model with location tracking
