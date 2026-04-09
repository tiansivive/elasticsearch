---
tags: [infrastructure, implemented, distributed, orchestration, documentation]
refs:
  - adr:D-044
  - adr:D-048
  - code:EvalTopology.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Topology

Cluster topology as typed piescript values:
- `Cluster.topology` returns local node + all nodes
- `Index.routing`/`shards`/`nodes` return shard placement
- Reads `ClusterState` -> `RoutingTable` -> `ShardRouting` -> `DiscoveryNode`
- Node records include [[inbox.infrastructure]] field for cross-node dispatch

**Depends on**: [[es-plugin.infrastructure]]
**Enables**: [[code-mobility.coordination]], [[inbox.infrastructure]]
**Connections**:
- part-of: [[block-b.roadmap]]
- constrains: [[topology-wildcards.infrastructure]] — only STARTED shards, exact index name only (no wildcards)
- refines: D-048 split topology into cluster vs index routing
- enables: [[data-locality.distributed]] — topology reveals shard placement, enabling the compute-to-data pattern
- uses: [[inbox.infrastructure]] — node records include `inbox` field for cross-node dispatch
