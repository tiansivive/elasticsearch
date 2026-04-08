---
tags: [performance, compilation, open]
refs:
  - roadmap:post-mvp
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Bytecode Compilation

Compile piescript's pure fragment to JVM bytecode. Would eliminate interpreter overhead for tight loops. FFI calls become direct Java method invocations. Requires closure conversion, lambda lifting, register allocation.

**Depends on**: [[evaluator.language]]
**Enables**: [[ffi-painless.external]]
**Connections**:
- related: highly speculative — interpreter is sufficient for coordination-heavy workloads; worth considering for compute-heavy inner loops
- related: [[lowering-pass.performance]] — bytecode is a possible backend target after the lowering pass
