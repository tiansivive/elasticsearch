---
tags: [distributed, coordination, data, orchestration, implemented, concept, motivation]
refs:
  - adr:D-042
  - code:EvalCoordination.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Data Locality

Compute where data lives. Ship closures to data nodes rather than pulling data to coordinator.

- The send + scan pattern: coordinator discovers [[topology.infrastructure]], ships closure to target node, closure opens local shard, reads data, sends results back via [[channels.infrastructure]]
- Core value proposition of piescript's distributed model via [[code-mobility.coordination]]

**Depends on**: [[explicit-distribution.language]], [[code-mobility.coordination]], [[topology.infrastructure]]
**Enables**: (none directly)
**Connections**:
- contrasts-with: [[spark.comparable]] — same principle as Spark's "ship compute to data"; piescript makes it explicit (user names nodes), Spark makes it implicit (optimizer decides)
- uses: [[send.coordination]] — the send+scan pattern uses send to deliver closures to data nodes
- uses: [[shard-read.data]] — closure reads local shard data after arriving at the data node
- specializes: [[map-reduce.distributed]] — MapReduce is a specific instance of the compute-to-data pattern
