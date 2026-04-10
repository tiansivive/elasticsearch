---
tags: [types, primitives, open, task, question, needs-design, next]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:data-completeness
---
# DateTime

`DateTime` handling currently unsupported. ESQL datetime fields map to `Unsupported` type.

- Need: `DateTime` type constructor, epoch millis representation, formatting/parsing builtins, timezone handling, temporal arithmetic
- ESQL's `DATE_FORMAT`, `DATE_PARSE`, `DATE_TRUNC` etc.
- Critical gap for real-world use — most ES indices have timestamp fields

**Depends on**: [[unified-double.types]]
**Enables**: (none directly)
**Connections**:
- related: [[keyword-string.types]] — another primitive type requiring boundary conversion from ESQL
- related: [[unified-double.types]] — part of the primitive type cluster alongside Double/Keyword/Boolean
- blocks: [[scheduled-execution.lifecycle]] — scheduled execution needs temporal arithmetic for cron/interval semantics
- constrains: [[esql-value-converter.esql]] — DateTime requires a new conversion path in the value converter
