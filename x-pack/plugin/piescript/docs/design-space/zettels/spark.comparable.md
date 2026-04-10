---
tags: [theoretical, external, comparable, prior-art]
refs:
  - doc:archive/data-access.pre-threads.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: Spark

Apache Spark: ship compute to data (RDD/DataFrame). Same "bring compute to data" pattern as piescript's [[send.coordination|send]] + scan. Piescript makes a class of Spark workloads ES-native. Spark retains advantages for ML libraries, GPU, multi-system federation.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- inspired-by: [[dryadlinq.comparable]] — FlumeJava, DryadLINQ are the academic predecessors
- analogous-to: [[data-locality.distributed]] — same "ship compute to data" principle; piescript makes it explicit, Spark makes it implicit
- evolved-into: [[map-reduce.distributed]] — Spark is MapReduce's practical successor
