---
tags: [language, syntax, types, feature, implemented]
refs:
  - thread:language-expressiveness
---
# Pattern Types

The `Pattern` sealed hierarchy for match expressions. Phase 1 covers patterns that don't
need ADTs.

**Phase 1 patterns:**

| Pattern | Example | Binds | Type produced |
|---------|---------|-------|---------------|
| Literal | `42`, `"hello"`, `true` | nothing | concrete type of the literal |
| Variable | `x` | `x` to the matched value | fresh meta `?a` |
| Wildcard | `_` | nothing | fresh meta `?a` |
| Record | `{ name: n, age: 18 }` | `n` to field value | open row `{ name: ?a, age: Double \| ?r }` |
| Record tail | `{ name: n \| rest }` | `n` + `rest` as **record** | `rest` has type `Record ?r` (row tail) |
| List (exact) | `[]`, `[x]`, `[x, y]` | element variables | `List ?a`, length-checked at runtime |
| List tail | `[h \| t]` | `h` + `t` as `List` | `h : ?a`, `t : List ?a` |

**Key design points:**

- `|` for tails in both records and lists — consistent with row type syntax
  `{ field: Type | r }` and Erlang/Prolog list cons. See [[record-spread.language]] for the
  distinction: `|` is structural separation (patterns), `...` is value injection (expressions).
- Record patterns are open-row by default — `{ name: n }` matches any record with a `name`
  field. Falls out of [[row-polymorphism.types]] unification.
- Record tail `| rest`: at the type level, `rest` is `Record ?r` (row tail). At the value
  level, `rest` is a `RecordVal` constructed by removing matched fields from the original
  record. Rows don't exist at the value level.
- Nested patterns are not a special case — the `Pattern` hierarchy is recursive (`RecordPat`
  contains `Pattern` values, `ConsListPat` contains `Pattern` head/tail). Type inference
  walks patterns recursively; nesting falls out naturally.

**Deferred:**
- Constructor patterns (`Some x -> ...`) — needs [[adts.types]]
- Or-patterns, guards

**Depends on**: [[row-polymorphism.types]], [[list-type.language]], [[record-type.language]]
**Enables**: (none directly)
**Connections**:
- part-of: [[pattern-matching.hub]]
- uses: [[row-polymorphism.types]] — record patterns produce open-row types for unification
- contrasts-with: [[record-spread.language]] — `|` binds the rest in patterns; `...` spreads fields in expressions
