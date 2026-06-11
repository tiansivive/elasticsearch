---
name: Block D Local Data
overview: Block D — local data access via `use`, `Shard.open`, `Shard.consume`, and `Shard.read`. Pull-based interaction with Lucene. Introduces `Index r` type with elaboration-time field caps, async shard-level search returning `Channel (Searcher r)`, and pull-based doc iteration + field reading.
todos:
  - id: d050
    content: Write D-050 decision record in decisions.md capturing all Block D design decisions (use, Index r, open/consume/read, queries as records, Searcher lifecycle, pull/push duality vision, qualified builtin names)
    status: pending
  - id: d0-namespaces
    content: "D.0: Qualified builtin names — parser support for Namespace.name, rename all Prelude/EvalBuiltins entries, update all tests and scripts"
    status: pending
  - id: d1-grammar
    content: "D.1: Grammar + type system — use production in ANTLR (desugars to CoreLet), Index r / Searcher r / DocRef type constructors, IndexVal (serializable) / SearcherVal + DocRefVal (non-serializable) Value variants, Prelude entries"
    status: pending
  - id: d2-prepass
    content: "D.2: Index resolution pre-pass — extend to handle use declarations, run field caps, resolve Index r row type"
    status: pending
  - id: d3-open
    content: "D.3: Shard.open implementation — query record → QueryBuilder conversion, async searcher acquisition + query compilation, returns Channel (Searcher r)"
    status: pending
  - id: d4-consume-read
    content: "D.4: Shard.consume + Shard.read implementation — cursor-based doc iteration via DocIdSetIterator, DocValues reading per field type, DocRefVal construction"
    status: pending
  - id: d5-deps
    content: "D.5: EvalDependencies + wiring — add IndicesService, thread pool for async open, UUID in shard records"
    status: pending
  - id: d6-routing
    content: "D.6: routing/shards/nodes update — accept Index r instead of Keyword, update Prelude type schemes"
    status: pending
  - id: d7-tests
    content: "D.7: Unit tests — parser, elaborator, evaluator, serialization, DocValues conversion, consume cursor semantics"
    status: pending
  - id: d8-integration
    content: "D.8: Integration tests — single-node and multi-node open+consume+read, fan-out, query types, error cases"
    status: pending
  - id: d-docs
    content: "D.9: Update documentation — decisions.md (D-050), current-state.md, roadmap.md with Block D progress, design decisions, known limitations, and long-term vision (three-layer architecture: LuceneM, push/pull, declarative combinators). Include reference link to design discussion."
    status: pending
isProject: false
---

# Block D — Local Data Access (`use` / `open` / `consume` / `read`)

## Decision Record: D-050

Record in [decisions.md](x-pack/plugin/piescript/docs/decisions.md) before implementation.

### 1. `use` declaration — `Index r` type

`use .logs-test as idx` introduces an `Index r` binding. The elaborator's index resolution pre-pass extracts the index name, calls field caps, and resolves `r` to a concrete row type (e.g., `{ user.name: Keyword, user.age: Double }`). At runtime, the `Index r` value carries name, UUID, and field metadata (for DocValues reading). `Index r` is the **only** way to reference an index — no raw strings for index operations.

- `use` is new ANTLR grammar syntax that **desugars to a `CoreLet`** binding an `IndexVal` literal. The pre-pass resolves field caps; the elaborator produces a `CoreLet` with the resolved type and literal value. No new Core IR node needed.
- Index name syntax TBD — needs special lexer handling for hyphens/dots in ES index names (e.g., `.logs-test`). Could use a dedicated lexer mode or quoted form.
- `routing`, `shards`, `nodes` change from `Keyword →` to `Index r →` signatures
- Only `use` constructs `Index` values (no other constructor)
- `IndexVal` **must be serializable** — it travels in closures sent to data nodes. Needs `ValueSerialization` support (write name + UUID + field metadata list). Field metadata is small (field names + ES type descriptors).

### 2. Three pull-based primitives: `open`, `consume`, `read`

The interaction with Lucene is pull-based: the user drives iteration. Three Java-backed builtins surface the minimal Lucene primitives needed.

#### 2a. `Shard.open` — async searcher acquisition + query compilation

```
Shard.open : ∀r. Index r → Shard → Query → Channel (Searcher r)
```

