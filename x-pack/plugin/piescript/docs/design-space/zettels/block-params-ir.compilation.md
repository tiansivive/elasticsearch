---
tags: [compilation, ir, concept, lowering]
refs: []
---
# Block-Params IR

A lower intermediate representation where basic blocks take parameters (like SSA phi nodes but explicit). Enables efficient compilation of continuations as jumps rather than closure calls. Used by MLIR, Cranelift, WebAssembly's proposed funclets. Would sit between piescript's Core IR and JVM bytecode/Painless output.

Block parameters make the data flow between basic blocks explicit: instead of phi nodes (SSA) or implicit registers, each block declares what values it receives. A jump to a block passes arguments directly. This representation is close to both CPS (each block is a continuation with parameters) and to JVM bytecode (basic blocks with local variable slots), making it an ideal compilation target for continuation-based control flow.

**Depends on**: (none)
**Enables**: [[state-machine-loop.compilation]]
**Connections**:
- part-of: [[delimited-continuations.hub]]
- enables: [[state-machine-loop.compilation]] -- block params make state transitions efficient
- extends: [[core-ir.language]] -- lower IR sits below Core IR in the compilation pipeline
- uses: [[lowering-pass.performance]] -- Core IR lowers to block-params IR
- informs: [[bytecode-compilation.performance]] -- block-params IR is close to JVM bytecode's basic block structure
