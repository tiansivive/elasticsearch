---
tags: [principle, meta, workflow, concept]
refs:
  - doc:architecture.md
  - doc:roadmap.md
---
# Vertical Slice Testing

Every phase of piescript's development produces a runnable artifact exercisable via the REST
endpoint (`_piescript/eval`). No phase is "infrastructure only" -- each delivers user-visible
behavior that can be tested end-to-end.

**Methodology:**
- `PiescriptIT` integration tests exercise the full stack: parse -> elaborate -> evaluate -> REST response
- `debug/test-dev.sh`, `debug/test-eval.sh`, `debug/test-multinode.sh` provide manual smoke tests
- Each block (A through H) in the roadmap has acceptance criteria defined as REST-exercisable scenarios
- The dev endpoint (`_piescript/eval`) returns JSON, making test assertions straightforward

**Why this matters:**
- Prevents "it'll work when we wire it up" phases that accumulate hidden integration risk
- Forces the evaluator, type system, and infrastructure to stay in sync at every step
- Makes incremental delivery real: each phase is a working system, not a partially-built one
- Catches design flaws early by exercising the full pipeline, not just unit-testing components

**Depends on**: [[incremental-delivery.principle]], [[dev-endpoint.tooling]]
**Enables**: (none directly)
**Connections**:
- part-of: [[design-principles.hub]]
- part-of: [[implementation-plan-workflow.meta]]
- uses: [[incremental-delivery.principle]] -- vertical slices are the mechanism for incremental delivery
- uses: [[dev-endpoint.tooling]] -- REST endpoint is the exercisable surface for each slice
- uses: [[eval-endpoint.infrastructure]] -- the eval endpoint is the concrete test target
- informs: [[roadmap-hub.roadmap]] -- each roadmap block is a vertical slice
- validates: [[es-native.principle]] -- running inside ES means every slice runs in an actual ES cluster
- related: [[core-printer.tooling]] -- CorePrinter aids debugging during vertical slice development
