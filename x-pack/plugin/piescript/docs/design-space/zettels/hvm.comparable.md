---
tags: [comparable, computation, compilation, graph-rewriting]
refs:
  - resource:https://github.com/HigherOrderCO/HVM
---
# HVM (Higher-order Virtual Machine)

Higher-order Virtual Machine -- the only real implementation of interaction nets for a functional language (by Victor Taelin/HigherOrderCO). Targets Rust/GPU/multicore for massive parallelism via optimal reduction (Lamping's algorithm). Demonstrates: beta-reduction-optimal evaluation, automatic parallelism from purity, lazy cloning. NOT a compilation target for piescript (wrong platform -- Rust/GPU, not JVM). Relevant as proof of concept and source of implementation ideas.

Resources: HVM GitHub repository, Taelin's writeups on optimal reduction.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- implements: [[interaction-nets.computation]] -- the only real implementation
- contrasts-with: [[bytecode-compilation.performance]] -- graph reduction vs stack machine; fundamentally different
- validates: [[purity.language]] -- demonstrates that purity enables automatic parallelism
- informs: [[painless-push-down.performance]] -- could piescript improve on Painless for vectorized ops via parallel evaluation?
