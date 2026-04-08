---
tags: [distributed, coordination, data, orchestration, implemented]
refs:
  - adr:D-042
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Data Locality

Compute where data lives. Ship closures to data nodes rather than pulling data to coordinator. The send + scan pattern: coordinator discovers topology, ships closure to target node, closure opens local shard, reads data, sends results back via channel. Core value proposition of piescript's distributed model.

**Depends on**: [[explicit-distribution.language]], [[code-mobility.coordination]], [[topology.infrastructure]]
**Enables**: (none directly)
**Connections**:
- contrasts-with: [[spark.comparable]] — same principle as Spark's "ship compute to data"; piescript makes it explicit (user names nodes), Spark makes it implicit (optimizer decides)
- related: [[send.coordination]] — the send+scan pattern uses send to deliver closures to data nodes
- related: [[shard-read.data]] — closure reads local shard data after arriving at the data node
- related: [[map-reduce.distributed]] — MapReduce is a specific instance of the compute-to-data pattern
