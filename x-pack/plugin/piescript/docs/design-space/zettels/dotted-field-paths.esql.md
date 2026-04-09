---
tags: [esql, implemented, tech-debt, task]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Dotted Field Paths

[[nbe-compilation.esql]] fix: `ESQL.keep`/`ESQL.drop` now use dotted field paths from `Symbol` values (e.g., `host.name`) instead of record keys.

- When closures project nested fields, the `Symbol` carries the full dotted path
- Without this fix, `ESQL.keep (fn r -> { name: r.host.name })` would emit `KEEP name` instead of `KEEP host.name`
- Reveals a design tension: flat field names in records vs dotted paths in ESQL

**Depends on**: [[nbe-compilation.esql]], [[nested-record-types.data]]
**Enables**: (none directly)
**Connections**:
- tension-with: [[nested-record-types.data]] — flat-vs-dotted tension recurs wherever piescript records meet ESQL column references
- uses: [[field-caps-resolution.data]] — field caps resolution produces the nested structures that generate dotted paths
- uses: [[symbol-partial-evaluation.esql]] — Symbol values carry the dotted path information
