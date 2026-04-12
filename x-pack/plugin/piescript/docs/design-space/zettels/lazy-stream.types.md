---
tags: [types, language, codata, streaming, exploration, someday]
refs:
  - adr:D-043
---
# Lazy Stream Type

The future `Stream` type reserved by D-043 when `Stream` was renamed to `List`. D-043
explicitly states: "Stream is reserved for future lazy/Exchange-backed streaming."
No detailed design exists.

**Open design questions:**
- Coinductive ([[codata.types]]) or operational (Exchange-backed)?
- Semantics: lazy pull (consumer drives) or push (producer drives with backpressure)?
- Relationship to `List` — is `Stream` a supertype, a separate type, or a typeclass abstraction?
- Relationship to `Exchange r` ([[exchange-streaming.infrastructure]]) — is `Stream` sugar
  over Exchange, or a separate abstraction?
- Relationship to multi-value channels ([[multi-value-channels.coordination]]) — a channel
  that produces multiple values over time IS a stream.
- Terminal operations — what forces a `Stream` to materialize? Relates to the dormant
  [[terminal-operations.language]] concept.

**Potential models:**
1. **Coinductive `Stream a`** — defined by `head` and `tail` observations. Infinite.
   Needs lazy evaluation or explicit thunks.
2. **Exchange-backed `Stream r`** — `Page`s flowing via Exchange. Finite but large.
   Already partly implemented (Block G).
3. **Channel-backed `Stream a`** — multi-value channel producing values over time.
   Needs [[multi-value-channels.coordination]].
4. **Typeclass abstraction** — `Streamable f` unifying all three via [[typeclasses.types]].

**Depends on**: [[codata.types]]
**Enables**: (none directly)
**Connections**:
- reserved-by: [[list-type.language]] — D-043 renamed Stream→List, reserving "Stream" for this
- uses: [[codata.types]] — coinductive Stream is the theoretical foundation
- uses: [[exchange-streaming.infrastructure]] — Exchange-backed streaming is the operational foundation
- uses: [[multi-value-channels.coordination]] — multi-value channels are streams at the coordination level
- revives: [[terminal-operations.language]] — terminal operations determine when a Stream materializes
- informs: [[materialization-boundary.data]] — Stream→List conversion IS materialization
- contrasts-with: [[list-type.language]] — List is data (eager, finite); Stream is codata (lazy, potentially infinite)
- uses: [[anamorphisms.types]] — Stream production is an unfold
- uses: [[catamorphisms.types]] — Stream consumption is a fold
- complements: [[stream-a.language]] — lazy-stream is the type-system side; stream-a is the design/reconciliation side
