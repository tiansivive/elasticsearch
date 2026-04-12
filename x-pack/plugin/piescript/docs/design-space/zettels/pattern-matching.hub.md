---
tags: [language, control-flow, hub, feature, ready]
refs:
  - adr:D-010
  - adr:D-029
  - thread:error-handling
  - thread:language-expressiveness
---
# Pattern Matching

Match expressions as the primary control-flow mechanism (D-010). `if`/`then`/`else` is sugar
for matching on Boolean. 
Critical unblock for [[recursion.hub]] — any loop/recursion needs
branching. 
Independent of [[adts.types]] — basic matching (Boolean, literals, wildcards, records,
lists) requires no sum types. 
Constructor patterns arrive with ADTs later.

**Includes**: [[match-syntax.language]], [[pattern-types.language]], [[match-type-checking.language]], [[pattern-reuse.language]], [[type-level-matching.types]], [[core-match.language]]

**Depends on**: [[hindley-milner.types]], [[row-polymorphism.types]]
**Enables**: [[recursion.hub]], [[result-types.types]]
**Connections**:
- implements: D-010 — match-first philosophy
- implements: D-029 — open rows before pattern matching
- complements: [[adts.types]] — constructor patterns require ADTs; basic patterns are independent
- complements: [[type-narrowing.types]] — pattern matching is the primary mechanism for type refinement
- complements: [[existential-types.types]] — matching on existentials reveals hidden types
- enhances: [[when-synchronization.coordination]] — destructuring in channel bindings
- enhances: [[currying.language]] — destructuring in lambda params
- prerequisite-for: [[catamorphisms.types]] — fold/unfold over recursive types are typed pattern matching; catamorphisms generalize match to arbitrary ADTs
- prerequisite-for: [[null-as-bottom.types]] — proper Option/Maybe requires matching to be useful
- informs: [[branch-merging-optimization.performance]] — optimization over match branches (merging, dead-branch elimination)
- specializes: [[curry-narrowing.language]] — pattern matching is a specific case of Curry-style narrowing (functions as patterns)
- specializes: [[cham-patterns.coordination]] — value-level pattern matching generalizes to functional patterns over multi-value channel stores (CHAM); `when` rules with patterns over message stores are a coordination-domain generalization of `match`
