---
tags: [language, control-flow, design-pattern, decided, recursion]
refs:
  - thread:language-expressiveness
---
# Implicit Recursion

All bindings are potentially recursive. No `rec` keyword (Haskell-style). The elaborator
extends the de Bruijn environment before evaluating the RHS, so the binding's own name is
in scope within its definition. Simpler than `let rec` — less syntax, less IR, same
semantics.

The value restriction (D-046) is unchanged — it applies to syntactic values regardless of
whether the binding is recursive. In a strict language, `let x = x + 1` reads an
uninitialized slot — caught statically by [[guarded-recursion.technique]].
The runtime [[recursion-sentinel.evaluation]] remains a documented fallback design and is
currently deferred.

**Depends on**: [[evaluator.language]], [[de-bruijn-indices.language]]
**Enables**: (none directly)
**Connections**:
- implements: [[recursion.hub]] — the chosen surface-level recursion model
- uses: [[tying-the-knot.technique]] — mutable slot + backpatch as implementation
- uses: [[guarded-recursion.technique]] — primary safety mechanism
- deferred-by: [[recursion-sentinel.evaluation]] — runtime fallback design, currently deferred
- replaces: [[let-rec-syntax.language]] — implicit recursion makes `let rec` unnecessary
- uses: [[value-restriction.types]] — value restriction unchanged by implicit recursion
- uses: [[elaboration-architecture.types]] — env extension before RHS evaluation
