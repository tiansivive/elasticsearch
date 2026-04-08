---
tags: [theoretical, external, comparable]
refs:
  - doc:data-access.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: Spark

Apache Spark: ship compute to data (RDD/DataFrame). Same "bring compute to data" pattern as piescript's send + scan. Piescript makes a class of Spark workloads ES-native. Spark retains advantages for ML libraries, GPU, multi-system federation.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: FlumeJava, DryadLINQ are the academic predecessors
- related: [[data-locality.distributed]] — same "ship compute to data" principle; piescript makes it explicit, Spark makes it implicit
- related: [[map-reduce.distributed]] — Spark is MapReduce's practical successor