`Shard.open idx shard query` acquires an `Engine.Searcher`, compiles the query (`QueryBuilder` → Lucene `Query` → `Weight`), creates per-segment scorers, and returns a `Channel (Searcher r)`. The channel is completed asynchronously when the searcher and scorers are ready. The user `when`s on the channel to receive the `Searcher r`.

- `open` is a **regular builtin** — its Java implementation creates a `ChannelVal`, submits async work to a thread pool, and returns the channel immediately. No `CoreOpen` IR node needed.
- The `r` flows from `Index r` → `Channel (Searcher r)` via unification.
- Async because searcher acquisition and query compilation involve local I/O (term statistics, posting list reads from mmap'd Lucene segments). The evaluator thread should not block.
- The `Searcher r` value internally holds: `Engine.Searcher`, compiled `Weight`, per-segment `Scorer` instances, iteration cursor state. The user never sees these internals — `Searcher r` is opaque.
- Segments are **hidden** inside the Searcher for the vertical slice. Only segments with matching docs are tracked (segments where `weight.scorer(leafCtx)` returns null are skipped).
- **No size limit** for the vertical slice (document as known limitation).

#### 2b. `Shard.consume` — pull N matching doc refs

```
Shard.consume : ∀r. Int → Searcher r → List DocRef
```

`Shard.consume 1000 searcher` advances the internal cursor by up to N positions, returning a `List DocRef`. If the list has fewer than N elements, the searcher is exhausted (no more matching docs).

- The `Searcher` is **stateful** — each `consume` call advances the cursor across segments. The `DocIdSetIterator` per segment is advanced until N docs are collected or all segments are exhausted.
- `DocRef` is opaque — internally holds a segment-local doc ID and a reference to its `LeafReaderContext`. The user cannot inspect or construct `DocRef` values.
- POSIX `read()` semantics: fewer results than requested signals end. No `Maybe`/ADTs needed.
- Synchronous — `consume` advances an in-memory iterator and reads from mmap'd posting lists. Fast, local, no reason to be async.
- Multiple `consume` calls on the same searcher continue from where the last left off (cursor is internal to the `SearcherVal`).

#### 2c. `Shard.read` — read field values from a doc ref

```
Shard.read : DocRef → String → Value
```

`Shard.read ref "user.name"` reads a single field's DocValues for the referenced document. `Shard.read ref "*"` reads all mapped fields and returns a `RecordVal` (type `r` from the `Index r` / `Searcher r` lineage).

- The `DocRef` carries its segment context internally, so `read` knows which `LeafReader` to use.
- DocValues reader dispatch based on ES field type (from local `MapperService`):
  - Keyword → `SortedDocValues` / `SortedSetDocValues` → `KeywordVal`
  - Integer/Long/Double/Float/Short/Byte → `SortedNumericDocValues` → `DoubleVal`
  - Boolean → `SortedNumericDocValues` (0/1) → `BooleanVal`
  - Datetime → `SortedNumericDocValues` (epoch millis) → use existing `DataTypeMapping`
  - Others: use existing `DataTypeMapping` for type mapping; unsupported types excluded from the row type
- Synchronous — reading DocValues is a local, fast mmap operation.
- `"*"` reads all mapped fields → produces a `RecordVal` matching type `r`. The `DocRef` holds a reference to the `Searcher`, which holds the `MapperService` (via `IndexService`).
- Reading a single named field returns a scalar `Value` (the field's value).

### 3. Queries as plain records

Queries are piescript records, converted to ES `QueryBuilder` at runtime inside `open`:

```
{ match_all: true }                              → MatchAllQueryBuilder
{ term: { field: "status", value: "active" } }   → TermQueryBuilder
{ range: { field: "age", lte: 20.0 } }           → RangeQueryBuilder
{ bool: { must: [...], should: [...] } }          → BoolQueryBuilder
```

- No new `Value` variant — just `RecordVal`
- No `NamedWriteableRegistry` needed — records serialize via existing `ValueSerialization`
- No compile-time query validation — runtime conversion in `open`
- Convenience builtins (`Query.matchAll`, `Query.term`, `Query.range`, `Query.bool`) return pre-built records. Optional — user can construct records directly.
- `Query` type in the type system: for the vertical slice, an open row (any record). Future: structured query type with ADTs.

### 4. `Searcher r` — opaque, non-serializable, node-local

`Searcher r` holds the `Engine.Searcher`, compiled `Weight`, per-segment `Scorer` instances, and iteration cursor state. It is:

- **Not serializable** — runtime rejection if serialization is attempted.
- **Node-local** — only valid on the node where `open` ran.
- **Stateful** — `consume` advances the internal cursor.
- Future: `Local TYPE` kind prevents serialization at the type level (Phase 6+).

### 5. `DocRef` — opaque, non-serializable

`DocRef` holds a segment-local doc ID and a reference to its `LeafReaderContext`. It is:

- **Not serializable** — only meaningful in the context of its `Searcher`.
- **Lightweight** — just an int + a reference.
- The user cannot construct, inspect, or compare `DocRef` values. They are produced by `consume` and consumed by `read`.

### 6. Resource management (searcher release)

For the vertical slice: **figure out during implementation, add explicit `release` only if needed.**

Options under consideration (in order of preference):

1. **Ref-counted auto-release**: the `SearcherVal` tracks whether all docs have been consumed (cursor exhausted). When exhausted, the `Engine.Searcher` is released automatically. If never fully consumed → leak (documented limitation).
2. **Scope-based**: the spawned context that called `open` holds the searcher; when the spawn completes, any unreleased searchers are cleaned up.
3. **Explicit `Shard.release`**: add as a primitive only if options 1-2 prove insufficient.
4. **Future**: free monad runner or bracket pattern guarantees release.

### 7. `routing` / `shards` / `nodes` update

- Signature changes from `Keyword → ...` to `Index r → ...`
- Shard records gain `index_uuid: Keyword` field (from `ShardRouting.shardId().getIndex().getUUID()`)
- `topology "cluster"` stays as-is (cluster-level, no index)

### 8. Error handling

- **Missing shard**: `IndexService.getShard()` throws → piescript runtime error (propagated through channel)
- **Invalid query record**: conversion to `QueryBuilder` fails → runtime error
- **Index not found in routing table**: same as current `routing` behavior (throw)
- **Consume on exhausted searcher**: returns empty list (not an error)
- All errors documented. Graceful handling deferred.

### 9. Security

- Global per-request: the user who sends the piescript request must have access to all referenced indices
- Not enforced in the vertical slice — inbox handler has `internal:data/read/piescript/send` permissions, which allows local shard access
- Document as known limitation for production review

### 10. Deferred items (documented, not blocking)

- **Dynamic index names**: `use` requires a static index name (pre-pass extracts it). Future: external config parameters.
- `**Local` kind: type-level enforcement that `Searcher`/`DocRef` can't cross nodes.
- **Size limit**: no limit on search results for vertical slice.
- **Projection**: `read` with `"*"` reads all fields. Future: read only requested fields.
- **Query type safety**: ADT-based query types (requires pattern matching).
- **Schema introspection**: let users inspect `Index r` field metadata at runtime.
- **Proper module system**: Phase 7 — stored definitions, imports, versioning.
- **Rank-2 polymorphism for record fields**: would allow namespace records with polymorphic fields.
- **Maybe / ADTs**: `consume` uses "fewer than N" convention because piescript lacks sum types. When ADTs are added, `consume` could return `Maybe` or a richer result type.

---

## Long-Term Vision: Three-Layer Shard Interaction Architecture

This section documents the architectural direction discussed during Block D design. None of this is implemented in Block D — it is the evolution path. The architecture has three layers, each built on the one below. Users choose their level of control.

### Fundamental insight

Lucene provides exactly **two** modes for processing matching documents:

1. **Pull-based** (`DocIdSetIterator` / `Scorer`): the user drives iteration via `nextDoc()`.
2. **Push-based** (`Collector` / `LeafCollector` / `CollectorManager`): Lucene drives iteration, calling `collect(doc)` on a user-provided callback. Lucene manages per-segment parallelism.

Everything else in ES (ESQL operator pipelines, aggregations, TopDocs, Exchange) is built on one of these two.

### Layer 1 (bottom): `LuceneM` — free monad over Lucene primitives

The foundation. `LuceneM a` is a free monad that reifies **all** interactions with Lucene as data. Each operation is a constructor — building a `LuceneM` value does nothing; it describes what to do. An interpreter (`Lucene.run : LuceneM a → Channel a`) executes the description, managing resource lifecycle automatically (searcher release guaranteed on completion).

The Lucene primitives surfaced in the monad:

```
Lucene.acquire  : Index r → Shard → LuceneM (Searcher r)     -- acquireSearcher
Lucene.release  : Searcher r → LuceneM ()                     -- close searcher
Lucene.compile  : Searcher r → Query → LuceneM (Weight r)     -- query → Lucene Weight
Lucene.segments : Searcher r → LuceneM (List (Segment r))     -- reader.leaves()
Lucene.scorer   : Weight r → Segment r → LuceneM (DocIter r)  -- per-segment iterator
Lucene.next     : DocIter r → LuceneM (Maybe DocRef)          -- iterator.nextDoc()
Lucene.read     : Segment r → DocRef → String → LuceneM Value -- read DocValues
Lucene.collect  : Weight r                                     -- Lucene Collector (push-based)
               → (Segment r → acc)
               → (acc → Segment r → DocRef → acc)
               → (List acc → result)
               → LuceneM result

Lucene.run      : LuceneM a → Channel a                       -- interpret + execute
```

These mirror the real Lucene/ES objects 1:1. The user sees and controls Searchers, Weights, Segments, Scorers, DocIters — the actual machinery. The free monad is just the composition mechanism: it makes these interactions first-class values rather than side effects on mutable Java objects. Resource management is automatic — the interpreter guarantees searcher release when the `LuceneM` computation completes.

Because `LuceneM` is a monad, it supports standard monadic operations (`bind`/`>>=`, `pure`, `map`, `sequence`, etc.). This is the escape hatch for power users who need full control over every Lucene primitive.

### Layer 2 (middle): Pull/Push access patterns

Built on `LuceneM`. This layer provides the controlled push/pull interaction modes — the same `open`, `consume`, `read` from Block D, plus push-based `collect`, but now implemented as `LuceneM` programs rather than opaque Java builtins.

#### Pull mode

```
-- These are piescript functions that return LuceneM programs
Shard.open    : Index r → Shard → Query → LuceneM (PullHandle r)
Shard.consume : Int → PullHandle r → LuceneM (List DocRef)
Shard.read    : DocRef → String → LuceneM Value
```

`Shard.open` is implemented in piescript as:

```
let open idx shard q =
  Lucene.acquire idx shard >>= fn searcher ->
  Lucene.compile searcher q >>= fn weight ->
  Lucene.segments searcher >>= fn segs ->
  -- create scorers for matching segments, build PullHandle
  ...
```

The user doesn't see Searcher/Weight/Segment — they're encapsulated in `PullHandle`. But they could, by dropping to Layer 1.

#### Push mode

```
Shard.collect : Index r → Shard → Query
             → (Segment r → acc)
             → (acc → Segment r → DocRef → acc)
             → (List acc → result)
             → LuceneM result
```

This wraps `Lucene.collect` with automatic acquire/compile. The user explicitly chooses the Collector path for Lucene-optimized, parallel-per-segment push-based processing.

#### Segments

Segments are surfaced at this layer for explicit parallelism control:

```
Shard.segments : PullHandle r → LuceneM (List (Segment r))
```

Enables per-segment `spawn` for user-controlled parallelism.

#### Pull/Push as a type-level distinction

The mode can become a type parameter: `ShardM Pull r` vs `ShardM Push r`. Shared operations (`open`, `read`) work in both. Mode-specific operations (`consume` for pull, `onDoc`/`onMerge` for push) are restricted to their mode. `open` takes a mode indicator: `Shard.open Pull idx shard q` or `Shard.open Push idx shard q`.

### Layer 3 (top): Declarative combinators with typeclass-driven interpretation

Built on Layer 2. This is the ergonomic, high-level API for the 95% case. The user writes standard functional combinators — `scan`, `map`, `filter`, `fold`, `batch`, `collect` — and **typeclass instances decide how to interpret them**.

```
-- ShardPlan r a: a declarative description of a shard computation
Shard.scan   : Index r → Query → ShardPlan r (Stream r)
Shard.filter : (a → Bool) → ShardPlan r (Stream a) → ShardPlan r (Stream a)
Shard.map    : (a → b) → ShardPlan r (Stream a) → ShardPlan r (Stream b)
Shard.fold   : b → (b → a → b) → ShardPlan r (Stream a) → ShardPlan r b
Shard.batch  : Int → ShardPlan r (Stream a) → ShardPlan r (Stream (List a))
Shard.take   : Int → ShardPlan r (Stream a) → ShardPlan r (Stream a)

Shard.execute : ShardPlan r a → LuceneM a   -- compile plan to LuceneM program
```

The typeclass instances for `Filterable`, `Functor`, `Foldable` (etc.) on `ShardPlan` determine the execution strategy:

- A `filter` might push the predicate into the Lucene query (if it can be compiled to a `QueryBuilder`), or apply it post-materialization.
- A `fold` might compile to a `Lucene.collect` (push-based, parallel per segment) for efficiency.
- A `map` applied before `fold` might fuse into the collector's per-doc callback.
- A `batch 1000` followed by `map` might compile to pull-based batched iteration.

The user writes declarative, composable code. The typeclass machinery picks the best Layer 2 strategy, which in turn compiles to Layer 1 `LuceneM` primitives. The user doesn't think about push vs pull or segments — but they **could** drop down to Layer 2 or Layer 1 for explicit control at any time.

### How Block D fits

Block D implements `open`, `consume`, `read` as **opaque Java builtins** — not as `LuceneM` programs. This is the vertical slice. The evolution path:

1. **Block D (now)**: `open`, `consume`, `read` as 3 Java-backed builtins. Pull-based. Segments hidden. No `LuceneM`.
2. **Next**: Surface `Segment r`. Add push-based `collect` primitive. Pull/push as distinct entry points.
3. **Then**: Implement `LuceneM` as the foundational free monad. Rewrite `open`/`consume`/`read`/`collect` as `LuceneM` programs (Layer 2). The Java-backed builtins become the `LuceneM` primitive operations (Layer 1).
4. **Later**: Add `ShardPlan` combinators (Layer 3). Implement typeclass instances for interpretation. `Filterable` pushes predicates to Lucene queries. `Foldable` compiles to Collectors.
5. **Scale**: `Materialize a` typeclass for output format (Records, Pages/Blocks). Exchange integration. Operator pipeline bridging into ESQL's compute engine.

---

## D.0 — Qualified Builtin Names (Namespacing)

All builtins move to qualified names (`Namespace.name`). No backwards compatibility with unqualified names — clean break.

### Grammar

- Parser: allow `UPPER_IDENT DOT LOWER_IDENT` as a qualified name in expression position
- Elaborator: look up qualified name in the Prelude module map (keys become `"Math.abs"`, `"List.map"`, etc.)
- Core IR: `CoreFree("Math.abs")` — same mechanism, just qualified string key
- Runtime: `BuiltinVal("Math.abs", 1, [])` — dispatch in `EvalBuiltins` on qualified name

### Namespace assignments


| Namespace | Builtins                                                                     | Notes                                                                    |
| --------- | ---------------------------------------------------------------------------- | ------------------------------------------------------------------------ |
| `Math`    | `abs`, `floor`, `ceil`, `round`, `sqrt`, `log`, `min`, `max`, `pow`, `toInt` | All `Double → Double` or `Double → Double → Double`                      |
| `List`    | `map`, `filter`, `reduce`, `head`, `tail`, `length`, `isEmpty`, `at`         | Polymorphic list operations                                              |
| `Cluster` | `topology`                                                                   | Cluster-level info                                                       |
| `Shard`   | `open`, `consume`, `read`                                                    | Shard-level data access (pull-based)                                     |
| `Index`   | `routing`, `shards`, `nodes`                                                 | Index-level routing and topology                                         |
| `Query`   | `matchAll`, `term`, `range`, `bool`                                          | Query construction helpers (optional — users can build records directly) |


### Impact

- Update `Prelude.MODULE` keys and `Prelude.ARITY` keys
- Update `EvalBuiltins` switch cases
- Update all existing tests (elaborator, evaluator, integration)
- Update debug scripts and manual test programs
- Do this **first** in Block D (D.0) so all subsequent work uses qualified names

---

## D.1 — Grammar + Type System

- ANTLR grammar: `use` production (e.g., `USE DOT_IDENT AS LOWER_IDENT` or similar — exact lexer token TBD for index names with hyphens/dots). `use` desugars to `CoreLet` binding an `IndexVal` literal — no new Core IR node for `use`.
- New type constructors in the type system: `Index r`, `Searcher r` (alongside existing `Channel r`, `List r`)
- `DocRef` as a monomorphic opaque type (no type parameter — it's just a handle)
- New `Value` variants:
  - `IndexVal(name, uuid, fieldMetadata)` — **serializable** (travels in closures to data nodes)
  - `SearcherVal(engineSearcher, weight, scorers[], cursor, indexService)` — **non-serializable** (holds JVM resources)
  - `DocRefVal(leafReaderContext, localDocId)` — **non-serializable** (tied to its Searcher)
- `Shard.open`, `Shard.consume`, `Shard.read` added to `Prelude` with type schemes
- Update `routing`/`shards`/`nodes` type schemes from `Keyword →` to `Index r →`
- New list utility: `at : Double → List a → a` — index-based access (0-based). Throws on out-of-bounds. Added to `Prelude` (arity 2) and `EvalBuiltins`.
- Existing `query` path unaffected — additive changes only.

## D.2 — Index Resolution Pre-Pass

- Extend `IndexResolutionPrePass` to scan for `use .index-name as idx` declarations
- Run field caps for each `use` declaration (same mechanism as `query` today)
- Store resolved mappings keyed by the `use` binding name (or source location)
- The elaborator produces `Index r` with the concrete row type from field caps
- At runtime, `use` evaluates to an `IndexVal` carrying name, UUID, and field metadata

## D.3 — `Shard.open` Implementation

- Prelude type: `∀r. Index r → ShardRecord → { match_all: Boolean } → Channel (Searcher r)`
- `Shard.open` is a **regular builtin** — no special Core IR node. Its Java implementation:
  1. Evaluate all three arguments (`IndexVal`, `RecordVal` shard, `RecordVal` query)
  2. Create a `ChannelVal` via `ChannelRegistry`
  3. Submit async work to a thread pool:
    a. Extract index name + UUID from `IndexVal`
     b. Extract shard ID from shard `RecordVal`
     c. Convert query `RecordVal` → `QueryBuilder` (pattern match on record fields: `match_all`, `term`, `range`, `bool`)
     d. `indicesService.indexServiceSafe(new Index(name, uuid))`
     e. `indexService.getShard(shardId)`
     f. `indexShard.acquireSearcher("piescript_open")`
     g. `indexService.newSearchExecutionContext(shardId, 0, searcher, ...)`
     h. `ctx.toQuery(queryBuilder).query()` → Lucene `Query`
     i. `searcher.createWeight(query, ScoreMode.COMPLETE_NO_SCORES, 1.0f)` → `Weight`
     j. For each `LeafReaderContext` in `searcher.getIndexReader().leaves()`: create `Scorer` via `weight.scorer(leafCtx)`, skip null scorers (no matches in that segment)
     k. Build `SearcherVal` with the searcher, weight, scorers, and initial cursor state
     l. Complete the channel with `SearcherVal`
  4. Return `ChannelVal` immediately
- The builtin needs access to `ChannelRegistry` (for channel creation), `IndicesService` (for shard access), and a thread pool executor (for async work) — all via `EvalDependencies`.

## D.4 — `Shard.consume` + `Shard.read` Implementation

### `Shard.consume`

- Prelude type: `∀r. Double → Searcher r → List (DocRef r)` — note `DocRef` is parameterized by `r` to thread the row type through
- `EvalBuiltins`: new `Shard.consume` case
  1. Extract `SearcherVal` and N (as int)
  2. Advance the internal cursor: iterate through segments' `Scorer.iterator()`, calling `nextDoc()` until N docs are collected or all segments are exhausted
  3. For each collected doc: wrap as `DocRefVal(leafReaderContext, localDocId, searcherState)`
  4. Return `ListVal` of `DocRefVal`s
  5. The `SearcherVal`'s internal cursor state (current segment index, current position within segment) is mutable and updated in-place
- If the searcher is already exhausted, return an empty list.

### `Shard.read`

- Prelude type: `∀r. DocRef r → r` — reads **all** mapped fields and returns a record matching the row type `r`
- `EvalBuiltins`: new `Shard.read` case (arity 1, no field name parameter)
  1. Extract `DocRefVal` (leafReaderContext + localDocId + searcherState back-reference)
  2. Get `MapperService` from `searcherState.indexService.mapperService()`
  3. Iterate all mapped field types via `mappingLookup().getMatchingFieldNames("*")` (skip `_`-prefixed metadata fields, skip fields without doc values)
  4. For each field: read DocValues from `leafReaderContext.reader()`, convert to piescript `Value`
  5. **Throw** on missing doc values — the type system guarantees the field exists, so a missing value is a hard error (no nulls)
  6. Build and return `RecordVal` from all field values
- DocValues reading per field type:
  - `SortedSetDocValues`: `advanceExact(docId)` → `lookupOrd(nextOrd())` → `BytesRef` → `KeywordVal`
  - `SortedNumericDocValues`: `advanceExact(docId)` → `nextValue()` → decode based on field type → `DoubleVal` / `BooleanVal`
  - Unsupported types: throw `EvaluationException` (no silent fallback)
- **Design note:** Individual field projection (e.g. `Shard.read ref "fieldname"`) requires a `Label` kind for type-level singleton strings. See "Future: Label Kind" below.

## D.5 — `EvalDependencies` + Wiring

- Add `IndicesService` to `EvalDependencies`
- Add a thread pool executor for async `open` work (could use ES's `SEARCH` thread pool or a dedicated one)
- Wire in `PiescriptPlugin.createComponents()` (available as a standard component)
- Pass through to inbox evaluator (same as `TransportService`, `ChannelRegistry`)
- Add UUID field to shard records in `EvalTopology.buildShardCoreFields()`

## D.6 — `routing` Update

- Change `routing`, `shards`, `nodes` to accept `IndexVal` instead of `KeywordVal`
- Extract index name from `IndexVal.name()` (instead of `KeywordVal.value()`)
- Update `EvalTopology.resolveRouting()` parameter handling
- Update Prelude type schemes

## D.7 — Unit Tests

- `use` parsing (parser tests)
- `Index r` type resolution (elaborator tests: field caps → concrete row type)
- `Shard.open` evaluator tests (with mock `IndicesService` / shard — verify channel creation and async completion)
- `Shard.consume` cursor semantics tests (consume N, consume again continues, consume past end returns empty)
- `Shard.read` tests per field type (keyword, numeric, boolean) — reads all fields, returns RecordVal
- `Shard.read` throws on missing doc values (no silent nulls)
- Query record → QueryBuilder conversion tests
- `SearcherVal` / `DocRefVal` non-serialization test (serialization rejects them)
- `routing` with `IndexVal` argument tests

## D.8 — Integration Tests

- Single-node: `use` + `Shard.open` + `Shard.consume` + `Shard.read` on a local shard
- Multi-node: ship closure to data node, open + consume + read on remote shard, send results back
- Fan-out: open on multiple shards across nodes, collect via channels
- Query types: `match_all`, `term`, `range`, `bool` on real data
- Batched consume: consume 10 at a time, verify all docs retrieved
- Error case: open with wrong shard ID (shard not on node)

## D.9 — Documentation Update

Update all relevant documentation files to record Block D's design decisions, progress, known limitations, and long-term architectural vision. Include the reference link to the design discussion in each updated file.

**Design discussion reference**: [Block D design discussion](01e7770e-9e20-41ae-a116-2e78142bb672) — covers the full rationale for `open`/`consume`/`read` primitives, the three-layer architecture (LuceneM → Push/Pull → Declarative), push/pull duality, segment surfacing, and the evolution path from Block D's vertical slice to the full vision.

### `decisions.md` — D-050 decision record

Write a D-050 decision record capturing all Block D design decisions:

- `use` declaration and `Index r` type with elaboration-time field caps
- Three pull-based primitives: `Shard.open` (async, returns `Channel (Searcher r)`), `Shard.consume` (cursor-based pull), `Shard.read` (DocValues reading)
- Queries as plain records (runtime conversion to `QueryBuilder`)
- `Searcher r` and `DocRef r` as opaque, non-serializable, node-local types (row type `r` threaded through for type safety)
- Resource management approach (auto-release on exhaustion, explicit `release` deferred)
- Qualified builtin names (namespacing)
- Reference the design discussion and this plan for full context

### `current-state.md`

Update to reflect Block D completion status:

- New capabilities: `use`, `Shard.open`, `Shard.consume`, `Shard.read`
- New types: `Index r`, `Searcher r`, `DocRef r`
- Known limitations: no size limit, potential searcher leak if not fully consumed, no security enforcement, no `Maybe`/ADTs for consume exhaustion signaling, no recursion for batched consumption, `Shard.read` is wildcard-only (individual field projection requires `Label` kind — see future work)
- Reference the design discussion

### `roadmap.md`

Update Block D status and add long-term vision notes:

- Mark Block D items as complete/in-progress as appropriate
- Add a "Future: Shard Interaction Architecture" subsection documenting the three-layer vision:
  - Layer 1: `LuceneM` free monad over all Lucene primitives
  - Layer 2: Pull/Push access patterns built on `LuceneM`
  - Layer 3: Declarative combinators with typeclass-driven interpretation
- Document the evolution path (Block D → segments + push → LuceneM → ShardPlan → compute engine integration)
- Reference the design discussion

---

## Future: `Label` Kind and Type-Level Field Projection

The current `Shard.read : ∀r. DocRef r → r` is a wildcard read — it materializes the entire record. This is type-safe (the return type `r` matches the `Index r` row type end-to-end) but does not support reading individual fields.

To enable statically-typed individual field access, piescript needs:

1. **A `Label` kind** — a new entry in `Kind.java` alongside `TYPE` and `ROW`. Values of kind `Label` are type-level singleton strings representing field names.
2. **A `Project` type family** — `Project r f` extracts the type of field `f` from row `r`. This is a type-level function, not a term-level one.
3. **Updated `Shard.read` signature** — `∀(r : Row)(f : Label). DocRef r → f → Project r f`. The field name argument is a term whose type is a `Label` singleton, and the return type is the projection of that label from the row.

This enables expressions like `Shard.read ref "name"` where:
- The elaborator checks that `"name"` is a valid field in `r`
- The return type is statically known (e.g. `Keyword` if `name : Keyword ∈ r`)
- Type errors are caught at elaboration time, not runtime

The `Label` kind also enables future work on typed record projections more broadly (e.g. `record.field` syntax with static checking).

**Current workaround:** `Shard.read` reads all fields and the caller uses record projection to access individual fields from the resulting record. This is correct but materializes all fields even if only one is needed.

## Future (high priority): Rows as First-Class MonoTypes

**Problem:** `RowType` is currently a separate structure from `MonoType`. Rows only enter the `MonoType` space via the `RecordType(RowType)` wrapper. This means:

- Type constructors parameterized by a schema (like `Index r`, `Searcher r`, `DocRef r`) carry `r` as `Kind.TYPE`, not `Kind.ROW`. The type variable `r` unifies with a full `RecordType(closed({...}))`, not a bare row.
- There is **no kind-level constraint** preventing `DocRef Double` or `Index Boolean` — the type system treats `r` as any type. Only the runtime enforces that `r` is actually a record.
- `Shard.read : ∀r. DocRef r → r` works because `r` happens to unify with a `RecordType`, but this is not enforced by the kind system.

**Fix:** Promote rows to first-class `MonoType` citizens. This is **orthogonal** to the question of whether `Record` should be modeled as `App(Record, Row)` (a type constructor of kind `Row → Type`) or kept as the current `RecordType(RowType)` structure — the latter has advantages for pattern matching in Java and can be kept as-is. The key change is that `RowType` must be representable in `MonoType` so that:

1. `Kind.ROW` metavariables can appear in `AppType` arguments
2. Type constructors like `Index`, `Searcher`, `DocRef` can be properly kinded as `Row → Type`
3. `Shard.read : ∀(r : Row). DocRef r → Record r` has `r` correctly constrained to a row
4. The zonker/unifier resolves `Kind.ROW` metas to row solutions in all positions

**Scope:** touches `MonoType`, `RowType`, unification, zonking, serialization, instantiation. Estimated ~dozen files. Should be done alongside or before the `Label` kind work, as `Label` also requires rows to be first-class for `Project r f` to work.

---

## Target Program (full vertical slice)

```
use .my-index as idx

let topo = Cluster.topology "cluster"
let shards = Index.routing idx
let nodes = Index.shards idx

-- fan out to each shard's node
shards |> List.map (fn shard ->
  let node = List.at shard.node nodes
  let resultCh = spawn!
  in spawn @node (
    let openCh = Shard.open idx shard { match_all: true }
    in when (openCh searcher) ->
      let refs = Shard.consume 10000 searcher
      let records = List.map (fn ref -> Shard.read ref) refs
      in send resultCh records
  )
  resultCh
)
```

**Notes on the target program:**

- `Shard.consume 10000` is a simplification for the vertical slice — assumes fewer than 10000 docs per shard. Recursive/batched consumption requires recursion support (not yet available in piescript).
- `Shard.read ref` reads all mapped fields, returning a `RecordVal` matching the `Index r` row type. The row type `r` is threaded through `Index r → Searcher r → DocRef r → r`, ensuring end-to-end type safety.
- The async flow is: `spawn @node` ships the closure, `Shard.open` returns a channel (async I/O), `when` fires when the searcher is ready, then `consume` + `read` are synchronous within the spawned context.
- Results are sent back to the coordinator via `resultCh`.

