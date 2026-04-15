---
tags: [recursion, types, technique, concept]
refs: []
---
# Guarded Recursion

Static detection of invalid recursive self-references. In a `let` binding's RHS, any
reference to the binding itself (de Bruijn index 0) that is NOT under a lambda abstraction
is an immediate self-reference — the value is accessed before it's computed. A reference
UNDER a lambda is deferred (only evaluated when the function is called) and thus safe.

**The check:** discriminate binders by origin ("let" vs "lambda"). On variable lookup during
elaboration, if index is 0 and the binder is a let binder and we're not under a lambda,
reject statically: "binding accessed during own initialization."

Examples:
- `let f = fn x -> f x` — safe: self-reference `f` is under `fn x ->` (deferred)
- `let x = x + 1` — invalid: self-reference `x` is immediate (not under a lambda)
- `let pair = { a: pair, b: 1 }` — invalid: self-reference `pair` is immediate in record

This is OCaml's approach to `let rec` — the RHS must be "guarded" (all self-references
under a lambda or constructor). The formal name is the guarded recursion check or
syntactic-value restriction for recursive bindings.

**Strictly better than runtime sentinel:** catches errors at elaboration time (zero runtime
cost, better error messages). Guarded recursion is the enforcement mechanism;
the [[recursion-sentinel.evaluation]] remains a fallback design and is deferred.

**Codata changes the picture:** with coinductive types ([[codata.types]]), `let x = x + 1`
WOULD be valid — it defines an observation-based infinite value. Guarded recursion would
need to be relaxed for codata bindings.

**Depends on**: [[implicit-recursion.design]], [[elaboration-architecture.types]]
**Enables**: (none directly)
**Connections**:
- part-of: [[recursion.hub]]
- implements: [[implicit-recursion.design]] — the static safety check that makes implicit recursion sound
- supersedes: [[recursion-sentinel.evaluation]] — static check is primary; runtime sentinel is deferred fallback
- uses: [[de-bruijn-indices.language]] — detection relies on index 0 being the current let binding
- uses: [[elaboration-architecture.types]] — check runs during elaboration, not evaluation
- tension-with: [[codata.types]] — codata would relax guarded recursion for coinductive bindings
- analogous-to: OCaml's guarded recursion check for `let rec`
