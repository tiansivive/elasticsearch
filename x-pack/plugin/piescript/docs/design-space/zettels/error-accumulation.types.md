---
tags: [types, language, inference, open, needs-design, later, concept]
refs:
  - adr:D1.14
---
# Error Accumulation

The elaborator currently uses fail-fast error reporting (D1.14): the first type error aborts elaboration and reports a single `ElaborationException`. This is simple to implement but provides poor UX for programs with multiple independent errors -- the user fixes one error, re-runs, discovers the next, and iterates.

Future improvement: accumulate multiple type errors during elaboration and report them all at once. This requires changing the elaborator from fail-fast (throw on first error) to collect-and-continue (record the error, substitute a placeholder type or error type, continue elaborating). The challenge is deciding what to do after an error: inserting an error sentinel type prevents cascading errors but may cause the elaborator to produce misleading subsequent errors if the sentinel interacts with unification.

GHC's approach: collect errors, use `TyErr` as a sentinel type that unifies with everything (preventing cascading errors), report all collected errors at the end. Rust's approach: similar, with `!` (never type) or error type as sentinel. Both require careful design to avoid "error avalanche" where one real error triggers dozens of cascading false positives.

**Depends on**: [[type-errors.types]], [[elaboration-architecture.types]]
**Enables**: (none directly)
**Connections**:
- extends: [[type-errors.types]] -- enriches the error reporting surface from single-error to multi-error
- informs: [[elaboration-architecture.types]] -- changes the elaboration loop from fail-fast to collect-and-continue
- related: [[unification-algorithm.types]] -- unification failure must produce a recoverable error, not abort
- related: [[deferred-constraints.types]] -- deferred constraints already accumulate work; error accumulation extends this to error collection
- analogous-to: GHC's `TyErr` sentinel type approach
- analogous-to: Rust's error recovery in type checking
