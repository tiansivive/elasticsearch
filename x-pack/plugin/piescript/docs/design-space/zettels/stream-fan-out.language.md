---
tags: [language, concept, decision, implemented]
refs:
  - adr:D-017
---
# Stream Fan-Out

Streams (now Lists) allow multi-use without linearity. `ListVal` wraps immutable `List<Value>` -- sharing is trivially safe. Under the original [[plan-graph.language|plan-graph model]], fan-out was a DAG property handled by exchange operators. Key insight: "StreamVal is data, not a resource" -- no linearity tax needed for data descriptions. Linearity reserved for [[channels.infrastructure|channel]] endpoints (Phase 6 [[qtt-linearity.types|QTT]]).

**Depends on**: [[list-type.language]]
**Enables**: (none directly)
**Connections**:
- contrasts-with: [[qtt-linearity.types]] — linearity for channels not streams
- evolved-into: [[eager-materialization.data]] — currently everything is eagerly materialized so fan-out is trivial
