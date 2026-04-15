---
tags: [principle, testing, distributed, workflow, implementation]
refs:
  - session:recursion-closeout
---
# Cross-Node Testing Layers

Distributed features should be validated in layered scopes, not a single test tier.

For cross-node semantics (closure shipping, remote inbox execution, exchange transport, and similar behavior), use all three layers:

1. Serialization/unit tests for wire-level invariants and value round-trips.
2. javaRest integration tests for end-to-end behavior in real cluster topology.
3. Debug scripts for operational smoke coverage and rapid regression checks during implementation.

No single layer substitutes for the others: unit tests miss orchestration, integration tests can miss quick manual scenarios, and debug scripts are not exhaustive.

**Depends on**: [[vertical-slice-testing.principle]], [[implementation-plan-workflow.meta]]
**Enables**: [[exchange-remote-testing.infrastructure]], [[recursive-closure-shipping.coordination]]
**Connections**:
- refines: [[vertical-slice-testing.principle]] -- adds a distributed layered-coverage rule
- constrains: [[implementation-plan-workflow.meta]] -- cross-node changes should update automated tests and debug scripts
- validates: [[code-mobility.coordination]] -- mobility guarantees need layered cross-node coverage