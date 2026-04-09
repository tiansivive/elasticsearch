---
tags: [performance, compilation, open, concept]
refs:
  - roadmap:post-mvp
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Bytecode Compilation

Compile piescript's pure fragment to JVM bytecode.

- Would eliminate interpreter overhead for tight loops
- FFI calls become direct Java method invocations
- Requires closure conversion, lambda lifting, register allocation
- Highly speculative — the [[evaluator.language]] is sufficient for coordination-heavy workloads; worth considering for compute-heavy inner loops

**Depends on**: [[evaluator.language]]
**Enables**: [[ffi-painless.external]]
**Connections**:
- extends: [[lowering-pass.performance]] — bytecode is a possible backend target after the lowering pass
- alternative-to: [[evaluator.language]] — replaces tree-walking interpretation with compiled code for the pure fragment
