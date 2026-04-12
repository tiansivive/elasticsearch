---
tags: [types, theoretical, concept, codata, exploration]
refs:
  - plan:scripting_language_design_9286506e
---
# Codata

Coinductive types — the dual of algebraic data types ([[adts.types]]). Where ADTs are defined
by constructors (how to build a value), codata is defined by observations/destructors (how to
use a value). ADTs are finite (initial algebras); codata can be infinite (final coalgebras).

**Core idea:** An ADT value IS something (a tagged union). A codata value DOES something
(responds to observations). A `List` is ADT: `Nil | Cons(head, tail)`. A `Stream` is codata:
you can observe `head` and `tail`, but the structure is potentially infinite.

**Prior discussion in piescript:** The original scripting language design plan
(`scripting_language_design_9286506e`) explicitly discusses codata in two sections:

- **§2.10 "No User-Defined Corecursion"** — decided: no `let ones = 1 : ones` or similar
  coinductive definitions. Streams come from queries or combinators, never from user-defined
  corecursion. Rationale: KISS — avoids need for thunks, cofix, or guardedness checking.
- **§6.1 "Streams as Abstract Codata"** — `Stream a` described as an opaque type defined by
  its eliminators (observations), not its constructors. Users never construct streams directly
  (except via `query`). Underlying runtime: pages flow on demand (pull-based); **stream codata
  semantics are hidden under the abstract type**.
- **§3** — "No coinductive or inductive type definitions in the type theory."

So the original design already had the key insight: **streams ARE codata operationally**
(observation-based, pull-based) but the language **doesn't expose coinductive types to the
user**. This is the right position for now — the design space is open but not committed.

**Connection to piescript today:**
- `let x = x + 1` — invalid for data (immediate self-reference), valid for codata (defines
  an infinite stream observed lazily). The [[guarded-recursion.technique]] check must be
  relaxed for codata bindings.
- The `Stream` type reserved by D-043 ([[list-type.language]]) could be coinductive — defined
  by `head : Stream a → a` and `tail : Stream a → Stream a`, not by constructors.
- Exchange-backed streaming ([[exchange-streaming.infrastructure]]) is operationally similar
  to codata: a `Source r` produces pages on demand (observation), not all at once (construction).
- The distinction between `List` (finite, eager, ADT) and `Stream` (potentially infinite, lazy,
  codata) is exactly the data/codata duality.

**Depends on**: [[recursive-types.types]]
**Enables**: [[lazy-stream.types]]
**Connections**:
- dual-of: [[adts.types]] — ADTs are initial algebras (constructors); codata is final coalgebras (observations)
- dual-of: [[catamorphisms.types]] — catamorphisms consume ADTs (fold); [[anamorphisms.types]] produce codata (unfold)
- informs: [[lazy-stream.types]] — a future Stream type may be coinductive
- informs: [[guarded-recursion.technique]] — codata relaxes the guarded recursion check
- informs: [[exchange-streaming.infrastructure]] — Exchange Sources are operationally codata (produce on demand)
- informs: [[materialization-boundary.data]] — the data/codata boundary IS the materialization boundary
- informs: [[stream-a.language]] — the original Stream a was explicitly described as abstract codata (§6.1)
- contrasts-with: [[list-type.language]] — List is data (eager, finite); Stream is codata (lazy, potentially infinite). D-043 reserves "Stream" for this.
- constrained-by: [[no-corecursion.decision]] — user-defined corecursion explicitly rejected
