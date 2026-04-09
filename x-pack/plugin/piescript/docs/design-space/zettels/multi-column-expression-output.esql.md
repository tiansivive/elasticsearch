---
tags: [esql, compilation, open, question, problem]
refs: []
---
# Multi-Column Expression Output

Unsolved problem: how [[record-type.language]] construction in mobile lambdas produces multiple output columns from one ESQL plan node. When a record-returning lambda is compiled to ESQL via [[nbe-compilation.esql]], the single expression must emit N EVAL commands, one per field.

- The current [[t-linq.esql]] compilation assumes one expression produces one column
- Solving requires either a multi-EVAL expansion pass or flattening record-typed expressions into parallel column bindings during [[esql-compilation.esql]]

**Depends on**: [[record-type.language]]
**Enables**: (none directly)
**Connections**:
- blocks: [[expression-evaluator-compilation.performance]] — cannot compile record-returning expressions to ESQL evaluators until this is solved
- tension-with: [[record-type.language]] — records are first-class in the language but ESQL has no record-valued columns
- constrains: [[nbe-compilation.esql]] — NbE must handle multi-column readback for record-valued Symbols
- constrains: [[esql-compilation.esql]] — ESQL compilation must expand records into multiple EVAL commands
