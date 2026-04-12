---
tags: [paper, types, effects, resources, theoretical, polymorphism, safety, language, reference]
refs:
  - doc:references.md
  - resource:https://doi.org/10.1145/3341714
---
# Orchard et al. -- Quantitative Program Reasoning with Graded Modal Types

Dominic Orchard, Vilem-Benjamin Liepelt, and Harley Eades III. "Quantitative Program Reasoning with Graded Modal Types." *ICFP 2019*.

Graded modal types generalize linearity: instead of just 0/1/omega multiplicities, grades can be drawn from any semiring. This allows tracking not just "how many times" a resource is used, but richer quantitative properties: security levels, sensitivity bounds, approximation guarantees, effect tracking. The Granule language implements this theory.

For piescript, graded modalities are the natural extension beyond QTT-style linearity. Where Linear Haskell (Bernardy et al. 2018) gives 0/1/omega on arrows, graded modalities can track effect costs, data sensitivity, and resource budgets through the same mechanism. This is relevant to piescript's future type system: channel usage could be graded not just linearly but with richer annotations (e.g., "this channel is used at most 3 times" or "this computation has cost O(n)").

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- extends: [[qtt-linearity.types]] -- graded modalities generalize QTT multiplicities from {0,1,omega} to arbitrary semirings
- informs: [[algebraic-effects.types]] -- effect tracking can be expressed as graded modalities
- informs: [[one-shot-continuations.control]] -- one-shot is the grade-1 case; graded types generalize to n-shot
- informs: [[effect-systems.types]] -- graded modalities subsume traditional effect systems
- informs: [[session-types.types]] -- session types can be enriched with quantitative grades
- part-of: [[papers.hub]]
- related: [[linear-haskell.paper]] -- Linear Haskell is the special case with the {0,1,omega} semiring
- related: [[plotkin-pretnar-handlers.paper]] -- algebraic effects + graded modalities = quantitative effect reasoning
