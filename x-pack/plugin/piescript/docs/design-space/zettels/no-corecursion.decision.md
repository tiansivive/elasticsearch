---
tags: [language, codata, recursion, decided, rejected, concept]
refs:
  - plan:scripting_language_design_9286506e
---
# No User-Defined Corecursion

Piescript explicitly forbids user-defined corecursion. There is no `let ones = 1 : ones` -- streams come from queries, ESQL combinators, or Exchange infrastructure, never from recursive user definitions. This avoids the need for thunks, `cofix`, productivity checking, or guardedness analysis for coinductive types.

The decision follows from the KISS principle and the strict evaluation strategy. In a strict language, `let ones = 1 : ones` would require allocating a thunk for the tail -- introducing the exact lazy evaluation machinery that piescript rejects. Rather than carving out a special lazy sublanguage for streams, piescript provides streams through infrastructure (ESQL queries, Exchange pages, channels) where laziness is explicit and bounded.

Plan section 2.10: "No User-Defined Corecursion." Section 3: "No coinductive or inductive type definitions in the type theory."

**Depends on**: [[strict-evaluation.decision]]
**Enables**: (none directly)
**Connections**:
- rejected-in-favor-of: [[stream-a.language]] -- streams come from infrastructure, not user recursion
- informs: [[codata.types]] -- codata exists conceptually (Exchange Sources are operationally codata) but users cannot define coinductive values
- informs: [[guarded-recursion.technique]] -- the guarded recursion check does not need a codata exemption (no coinductive bindings exist)
- informs: [[recursion.hub]] -- constrains the recursion design space; self-referential stream definitions are out
- uses: [[strict-evaluation.decision]] -- strict eval makes corecursion impossible without thunks; this decision ratifies that consequence
- contrasts-with: Haskell -- Haskell allows `ones = 1 : ones` because lazy evaluation provides implicit thunks
- contrasts-with: [[anamorphisms.types]] -- anamorphisms (unfolds) are the categorical dual of folds; piescript provides them only through infrastructure, not user `cofix`
- complements: [[fused-loop-match.language]] -- structured iteration replaces what corecursion would provide for finite sequences
- tension-with: [[lazy-stream.types]] -- a future lazy Stream type would reopen this question if users need to define custom streams
