---
tags: [es-internals, compute-engine, columnar, infrastructure, documentation, implemented]
refs:
  - adr:D-054
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/operator/Driver.java
  - code:x-pack/plugin/esql/compute/src/main/java/org/elasticsearch/compute/operator/Operator.java
---
# Compute Driver and Operator Chain

A **`Driver`** runs **single-threadedly** on a linear chain of **`Operator`** instances, passing **`Page`**s from one operator to the next and owning operator lifecycle (see the `Driver` class Javadoc in the repository path listed in `refs`). The chain typically starts with a **source** operator (produces pages) and ends with a **sink** operator (consumes pages). This is how ESQL’s compiled plans execute in the compute engine.

Piescript does **not** instantiate **`Driver`** in the current [[shard-stream.data]] path (it builds **`Page`s** with block builders only). This zettel documents the engine for ESQL literacy and navigation of **`org.elasticsearch.compute`**.

**Depends on**: [[compute-engine.es]], [[compute-data-page-block.es]]

**Enables**: (none directly in piescript)

**Connections**:
- part-of: [[compute-engine.es]]
- informs: [[evaluator.language]] — contrast: ESQL pipeline driver vs piescript tree evaluator
- related: [[esql-compilation.esql]] — plans lower to operators consumed by drivers
