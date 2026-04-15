---
tags: [types, continuation, theoretical, concept]
refs:
  - resource:https://doi.org/10.1007/978-3-540-73228-0_3
---
# Answer-Type Polymorphism

Filinski's insight: the answer type of a delimited continuation can be polymorphic. Without it, every use of shift/reset must agree on the final answer type, making composition painful. With it, delimited continuations compose freely. Critical for typing algebraic effect handlers -- each handler transforms the answer type independently.

The answer type is "what the overall reset expression returns." Without polymorphism, if one library's handler produces `Int` and another's produces `String`, they cannot be composed. Answer-type polymorphism quantifies over this type, enabling handlers to be written generically.

Resources: Asai & Kameyama "Polymorphic Delimited Continuations" (2007), Filinski "Representing Monads" (1994).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[delimited-continuations.hub]]
- enables: [[algebraic-effects.types]] -- effect handler typing needs answer-type polymorphism
- extends: [[hindley-milner.types]] -- answer type is an additional type parameter in the continuation type
- uses: [[higher-rank.types]] -- answer-type polymorphism may require rank-2 types in some formulations
- explored-for: [[fused-loop-match.language]] — studied for piescript's loop/repeat typing via yap compiler reference. Not fully solved: repeat doesn't change the answer type (unlike shift). The dual-type tracking insight is valuable but the mechanism needs adaptation. See [[repeat-design-exploration.note]].
