---
tags: [ml, meta, decision, concept]
refs:
  - doc:vision.md
---
# ML Non-Goals

What piescript explicitly does NOT attempt:

- **Model training** — gradient descent, backpropagation, matrix multiplication.
  Requires GPUs, CUDA, automatic differentiation. ES's JVM is fundamentally
  wrong for this.
- **Heavy inference** — running large transformer models, billion-parameter
  embeddings. ES's native inference API and external providers handle this.
- **Tensor operations** — piescript's type system is records and lists, not
  multi-dimensional arrays with broadcasting semantics.

Piescript is not "ML in ES." It is a general-purpose distributed computation
language that happens to make ML-adjacent workflows ES-native. The advantage
comes from the data access architecture, not ML-specific primitives.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- part-of: [[ml-workflow-integration.ml]]
- constrains: [[ml-workflow-integration.ml]] — defines the boundary of what piescript covers
- related: [[extraction-cliff.external]] — piescript dissolves the extraction cliff for data-heavy ML workflows, not compute-heavy ones
