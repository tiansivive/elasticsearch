---
tags: [types, primitives, open]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# DateTime

`DateTime` handling currently unsupported. ESQL datetime fields map to `Unsupported` type. Need: `DateTime` type constructor, epoch millis representation, formatting/parsing builtins, timezone handling, temporal arithmetic. ESQL's `DATE_FORMAT`, `DATE_PARSE`, `DATE_TRUNC` etc.

**Depends on**: [[unified-double.types]]
**Enables**: (none directly)
**Connections**:
- related: critical gap for real-world use — most ES indices have timestamp fields
