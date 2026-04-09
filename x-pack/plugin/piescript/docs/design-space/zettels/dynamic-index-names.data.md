---
tags: [data, types, open, concept, question]
refs:
  - vision:data-access
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Dynamic Index Names

When the index is a runtime value, schema depends on external cluster state. Honest type is Dynamic with explicit narrowing. Options: GADT-based refinement (OutsideIn(X)), CPS-style validation, Reflect [[typeclasses.types]].

Three approaches compared:

(a) **GADT-based refinement via OutsideIn(X)** — Pattern matching on a GADT constructor that carries a type equality witness narrows the type in the branch body. For example, `ValidIndex idx proof -> ...` where `proof : idx ~ Index r` lets the branch body use `r` as a known row type. This is the most principled approach — it composes with the rest of the type system and requires no special cases — but it requires the full OutsideIn(X) constraint solver with local type refinement in case branches.

(b) **CPS-style validation** — `withIndex name (\idx -> body)` where the continuation receives a typed `Index r` if validation succeeds (and an error path otherwise). Simpler to implement because it avoids GADTs entirely: the `withIndex` function is an ordinary polymorphic function whose callback is existentially quantified over `r`. The trade-off is that it forces CPS style on the user, nesting validation callbacks when multiple dynamic names are involved.

(c) **Reflect typeclass** — `reflect : Keyword -> Dynamic` followed by typeclass-based narrowing (`narrow : (Reflect a) => Dynamic -> Maybe a`). Most flexible — works for any type, not just indices — but requires the typeclass machinery to be in place and pushes type errors to runtime (the `Maybe` can be `Nothing`).

All three are future work. Static `use` declarations cover the common case where the index name is known at script-writing time and the mapping can be fetched during elaboration.

**Depends on**: [[use-declarations.data]], [[typeclasses.types]]
**Enables**: (none directly)
**Connections**:
- contrasts-with: [[use-declarations.data]] — static names (via `use`) get full type safety; dynamic names are the escape hatch
- related: [[typeclasses.types]] — Reflect typeclass approach requires typeclass machinery
- tradeoff-with: [[gadt-rejection.types]] — GADT-based refinement option would require OutsideIn(X)
