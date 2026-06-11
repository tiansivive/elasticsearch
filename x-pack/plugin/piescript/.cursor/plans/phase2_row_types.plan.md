---
name: "Phase 2 (original): Index Resolution + Concrete-Row Constraints"
overview: "Original Phase 2 design plan. Superseded by phase2_implementation.plan.md which resolved inconsistencies and added eager evaluation. Kept for historical reference."
todos: []
isProject: false
---

# Phase 2: Index Resolution + Concrete-Row Constraints (SUPERSEDED)

> **This plan has been superseded by [phase2_implementation.plan.md](phase2_implementation.plan.md).**
> Kept for historical reference. See the implementation plan for what was actually built.

**Parent:** `scripting_language_design_9286506e.plan.md`
**Status:** Superseded — see phase2_implementation.plan.md
**Dependencies:** Phase 1d (open-row unification, row polymorphism), Phase 1 (elaborator, Core IR, interpreter)

> **Note (D-029):** The open-row unification algorithm (Q2.1), row variable generalization (Q2.3),
> and core row polymorphism have been **pulled forward to Phase 1d** as part of the phase
> reordering. This plan now covers the remaining Phase 2 work: index resolution, concrete-row
> constraints, and `query` expression typing. Q2.1 and Q2.3 design questions below remain
> relevant as reference material for Phase 1d implementation.

## Goal

Programs containing `query` expressions are typechecked against real ES index mappings. Cross-index type conflicts and unmapped fields produce precise, actionable errors at the field-access site. Builds on the row polymorphism infrastructure from Phase 1d.

## Vertical Slice

```bash
# Setup: index with known mappings
# logs-test has fields: @timestamp (date), message (keyword), status (integer)

curl -X POST "localhost:9200/_yourlang/eval" \
  -H "Content-Type: application/json" \
  -d '{
    "program": "let docs = query FROM logs-test; in docs |> map .message"
  }'

# Returns columnar result with just the message column
```

Also (negative test — cross-index type conflict):

```bash
# logs-a has status: integer, logs-b has status: keyword

curl -X POST "localhost:9200/_yourlang/eval" \
  -d '{
    "program": "let docs = query FROM logs-*; in docs |> map (fn d -> d.status + 1)"
  }'

# Returns type error: "status has type Int in [logs-a] but Keyword in [logs-b]"
```

Also (positive — conflicting field never accessed):

```bash
curl -X POST "localhost:9200/_yourlang/eval" \
  -d '{
    "program": "let docs = query FROM logs-*; in docs |> map .message"
  }'

# Succeeds — status conflict is latent but .message is consistent across all indices
```

## Open Questions (Must Resolve Before Implementation)

### Q2.1: Row unification algorithm

The elaborator from Phase 1 has simple unification (TCon, ->, metavars). Phase 2 extends it with row unification.

**Core algorithm for `rho ~ (l: alpha | r)`:**

When unifying a row metavar `rho` with a row constraint `(l: alpha | r)`:

1. If `rho` is unsolved: record the constraint (or decompose immediately?)
2. If `rho` is solved to a concrete row `(l1: t1, ..., ln: tn)`:
  - Find `l` among `l1...ln`. If found: unify `alpha ~ ti`, bind `r` to the remaining fields.
  - If not found: unification failure (field missing).
3. If `rho` is solved to another open row `(l1: t1, ..., ln: tn | rho')`:
  - Find `l` among `l1...ln`. If found: unify `alpha ~ ti`, bind `r` to `(remaining | rho')`.
  - If not found: emit `rho' ~ (l: alpha | r')` with fresh `r'`, and bind `r` to `(l1: t1, ..., ln: tn | r')`.

**Sub-questions:**

