---
tags: [language, evaluation, safety, implemented, decision, concept]
refs:
  - adr:D-025
  - code:Evaluator.java
---
# Evaluator Trusts Typechecker

The principle that runtime type mismatches in the [[evaluator.language]] are `AssertionError` (internal bug), not user-facing errors.
- The type checker is the single source of type truth; the evaluator assumes well-typed input and does not re-check types at runtime
- If a cast fails or an unexpected variant appears, that signals a bug in the [[elaboration-architecture.types]], not a user mistake
- This keeps the evaluator simple and fast, with all user-facing type errors caught during elaboration

**Depends on**: [[hindley-milner.types]], [[elaboration-architecture.types]]
**Enables**: (none directly)
**Connections**:
- complements: [[hindley-milner.types]] — HM inference guarantees well-typedness; the evaluator relies on that guarantee
- uses: [[elaboration-architecture.types]] — elaboration is the phase that catches all type errors before evaluation
