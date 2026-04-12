---
tags: [paper, types, pi-calculus, continuation, session-types, theoretical, coordination, concurrency, safety, reference]
refs:
  - doc:references.md
  - resource:https://www.pure.ed.ac.uk/ws/portalfiles/portal/18383989/Wadler_2012_Propositions_as_Sessions.pdf
---
# Wadler -- Propositions as Sessions

Philip Wadler. "Propositions as Sessions." *ICFP 2012*.

Establishes a Curry-Howard correspondence for concurrency: propositions in classical linear logic correspond to session types, proofs correspond to processes, and cut elimination corresponds to communication. The correspondence gives deadlock-freedom and protocol compliance for free from the type system -- well-typed programs cannot deadlock because they correspond to valid proofs in linear logic.

Called "the most relevant paper for piescript's future type system" in references.md. If piescript channels are typed with session types derived from linear logic, the type system would statically guarantee that coordinating processes communicate correctly and terminate without deadlocks.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- formalizes: [[session-types.types]] -- linear logic / session type correspondence
- formalizes: [[qtt-linearity.types]] -- linearity as a logical discipline on channel endpoints
- informs: [[delimited-continuations.hub]] -- cut elimination as communication relates to continuation-passing
- informs: [[channels.infrastructure]] -- channels as typed endpoints with session protocols
- part-of: [[papers.hub]]
- related: [[honda-session-types.paper]] -- Honda's original session types; Wadler provides the logical foundation
- extends: [[honda-session-types.paper]] -- gives logical underpinning to Honda's session types
- related: [[join-calculus.coordination]] -- process calculus formalization of coordination
