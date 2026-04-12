---
tags: [theoretical, concept, fixpoint]
refs:
  - doc:references.md
---
# Termination Analysis

Does a rewriting system terminate (reach a normal form)? Techniques: structural recursion (subterm ordering), polynomial interpretation, dependency pairs. Undecidable in general but useful heuristics and decidable subsets exist. Datalog fixpoint over monotone rules: guaranteed to terminate (finite domain, monotone growth). General recursion: undecidable. Fused loop-match with repeat: termination depends on the step function (not statically guaranteed without refinement types or sized types).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[term-rewriting.theory]]
- informs: [[datalog-fixpoint.search]] -- monotone Datalog terminates
- informs: [[recursion.hub]] -- general recursion doesn't guarantee termination
- informs: [[fused-loop-match.language]] -- loop-match termination depends on step function
- informs: [[recursive-types.types]] -- iso-recursive fold/unfold and termination
- uses: [[stratified-negation.search]] -- stratification is a termination guarantee for non-monotone Datalog
