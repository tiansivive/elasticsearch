---
tags: [paper, types, pi-calculus, coordination, theoretical, concurrency, channels, safety, protocol, reference]
refs:
  - doc:references.md
  - resource:https://filipendule.github.io/mgs/honda.vasconcelos.kubo.pdf
---
# Honda, Vasconcelos, Kubo -- Language Primitives for Structured Communication-Based Programming

Kohei Honda, Vasco T. Vasconcelos, and Makoto Kubo. "Language Primitives and Type Discipline for Structured Communication-Based Programming." *ESOP 1998*.

The original session types paper. Introduces binary session types: types that describe the protocol on a channel (send int, then receive string, then done). A session type is a sequence of communication actions -- the dual endpoints must agree on the protocol, and the type system enforces this statically.

For piescript, binary session types are the first step toward typed channel protocols. Currently piescript channels are typed with `Channel t` (carrying a single value of type `t`). Session types would extend this to describe multi-step protocols: `!Int.?String.end` means "send an Int, receive a String, then close." This enables static verification that coordinating processes follow the expected protocol.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- formalizes: [[session-types.types]] -- the original formalization of session types
- informs: [[channels.infrastructure]] -- channels as typed protocol endpoints
- informs: [[when-synchronization.coordination]] -- `when` as a reception action in a session protocol
- part-of: [[papers.hub]]
- related: [[milner-pi-calculus.paper]] -- session types add a typing discipline to Milner's pi-calculus
- related: [[wadler-propositions-as-sessions.paper]] -- Wadler gives Honda's session types a logical foundation
- extended-by: [[honda-multiparty-sessions.paper]] -- multiparty extension
