---
tags: [data, types, implemented, documentation]
refs:
  - adr:D-050
  - code:Prelude.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Index Type

`Index r` carries index name, UUID, and field metadata. UUID resolved at evaluation time from `ClusterService` (not in [[core-ir.language]] -- keeps it cluster-state-free).
- `IndexVal` is serializable and travels in [[closure-val.language]]
- `Index.routing`, `Index.shards`, `Index.nodes` accept `Index r`

**Depends on**: [[use-declarations.data]]
**Enables**: [[shard-read.data]], [[shard-write.data]], [[topology.infrastructure]]
**Connections**:
- part-of: [[block-d.roadmap]]
- uses: [[field-caps-resolution.data]] — row type r derived from field capabilities
- uses: [[nested-record-types.data]] — row type r includes nested record types from OBJECT fields
