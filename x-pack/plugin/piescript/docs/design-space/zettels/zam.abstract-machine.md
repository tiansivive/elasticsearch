---
tags: [abstract-machine, compilation, theoretical, comparable]
refs:
  - resource:https://xavierleroy.org/talks/zam-kazam05.pdf
---
# ZAM (Zinc Abstract Machine)

OCaml's bytecode interpreter. Stack-based, optimized for strict curried functions. Push args on stack, jump to function body. Not a direct target for piescript but relevant as practical knowledge if piescript compiles to bytecode. Demonstrates efficient compilation of curried functions in a strict ML-family language.

Key design choices: the ZAM compiles curried multi-argument functions into a single stack frame (avoiding per-argument closure allocation), uses a "grab" instruction that checks whether enough arguments are available, and falls back to partial application closures only when necessary. This is the canonical solution to the "currying tax" in strict functional languages.

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- informs: [[bytecode-compilation.performance]] -- ZAM's design choices are relevant if piescript targets bytecode
- contrasts-with: [[cek-machine.evaluation]] -- stack machine vs environment machine
- uses: [[currying.language]] -- optimized for curried calling convention
