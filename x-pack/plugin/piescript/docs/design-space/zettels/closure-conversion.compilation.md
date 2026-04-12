---
tags: [compilation, technique, concept, lowering]
refs:
  - doc:references.md
---
# Closure Conversion

Compiler pass that eliminates free variables from closures by converting them to explicit environment parameters or flat records. Prerequisite for most compilation targets (bytecode, Painless, interaction nets). Piescript closures already carry `Value[] env` -- closure conversion would make this explicit in a lower IR. Also needed for code mobility (traveling closures need explicit captured environments for serialization).

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- prerequisite-for: [[bytecode-compilation.performance]] -- bytecode needs closure-free code
- prerequisite-for: [[interaction-nets.computation]] -- nets need explicit port wiring, no free variables
- prerequisite-for: [[painless-push-down.performance]] -- Painless has no closures; must convert
- uses: [[code-mobility.coordination]] -- traveling closures = serialized closure-converted code
- uses: [[closure-val.language]] -- current runtime closures carry `Value[] env`; closure conversion formalizes this
- extends: [[lowering-pass.performance]] -- closure conversion is a lowering pass
