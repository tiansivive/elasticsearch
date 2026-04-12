---
tags: [paper, types, continuation, resources, polymorphism, theoretical, safety, language, reference]
refs:
  - adr:D-018
  - doc:references.md
---
# Bernardy et al. -- Linear Haskell

Jean-Philippe Bernardy, Mathieu Boespflug, Ryan R. Newton, Simon Peyton Jones, and Arnaud Spiwack. "Linear Haskell: Practical Linearity in a Higher-Order Polymorphic Language." *POPL 2018*.

Key insight: linearity on arrows, not types. A function `f :: a ->_1 b` promises to consume its argument exactly once; the types `a` and `b` themselves are not "linear types." This retrofits linearity into a language with unrestricted polymorphism -- existing code is unaffected, linearity is opt-in at call sites. Directly referenced as D-018 for piescript's QTT-style multiplicities.

Piescript adopts this arrow-based approach: `A ->_pi B` where `pi` is 0, 1, or omega. Channel endpoints are linear (multiplicity 1), streams and closures remain unrestricted (omega). This enables session types, safe mutable references, and zero-copy optimization without requiring a wholesale type system redesign.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- formalizes: [[qtt-linearity.types]] -- the arrow-based linearity approach piescript adopts
- informs: [[one-shot-continuations.control]] -- one-shot = linear multiplicity 1; linear arrows guarantee single use
- informs: [[session-types.types]] -- linearity on channel endpoints enables session type protocols
- informs: [[ownership.types]] -- QTT multiplicities enable ownership semantics
- informs: [[zero-copy-linear-transfer.performance]] -- linear closures can be moved not cloned
- part-of: [[papers.hub]]
- related: [[granule-graded-modal.paper]] -- Granule generalizes linearity to graded modalities
