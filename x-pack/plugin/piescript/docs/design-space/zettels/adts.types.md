---
tags: [types, language, open, feature, concept, needs-design, next]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:error-handling
  - thread:language-expressiveness
---
# Algebraic Data Types

User-defined algebraic data types (sum + product types). Required for proper null handling (Option/Maybe), error handling (Result/Either), and domain modeling. Currently piescript has no sum types — only records (products) and sealed `Value` variants.

**GADTs were considered and rejected** for the ESQL stats typing problem (D-053 design session). The alternative — type-level computation via `force`/`&`/`Pick`/`Omit` (F-omega-lite) — won because it solves the row-merging problem without the complexity of OutsideIn(X) type inference that GADTs require. See [[gadt-rejection.types]]. Plain ADTs remain desirable but are independent of that decision.

**Depends on**: [[hindley-milner.types]]
**Enables**: [[result-types.types]], [[null-as-bottom.types]]
**Connections**:
- part-of: [[future-type-system.roadmap]]
- contrasts-with: [[gadt-rejection.types]] — GADTs would require OutsideIn(X); [[f-omega-lite.types]] approach handles what GADTs would have needed
- complements: [[pattern-matching.hub]] — constructor patterns require ADTs, but basic pattern matching (Boolean, literals, wildcards) is independent; neither blocks the other
- prerequisite-for: [[recursive-types.types]] — iso-recursive types require ADTs (fold/unfold are constructors)
- prerequisite-for: [[type-narrowing.types]] — ADTs enable TypeScript-style type refinement via pattern matching
