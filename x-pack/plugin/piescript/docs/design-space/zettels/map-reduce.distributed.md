---
tags: [distributed, performance, theoretical, aggregation]
refs:
  - doc:references.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# MapReduce

MapReduce as a specific instance of [[bird-meertens.types]] formalism. `map` is a list homomorphism; `reduce` with associative combiner is a fold over a commutative monoid. Lammel (2008) formalizes the connection.

- Piescript's `List.map` + `List.reduce` pattern is MapReduce
- `ESQL.statsBy` via [[esql-aggregates.esql]] is the query-engine equivalent
- The shuffle/group-by phase is a natural transformation

**Depends on**: [[bird-meertens.types]]
**Enables**: [[push-down-compilation.performance]]
**Connections**:
- inspired-by: [[dryadlinq.comparable]] — FlumeJava (Google 2010) and DryadLINQ (Microsoft 2008) are the practical systems
- related: [[push-down-compilation.performance]] — piescript's typeclass-driven push-down serves a similar optimization role but via a different mechanism
- related: [[data-locality.distributed]] — MapReduce relies on shipping compute to data; both are peer architectural concepts
- contrasts-with: [[spark.comparable]] — Spark is MapReduce's practical successor with the same compute-to-data principle