- **Occurs check for row variables:** Standard occurs check prevents `rho ~ (l: alpha | rho)`. Is this sufficient or do we need a more sophisticated check for rows?
- **Row ordering:** Rows are unordered sets of label-type pairs. The algorithm must handle `(a: Int, b: Keyword)` unifying with `(b: Keyword, a: Int)`. Standard approach: normalize by label before comparison.
- **Duplicate labels:** Forbidden. `(a: Int, a: Keyword)` is a kind error. Checked during elaboration when constructing row types.
- **Constraint representation:** Do we use substitution-based unification (eagerly substitute solved metavars) or constraint-based (collect constraints, solve later)? The concrete-row mechanism favors eager substitution with deferred concrete-row checks.

**Status:** Partially designed (see master plan Section 4). Algorithm details need to be written out.

### Q2.2: Concrete-row constraint processing

This is the mechanism from master plan Section 4.2. Implementation questions:

- **When are concrete-row checks triggered?** Option A: Immediately when a row constraint is emitted against a metavar that has concrete rows. Option B: Deferred to a post-pass after all constraints are solved.
- **Recommend Option A** (immediate): simpler, errors are reported at the site that caused them, no need for a separate pass.
- **Union-find metadata:** Each row metavar's representative in the union-find carries a `Set<ConcreteRow>`. When two metavars are merged, their concrete-row sets are unioned. When a row constraint is processed, if the representative has concrete rows, the constraint is also checked against each.
- **What is a `ConcreteRow`?** A closed row with label-type pairs plus an index name (or set of index names) for diagnostics: `ConcreteRow(Map<Label, Type>, Set<String> indices)`.
- **Failure reporting:** On unification failure against a concrete row, the error includes: the field name, the expected type (from the constraint), the actual type (from the concrete row), and the index names.

**Status:** Mechanism designed. Implementation details (union-find augmentation) need confirmation.

### Q2.3: Generalization and row variables

When generalizing a let-binding's type to a scheme, row variables must be handled:

```
let extract = fn doc -> doc.message
```

Inferred type before generalization: `{ message: alpha | rho } -> alpha`
After generalization: `forall alpha rho. { message: alpha | rho } -> alpha`

**Questions:**

