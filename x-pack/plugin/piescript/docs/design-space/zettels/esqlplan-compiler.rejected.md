---
tags: [esql, compilation, rejected, nbe, concept, superseded]
refs:
  - plan:block_f_linq_query_e7171607
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# EsqlPlan/EsqlCompiler Design (Rejected)

A detailed design existed for ESQL compilation via a sealed `EsqlPlan` interface and a separate `EsqlCompiler` pass that would walk the Core IR to produce query plans. `EsqlPlan` variants would mirror ESQL commands (`From`, `Where`, `Eval`, `Stats`, etc.), and the compiler would pattern-match on Core IR nodes to build the plan tree, then serialize the plan to an ESQL string.

Replaced entirely by NbE Symbol-based compilation during Block F. The NbE approach is simpler (no separate compiler pass -- the evaluator IS the compiler), more extensible (new combinators just need Symbol-aware primop cases), and more compositional (closures partially evaluated with symbolic rows naturally produce ESQL fragments). The EsqlPlan design would have duplicated the evaluator's traversal logic in a parallel compilation pass.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- rejected-in-favor-of: [[nbe-compilation.esql]] -- NbE Symbol-based compilation replaced this entirely
- part-of: [[esql-compilation.esql]] -- this was an earlier design for ESQL compilation
- informs: [[symbol-partial-evaluation.esql]] -- the NbE approach that replaced this uses Symbol as the compilation carrier
- contrasts-with: [[nbe-dual-pattern.types]] -- EsqlPlan was a traditional compiler; NbE uses evaluate-then-readback
- related: [[t-linq.esql]] -- both designs draw from T-LINQ; NbE is a more faithful realization of the quotation/normalization idea
- related: [[logical-plan-compilation.esql]] -- future LogicalPlan compilation may revisit some EsqlPlan ideas at a lower level
