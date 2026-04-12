---
tags: [theoretical, concept, graph-rewriting]
refs:
  - doc:references.md
---
# Term Rewriting

Pattern matching, evaluation, type-level force, Datalog fixpoint -- all are term rewriting. A term rewriting system (TRS) defines rules that transform terms matching a pattern into a replacement. Piescript's evaluator IS a TRS: each CoreExpr case is a rewrite rule. Useful as a consistency lens -- "is this design consistent with standard TRS properties?" -- but not directly actionable as an implementation target.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- informs: [[pattern-matching.hub]] -- pattern matching IS term rewriting
- informs: [[nbe-dual-pattern.types]] -- NbE = evaluate terms to normal form, which is TRS normalization
- informs: [[datalog-fixpoint.search]] -- Datalog fixpoint = monotone TRS reaching normal form
- informs: [[evaluator.language]] -- the evaluator IS a term rewriting system
- subsumes: [[knuth-bendix.theory]], [[church-rosser.theory]], [[termination-analysis.theory]]
