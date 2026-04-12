---
tags: [types, polymorphism, decided, rejected, concept, inference]
refs:
  - plan:scripting_language_design_9286506e
---
# Monomorphism Restriction Rejected

The Haskell monomorphism restriction is explicitly rejected for piescript. The restriction prevents generalization of bindings that are not syntactic functions -- widely considered a mistake in Haskell that causes confusing type errors and unexpected monomorphization. Piescript generalizes all syntactic values regardless of their form.

The value restriction (D-046) is the correct mechanism for preventing unsound generalization. It restricts generalization based on syntactic-value detection (is the RHS a value or a computation?) rather than the monomorphism restriction's function-vs-non-function distinction. The value restriction prevents `let ch = spawn!` from generalizing `Channel ?a` to `forall a. Channel a` -- the actual soundness concern -- without penalizing innocent non-function bindings like `let id = fn x -> x` applied to nothing.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- rejected-in-favor-of: [[value-restriction.types]] -- value restriction is the correct mechanism for controlling generalization
- informs: [[binding-levels.types]] -- binding-level generalization proceeds without the monomorphism restriction's extra guard
- informs: [[implicit-recursion.design]] -- implicit recursion combined with unrestricted generalization means all let bindings are potentially polymorphic
- contrasts-with: Haskell -- Haskell's monomorphism restriction was a pragmatic choice for defaulting; piescript avoids it entirely
- complements: [[hindley-milner.types]] -- HM inference generalizes at let boundaries; piescript does so without the monomorphism restriction
- related: [[effect-systems.types]] -- the monomorphism restriction is sometimes justified as a crude effect proxy; piescript uses value restriction instead
