---
tags: [esql, implemented, tech-debt]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Dotted Field Paths

NbE fix: `ESQL.keep`/`ESQL.drop` now use dotted field paths from `Symbol` values (e.g., `host.name`) instead of record keys. When closures project nested fields, the `Symbol` carries the full dotted path. Without this fix, `ESQL.keep (fn r -> { name: r.host.name })` would emit `KEEP name` instead of `KEEP host.name`. Reveals a design tension: flat field names in records vs dotted paths in ESQL.

**Depends on**: [[nbe-compilation.esql]], [[nested-record-types.data]]
**Enables**: (none directly)
**Connections**:
- related: flat-vs-dotted tension recurs wherever piescript records meet ESQL column references — future: structured field paths as first-class values
- related: [[field-caps-resolution.data]] — field caps resolution produces the nested structures that generate dotted paths
