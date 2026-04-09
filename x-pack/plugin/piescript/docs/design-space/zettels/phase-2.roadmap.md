---
tags: [roadmap, data, types, implemented]
refs:
  - plan:phase2_implementation
  - plan:phase2_row_types
---
# Phase 2 — Index Resolution + Concrete-Row Constraints

Programs typechecked against real ES index mappings. Eager evaluation, field-caps resolution, ESQL value converters, and a prelude.

**Depends on**: [[phase-1.roadmap]]
**Enables**: [[block-a.roadmap]]
**Connections**:
- part-of: [[roadmap-hub.roadmap]]
- part-of: [[mvp.roadmap]]
- subsumes: [[field-caps-resolution.data]]
- subsumes: [[concrete-row-constraints.types]]
- subsumes: [[esql-value-converter.esql]]
- subsumes: [[prelude.language]]
- subsumes: [[eager-materialization.data]]
