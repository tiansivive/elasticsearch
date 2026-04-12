---
tags: [language, effects, coordination, decided, concept, pi-calculus, principle]
refs:
  - plan:scripting_language_design_9286506e
  - adr:D-040
---
# No Monads for Effects

Join Calculus communication replaces monadic effect tracking in piescript. Channels, `spawn`, `when`, and `send` model effects explicitly as coordination primitives -- there is no IO monad, no `do`-notation, and no monadic bind for sequencing effects. Effects are visible in the program structure (coordination primitives are syntactically distinct from pure expressions) rather than tracked in the type system.

This is a fundamental architectural choice: where Haskell uses `IO a` to sequence effects and algebraic effect systems use `Eff [e1, e2] a` to track them, piescript uses process algebra. The evaluator IS the effect handler -- coordination primitives are interpreted by the evaluator, and purity of expressions is maintained by construction rather than by type-level tracking.

The tradeoff: no static effect information means the type system cannot distinguish `Double` (pure) from a computation that spawns processes. The value restriction (D-046) serves as a crude syntactic approximation of effect tracking. Future effect systems remain possible but are not the primary mechanism.

**Depends on**: [[join-calculus.coordination]], [[purity.language]]
**Enables**: (none directly)
**Connections**:
- rejected-in-favor-of: [[join-calculus.coordination]] -- Join Calculus primitives replace monadic effect sequencing
- contrasts-with: [[algebraic-effects.types]] -- algebraic effects model effects through handlers; piescript uses coordination primitives instead
- contrasts-with: [[effect-systems.types]] -- explicit effect tracking in types rejected in favor of implicit purity + coordination
- informs: [[purity.language]] -- purity is maintained by construction (no mutation, only coordination primitives) rather than by monadic encapsulation
- informs: [[evaluator.language]] -- the evaluator is the effect handler; no monadic interpretation layer needed
- uses: [[spawn.coordination]] -- spawn is the effect for forking computation
- uses: [[when-synchronization.coordination]] -- when is the effect for synchronization
- uses: [[send.coordination]] -- send is the effect for message passing
- complements: [[value-restriction.types]] -- value restriction is the crude effect approximation that compensates for not tracking effects in types
- complements: [[free-monad.types]] -- the free monad perspective (D-040) is the theoretical model, but the surface language hides it
- tension-with: [[effect-systems.types]] -- future effect tracking would add what this decision deliberately omits
