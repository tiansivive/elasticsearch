---
tags: [search, data, language, exploration, someday, fixpoint]
refs:
  - doc:references.md
---
# Datalog Fixpoint

Flix-style first-class Datalog fixpoint as a language expression. Compute-until-no-new-facts semantics (monotone set growth). Enables graph pattern computation natively in ES: transitive closure, reachability, connected components -- today requires extraction to Spark/Neo4j. Semi-naive evaluation avoids redundant recomputation.

**Depends on**: [[logic-unification.search]], [[logic-variables.search]]
**Enables**: (none directly)
**Connections**:
- part-of: [[logic-programming.hub]]
- uses: [[calm-theorem.types]] -- Datalog computes exactly the monotone/coordination-free fragment
- complements: [[join-calculus.coordination]] -- Datalog handles what doesn't need coordination; JC handles what does
- extends: [[stratified-negation.search]] -- safe negation extends Datalog beyond pure monotone
- inspired-by: Flix (first-class fixpoint in ML-family language)
- enables: graph search in ES (transitive closure, reachability -- the exact problem ES users extract to Spark for)
- tension-with: [[distributed-continuations.obstacle]] -- distributed fixpoint intermediate state spans nodes
- informs: [[termination-analysis.theory]] -- monotone Datalog guaranteed to terminate
