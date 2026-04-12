---
tags: [language, types, codata, streaming, open, question]
refs:
  - plan:scripting_language_design_9286506e
  - adr:D-043
---
# Stream a Datatype

The original scripting language design (§6.1) defined `Stream a` as abstract codata — an
opaque type with functional eliminators (`map`, `filter`, `fold`, `take`, `zip`), not
constructors. Users never construct streams directly (except via `query`). Pull-based, with
page-level compute operators hidden under the abstract type.

D-043 renamed `Stream` to `List` because the implementation was eagerly materialized
(`ListVal(List<Value>)`), not lazy. The name "Stream" was **reserved for future
lazy/Exchange-backed streaming** — but no detailed design followed.

## The reconciliation problem

The original `Stream a` and the current Exchange streaming (D-054) are fundamentally
different abstractions:

| | Original `Stream a` (§6.1) | Current Exchange (D-054) |
|--|---------------------------|--------------------------|
| Abstraction | Value you compute with | Infrastructure you orchestrate |
| Level | High (declarative combinators) | Low (explicit plumbing) |
| Model | Pull-based codata (observe head/tail) | Callback-based (Exchange.poll invokes per page) |
| Types | Piescript `Value` | Columnar `Page` (Block/Block[]) |
| User sees | `stream \|> filter f \|> map g` | `Exchange.open`, `sink`, `connect`, `addPage`, `poll` |

**Open questions:**
- Can `Stream a` be implemented on top of Exchange? The materialization boundary
  (`Page` → `Value`) sits between them. Push-vs-pull inversion needed.
- Should it? Piescript's philosophy is explicit distribution ([[explicit-distribution.language]]).
  An abstract `Stream a` that hides Exchange orchestration may conflict with that.
- Is `Stream a` Exchange-backed, local-only (like Kotlin's `Sequence`), or a typeclass
  abstraction over multiple backends?
- How does `Stream a` fit in the [[data-access-hierarchy.data|data access hierarchy]]?
  Is it a layer above Exchange, or a separate concept entirely?
- The original §6.1 assumed `Stream a` was the ONLY way to access query results. Today
  piescript has multiple paths: `query expr;` (ESQL), `Shard.*` (physical), Exchange
  (streaming). `Stream a` may be one layer, not THE abstraction.

## Possible designs

1. **Abstract codata over Exchange** — `Stream a` is sugar; combinators compile to Exchange
   setup + Page-level operations. High abstraction, but hides the explicit orchestration that
   piescript values.
2. **Local lazy sequences** — `Stream a` is a local-only lazy sequence (like Java `Stream`,
   Kotlin `Sequence`). No Exchange involvement. Useful for lazy transformations on data
   already in hand. Simple, but doesn't solve the distributed streaming problem.
3. **Typeclass abstraction** — `Streamable f` with instances for Exchange-backed, local,
   and channel-backed streams. Combines with [[query-typeclass.data]] for unified query surface.
   Most general, but heaviest — needs [[typeclasses.types]].
4. **Don't reify** — keep Exchange explicit, keep `List` eager, don't add a `Stream` type.
   The gap is filled by library functions over Exchange that feel stream-like without
   introducing a new type.

**Depends on**: [[codata.types]]
**Enables**: (none directly — design unsettled)
**Connections**:
- reserved-by: [[list-type.language]] — D-043 renamed Stream→List, reserving "Stream" for this
- originates-from: plan:scripting_language_design_9286506e §6.1 "Streams as Abstract Codata"
- tension-with: [[exchange-streaming.infrastructure]] — Exchange is explicit plumbing; Stream a is abstract. Reconciliation unclear.
- tension-with: [[explicit-distribution.language]] — abstract Stream may conflict with piescript's explicit-control philosophy
- uses: [[codata.types]] — the original design explicitly describes Stream as codata (observation-based)
- uses: [[materialization-boundary.data]] — Stream→Value conversion IS materialization; Page sits between
- informs: [[lazy-stream.types]] — lazy-stream is the type-system side of this question
- informs: [[query-typeclass.data]] — Stream could be a Query instance, or Query could subsume Stream
- informs: [[data-access-hierarchy.data]] — where does Stream sit in the ESQL/ShardPlan/List/Exchange hierarchy?
- contrasts-with: [[list-type.language]] — List is eager/finite (data); Stream is lazy/potentially-infinite (codata)
- contrasts-with: [[multi-value-channels.coordination]] — a channel producing values over time is also "streaming" but via coordination, not data access
