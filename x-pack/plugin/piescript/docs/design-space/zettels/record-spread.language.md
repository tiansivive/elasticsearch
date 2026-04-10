---
tags: [language, syntax, row-types, open, feature, needs-design, later]
refs:
  - thread:language-expressiveness
---
# Record Spread

`...` operator for injecting all fields of a record into another record expression. Extends
the existing record update syntax (`{ base | field = val }`) with multi-record merging.

**Proposed syntax:**
- `{ base | ...extra, field = val }` — start with base, spread extra's fields, override field
- `{ | ...foo, ...bar }` — merge multiple records (right-biased, consistent with `&` type operator)

The `|` separator already exists in record update syntax (BAR token). `...` is the spread
operator — distinct from `|` which means "and the rest" (structural separation in patterns
and updates). They compose: `|` separates the base from modifications, `...` expands a record
into field entries.

At the type level, `{ | ...foo, ...bar }` would have type `Record (typeof(foo) & typeof(bar))`,
reusing the existing `&` row merge operator in `force`. Right-biased overlap follows
[[row-merge-semantics.types]].

At the value level, the evaluator merges `Map<String, Value>` entries — same as `CoreUpdate`
but with all fields from the spread record instead of explicit field assignments.

**Depends on**: [[row-polymorphism.types]], [[update-sugar.language]]
**Enables**: (none directly)
**Connections**:
- extends: [[update-sugar.language]] — adds spread to the existing `{ base | ... }` syntax
- uses: [[row-merge-semantics.types]] — value-level merge follows the same right-biased semantics as type-level `&`
- contrasts-with: pattern `|` tail binding — `...` injects fields into an expression, `|` binds the remaining fields in a pattern