- Do we generalize row variables the same way as type variables? (Yes, standard.)
- Value restriction: does it apply? In a pure language without mutation, the answer is typically no. ML's value restriction exists because of mutable references; we don't have those.
- Monomorphism restriction (Haskell-style): do we apply it? (Recommend: no. It's widely considered a mistake in Haskell.)

**Status:** Straightforward, but worth confirming explicitly.

### Q2.4: Translating EsField to row types

`IndexResolver` returns `Map<String, EsField>`. Each `EsField` has:

- `name: String`
- `dataType: DataType`
- `properties: Map<String, EsField>` (for nested objects)

Translation to row types:

- Flat field: `EsField("status", INTEGER, {})` -> label `status` with type `TCon "Int"`
- Nested field: `EsField("host", OBJECT, { "name" -> EsField("name", KEYWORD, {}) })` -> label `host` with type `{ name: Keyword }`
- The `DataType` enum needs a mapping to `TCon` strings. This is a simple lookup table.

**Questions:**

- How do we handle `DataType` values that don't have obvious TCon names? (e.g., `UNSIGNED_LONG`, `IP`, `GEO_POINT`)
- Do we expose ALL ES types or a curated subset for v0?
- `_source`, `_id`, `_index` meta-fields: include in the row type or accessible via special syntax?

**Status:** Straightforward translation, but need the DataType -> TCon mapping table.

### Q2.5: Async index resolution during typechecking

`IndexResolver.resolveAsMergedMapping()` is async (returns via `ActionListener`). The typechecker is synchronous tree-walking. How do we bridge this?

**Options:**

- **Option A:** Resolve all `query` expressions' index patterns upfront (before elaboration), then pass the resolved mappings into the elaborator as context. Simple. Requires a pre-pass to collect all `query` nodes.
- **Option B:** Block the elaborator thread when it hits a `query` node, await the async result. Works but wastes a thread.
- **Option C:** Make the elaborator itself async (CPS or CompletableFuture-based). Complex but principled.

**Recommend Option A** for v0. Pre-pass collects all index patterns from `query` nodes in the CST, resolves them all (possibly in parallel), then elaboration proceeds synchronously with the resolved mappings available.

**Status:** Leaning Option A, needs confirmation.

## Tasks (Ordered by Dependency)

### T2.1: Resolve open questions Q2.1-Q2.5

Design session for row unification algorithm details and the async resolution strategy.

**Depends on:** Phase 1 elaborator exists (to understand the unification infrastructure)
**Blocks:** Everything else in Phase 2

### T2.2: Extend unification with row types

Add to the Phase 1 unifier:

- Row type representation: `RowType(Map<Label, Type>, Optional<MetaVar> tail)`
- Row unification: decompose `(l: alpha | r) ~ concrete_or_open_row`
- Occurs check for row variables
- Row normalization (label ordering)

**Depends on:** T2.1

### T2.3: Augment union-find with concrete-row metadata

- Each metavar representative carries `Set<ConcreteRow>`
- On merge: union the sets
- On row constraint processing: if representative has concrete rows, check against each
- On failure: produce diagnostic with index names

**Depends on:** T2.2

### T2.4: Implement index resolution pre-pass

- Walk the CST, collect all `query` nodes and their index patterns
- Call `IndexResolver.resolveAsMergedMapping()` for each (or batch via `fieldCaps` multi-index)
- Build `Map<QueryNode, ResolvedMapping>` where `ResolvedMapping` contains:
  - The merged field map (for the "happy path" single row)
  - Per-index-group concrete rows (for conflict detection)
  - Conflict info from `InvalidMappedField` and `partiallyUnmappedFields`
- Pass this map into the elaborator as context

**Depends on:** Phase 0 (plugin can call IndexResolver), Phase 1 (parser produces CST with query nodes)

### T2.5: Wire query typing into elaborator

When the elaborator encounters a `query` node:

1. Look up the `ResolvedMapping` from the pre-pass context
2. Create a fresh row metavar `rho`
3. Translate each index group's field map to a `ConcreteRow`, attach to `rho`
4. Return type `Stream (Record rho)`
5. Downstream field accesses on this stream's elements emit row constraints against `rho`, which trigger concrete-row checks

**Depends on:** T2.3, T2.4

### T2.6: Implement `map`/`filter` as built-in functions

For Phase 2, these are typed but don't need full runtime execution (that's Phase 3). The elaborator needs to know their types:

- `map : forall a b. (a -> b) -> Stream a -> Stream b`
- `filter : forall a. (a -> Bool) -> Stream a -> Stream a`

Register these in the initial type environment as built-in schemes.

**Depends on:** T2.2 (row polymorphism needed for `a` and `b` to be row types)

### T2.7: DataType to TCon mapping

Create the lookup table from ES `DataType` enum values to `TCon` strings. Handle edge cases (unsupported types, meta-fields).

**Depends on:** Q2.4 resolved

### T2.8: Integration tests

- **Row polymorphism:** Function over partial row works across different record types
- **Index resolution:** `query FROM real-index` gets correct row type
- **Concrete-row conflict:** Access conflicting field -> precise error with index names
- **Unmapped field:** Access field missing in some indices -> error with index names
- **No conflict access:** Access only consistent fields across conflicting indices -> success
- **Polymorphic propagation:** Pass query result to generic function, conflict detected at instantiation

**Depends on:** T2.5, T2.6

## Implications for Master Plan

- Row unification algorithm details (Q2.1) will be documented in a new Section 13.5 or as an appendix
- DataType -> TCon mapping (Q2.4) refines Section 3.2
- Async resolution strategy (Q2.5) adds detail to Section 10.4 pipeline

## Estimated Scope

Medium. The row unification extension is the core work (~5-10 files). Index resolution integration reuses existing `IndexResolver`. Concrete-row augmentation is a localized change to the union-find. Expect ~15-25 files (including tests).