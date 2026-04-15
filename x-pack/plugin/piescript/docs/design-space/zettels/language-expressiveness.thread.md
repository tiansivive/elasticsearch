---
tags: [thread, roadmap, language]
refs: []
---
# Language Expressiveness

From recursion and string concat to typeclasses, comprehensions, and a module
system. This thread covers the features that make piescript expressive enough to
write real programs — control flow, data manipulation operators, abstraction
mechanisms, and reusable definitions.

## Sequence

1. **Pattern matching** [[pattern-matching.hub]] — ready
   Match expressions (`match x | pat -> body`), `if/then/else` as sugar.
   Phase 1 (basic patterns) complete. ADT constructor patterns deferred.
   Unblocks recursion. See hub for sub-zettels.
   _Shared with: error-handling_

2. **Recursion** [[recursion.hub]] — ready
   Implicit recursion and fused `loop`/`repeat` are implemented.
   Enables pagination loops, iterative algorithms.

3. **String/list concat** [[string-concat.language]] — ready
   `<>` for Keyword concat, `++` for List concat. Future typeclass candidates.

4. **ADTs** [[adts.types]] — needs-design
   Sum + product types. Declaration syntax, constructor naming.
   Complements pattern matching (constructor patterns) but neither blocks the other.
   _Shared with: error-handling_

5. **GroupBy combinator** [[groupby.language]] — needs-design
   `groupBy` for partitioning and aggregation over lists.

6. **Traverse combinator** [[traverse.language]] — needs-design
   `List.traverse` for effectful mapping (semantic `map` over `Channel`).

7. **Comprehension syntax** [[comprehension-syntax.language]] — needs-design
   Sugar over ESQL/Query combinators (`from r in idx where ... select ...`).

8. **Push-down compilation** [[push-down-compilation.performance]] — needs-design
   Typeclass-driven compilation of piescript into Lucene/ESQL.
   Depends on: [[typeclasses.types]]

9. **Query typeclass** [[query-typeclass.data]] — needs-design
   `Query f` unifying ESQL, ShardPlan, List instances.
   Depends on: [[typeclasses.types]], [[comprehension-syntax.language]]

10. **Data access architecture** [[data-access-architecture.roadmap]] — exploration
    Full `Query a` typeclass vision with LuceneM escape hatch.

11. **Typeclasses** [[typeclasses.types]] — needs-design
    Ad-hoc polymorphism. Dictionary passing vs monomorphization, coherence.
    _Shared with: type-foundations_

12. **Module system** [[module-system.tooling]] — exploration
    Stored programs, imports, versioning.

**Depends on**: (none — root thread)
**Enables**: (none directly)
**Connections**:
- includes: [[recursion.hub]]
- includes: [[string-concat.language]]
- includes: [[adts.types]]
- includes: [[pattern-matching.hub]]
- includes: [[groupby.language]]
- includes: [[traverse.language]]
- includes: [[comprehension-syntax.language]]
- includes: [[push-down-compilation.performance]]
- includes: [[query-typeclass.data]]
- includes: [[data-access-architecture.roadmap]]
- includes: [[typeclasses.types]]
- includes: [[module-system.tooling]]
- includes: [[recursive-types.types]]
- related: [[error-handling.thread]] — shares ADTs, pattern matching
- related: [[type-foundations.thread]] — shares typeclasses
