---
tags: [performance, data-processing, streaming, theoretical, someday]
refs:
  - vision:speculative
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:ownership-resources
---
# Incremental Computation

Incremental aggregation without full recomputation.
- Instead of recomputing aggregations from scratch on each update, maintain intermediate state and incrementally incorporate new data
- This requires Monoid-like algebraic properties (associativity, identity) to merge partial results
- Needs [[persistent-resources.infrastructure]] to store intermediate aggregations

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- related: [[persistent-resources.infrastructure]] — needs persistent state
- related: [[scheduled-execution.lifecycle]] — scheduled incremental updates
- related: [[esql-aggregates.esql]] — aggregations are the primary target for incremental computation
