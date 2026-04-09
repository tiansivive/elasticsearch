---
tags: [tooling, language, implemented, documentation]
refs:
  - code:Exprs.java
  - code:Values.java
  - code:Types.java
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Builder DSL

`Exprs`, `Values`, and `Types` utility classes with static factory methods for concise Core IR / Value / Type construction: `lit(42)`, `lam("x", DOUBLE, body)`, `app(fn, arg)`, `doubleVal(n)`, `keyword(s)`, `record("k", v)`, `arrow(a, b)`. Type inference where possible (e.g., `lam` computes arrow type). Reduces test and production verbosity. Added in Block C.4. Uses `Val` suffix for primitives due to Java keyword conflicts (`intVal`, `nullVal`).

**Depends on**: [[core-ir.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[block-c.roadmap]]
- complements: [[core-printer.tooling]] — builder constructs IR, printer displays it
- uses: [[core-ir.language]] — factory methods produce `CoreExpr` nodes
