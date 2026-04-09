---
tags: [comparable, distributed, prior-art, reference]
refs:
  - doc:references.md
---
# Distributed Data Systems

External systems that combine query languages with distributed execution, each representing a different point in the design space piescript navigates:

- [[dryadlinq.comparable]]: LINQ over distributed DAGs with compile-time optimization
- [[webdamlog.coordination]]: location-aware Datalog with first-class data placement
- [[spark.comparable]]: lazy RDD/DataFrame transformations with catalyst optimizer
- [[flink.comparable]]: stateful stream processing with exactly-once semantics

Together these bracket the space from batch to streaming, declarative to imperative, location-transparent to location-explicit.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- subsumes: [[dryadlinq.comparable]], [[webdamlog.coordination]], [[spark.comparable]], [[flink.comparable]]
