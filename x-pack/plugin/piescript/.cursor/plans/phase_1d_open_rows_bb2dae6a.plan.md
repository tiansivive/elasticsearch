---
name: Phase 1d Open Rows & Row Polymorphism
overview: |
  Implement open-row unification (Leijen-style, adapted to flat-map RowType), row polymorphism,
  rigid type variables (MonoType.Rigid), the D-028 type variable convention at the ANTLR level,
  and type annotation elaboration into TypeSchemes. Supersedes D-021 (closed-row sugar).
todos:
  - id: t1d-1
    content: "MonoType: add Rigid(int id, Kind kind) variant to sealed interface"
    status: completed
  - id: t1d-2
    content: "TypeScheme: change quantified from Set<Integer> to Map<Integer, Kind>; update all call sites"
    status: completed
  - id: t1d-3
    content: "ANTLR: split IDENTIFIER into UPPER_IDENT / LOWER_IDENT in lexer; update parser rules"
    status: completed
  - id: t1d-4
    content: "ElaborationState: rename resolveType to zonk, return Optional<MonoType>; add resolveRow flattening"
    status: completed
  - id: t1d-5
    content: "Unifier: rewrite unifyRows with Leijen-style open-row decomposition; add Rigid handling"
    status: completed
  - id: t1d-6
    content: "TypeWalker: remove resolveDeep; kind-aware instantiate; collectMetas returns Map<Integer, Kind>"
    status: completed
  - id: t1d-7
    content: "Elaborator: resolveTypeAnnotation returns TypeScheme; checking rule for universal types; open-row sugar"
    status: completed
  - id: t1d-8
    content: "CorePrinter: inline zonk at point of use; handle Rigid variant"
    status: completed
  - id: t1d-9
    content: "Tests: UnifierTests (open rows), ElaboratorTests (type variables, row polymorphism), EvaluatorTests (end-to-end)"
    status: completed
  - id: t1d-10
    content: Update living docs (current-state, project-structure, decisions, roadmap)
    status: cancelled
isProject: false
---

# Phase 1d: Open Rows and Row Polymorphism

## Summary of Design Decisions

These decisions were made through detailed discussion and are authoritative for implementation.
Each references a formal ADR in `decisions.md`.

1. **Leijen-style row unification** (not Rémy) — adapted to our flat-map `RowType`. One recursive
  function, no 4-way pattern matching on row constructors. (D-030)
2. `**MonoType.Rigid` — new variant for bound/skolemized type variables. Distinct from `Meta`
  (which is a unification variable / "hole"). (D-031)
3. **Zonker API cleanup** — `resolveType` renamed to `zonk`, returns `Optional<MonoType>` (empty
  on unsolved meta, not the meta itself). No `resolveDeep`. (D-032)
4. **ANTLR lexer split** — `UPPER_IDENT` and `LOWER_IDENT` replace `IDENTIFIER` in type positions.
  The case convention is enforced at the grammar level, not the elaborator. (D-033)
5. **Type annotations elaborate to `TypeScheme`** — `resolveTypeAnnotation` returns `TypeScheme`
  when type variables are present. Checking against a universal type peels the quantifier and
   introduces Rigids. Annotated definitions store the scheme directly (no generalization step).
   Unannotated definitions generalize over unsolved metas. (D-034)
6. `**TypeScheme.quantified**` carries `Map<Integer, Kind>` — enables kind-aware instantiation
  (TYPE metas vs ROW metas). (Part of t1d-2)
7. **No `Forall` in `MonoType`** — `TypeScheme` is our type abstraction. For rank-1, quantifiers
  only appear at the top level, so a separate `MonoType` variant is unnecessary.

### Implementation Deviations (Phase 1d session)

The following deviations from this plan were introduced during the Phase 1d implementation
session and are tracked as technical debt:

1. **D-032 not fully implemented**: `resolveType` was renamed to `zonkOrKeep` (preserving the
   old return-meta-on-miss semantics) rather than the `Optional`-returning `zonk` specified in
   D-032. `resolveDeep` was **not removed** from `TypeWalker` — it is still used by `generalize`
   (to deep-resolve types before collecting metas) and by `CorePrinter`. This violates D-005's
   no-substitution principle.

2. **Instantiation uses `walkType` substitution**: `TypeWalker.instantiate` still replaces
   Rigids with fresh Metas via `walkType(scheme.body(), freshening)`. This is a tree-walking
   substitution — exactly the kind of operation D-005 prohibits.

3. **No `CoreTypeAbs`/`CoreTypeApp` in Core IR**: The plan describes the core as "System
   F-omega-like" with type abstractions "inferred by the elaborator" but the implementation
   did not add `CoreTypeAbs` or `CoreTypeApp` nodes to the `CoreExpr` hierarchy. Polymorphic
   bindings store a `TypeScheme` in the elaboration context but do not emit type abstraction
   nodes in the Core IR. Instantiation sites do not emit type application nodes.

**Resolution**: D-035 captures the correct design: add `CoreTypeAbs` and `CoreTypeApp` to the
Core IR, emit them during elaboration, and resolve Rigids via environment-based lookup. This
eliminates `resolveDeep`, `walkType`-based instantiation, and the `zonkOrKeep` workaround.
See [decisions.md § D-035](decisions.md).

## Architecture Overview

```
                          CST type annotation
                                 │
                    ┌────────────▼────────────┐
                    │  resolveTypeAnnotation   │
                    │  (collects LOWER_IDENTs  │
                    │   as Rigids, returns     │
                    │   TypeScheme)            │
                    └────────────┬────────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                   │
     Annotated def       Unannotated def       Use site
     let f : a -> a      let g = fn x -> x     g 42
              │                  │                   │
     TypeScheme from       Infer body type      Lookup scheme
     annotation            (uses Metas)         in environment
              │                  │                   │
     Check body against    Generalize unsolved   Instantiate:
     Rigid(0) → Rigid(0)  metas → TypeScheme    replace Rigids
              │                  │               with fresh Metas
     Store scheme          Store scheme              │
     in environment        in environment        Unify Meta
                                                 with Integer
```

## Type System Changes

### 1. `MonoType.java` — add `Rigid`

```java
/**
 * Bound (skolemized) type variable, introduced when:
 * - Checking a body against a universal type (from annotation)
 * - Generalizing an unannotated definition (unsolved metas become Rigids)
 *
 * Rigids do NOT unify with anything except themselves (same id).
 * Attempting to unify Rigid(0) with Integer is a type error.
 */
record Rigid(int id, Kind kind) implements MonoType {}
```

The `Rigid` variant must be handled in:

- `Unifier.unify` — two Rigids with the same id unify; Rigid vs anything else is a Mismatch
- `Unifier.occursIn` — Rigid is a leaf (like TCon)
- `TypeWalker.walkType` — Rigid may appear in substitution maps
- `TypeWalker.collectMetas` — Rigids are NOT metas; skip them
- `CorePrinter` — render as type variable name (e.g., `a`, `b`, `r`)
- `Evaluator` — should never encounter Rigids at runtime (erased by instantiation)

### 2. `TypeScheme.java` — kind-aware quantification

```java
// Before:
public record TypeScheme(Set<Integer> quantified, MonoType body) { ... }

// After:
public record TypeScheme(Map<Integer, Kind> quantified, MonoType body) { ... }
```

The quantified map now records which kind each bound variable has. This is needed for
kind-aware instantiation: TYPE variables are replaced with `freshType`, ROW variables
with `freshRow`.

Call sites to update:

- `TypeWalker.generalize` — build `Map<Integer, Kind>` from collected metas
- `TypeWalker.instantiate` — use kind to choose `freshType` vs `freshRow`
- `TypeScheme.mono()` — `Map.of()` instead of `Set.of()`
- `ElaborationContext.lookup` — unchanged (returns `TypeScheme`)
- Tests: `ElaborationStateTests`, `ElaboratorTests`

### 3. `ElaborationState.java` — zonker API cleanup

**Rename `resolveType` → `zonk`** with corrected semantics:

```java
// Before (problematic — returns the meta itself on miss):
public MonoType resolveType(MonoType type) {
    return switch (type) {
        case MonoType.Meta meta -> resolve(meta.id())
            .filter(MonoType.class::isInstance)
            .map(MonoType.class::cast)
            .map(this::resolveType)
            .orElse(type);      // ← returns the meta itself
        default -> type;
    };
}

// After (correct — Optional.empty() on unsolved):
public Optional<MonoType> zonk(MonoType type) {
    return switch (type) {
        case MonoType.Meta meta -> resolve(meta.id())
            .filter(MonoType.class::isInstance)
            .map(MonoType.class::cast)
            .flatMap(this::zonk);    // ← chain resolution, empty if unsolved
        default -> Optional.of(type);
    };
}
```

Callers that need the "return the type or the meta if unsolved" behavior use:
`state.zonk(type).orElse(type)`.

**Add `resolveRow(RowType)`** for flattening row tails through the zonker:

```java
public RowType resolveRow(RowType row) {
    if (row.rowVar().isEmpty()) return row;
    var meta = row.rowVar().get();
    var solution = resolve(meta.id());
    if (solution.isEmpty()) {
        // Unsolved — check if meta chains to another meta
        var zonked = zonk(meta);
        if (zonked.isPresent() && zonked.get() instanceof MonoType.Meta m && !m.equals(meta)) {
            return new RowType(row.fields(), Optional.of(m));
        }
        return row;
    }
    if (solution.get() instanceof RowType tailRow) {
        var merged = new LinkedHashMap<>(row.fields());
        merged.putAll(tailRow.fields());
        return resolveRow(new RowType(merged, tailRow.rowVar()));
    }
    return row;
}
```

## Unification Changes

### 4. `Unifier.java` — Leijen-style open-row unification + Rigid handling

**Rigid handling in `unify`**:

```java
if (ra instanceof MonoType.Rigid(var id1, var k1)
    && rb instanceof MonoType.Rigid(var id2, var k2)
    && id1 == id2) {
    return Optional.empty();  // same rigid — OK
}
// Rigid vs anything else (including Meta) falls through to Mismatch
```

Note: a Meta should NOT unify with a Rigid. Rigids are skolem constants — they represent
"for all" universally quantified variables. Solving a meta to a Rigid would be wrong because
it would "escape its scope." The standard approach: Metas unify with Metas and concrete types;
Rigids only unify with the same Rigid.

**Open-row unification** (`unifyRows`):

Adapted from Leijen's approach to our flat `RowType(Map<String, MonoType>, Optional<Meta>)`:

```
unifyRows(rowA, rowB, state):
  1. Flatten both rows via state.resolveRow()
  2. Pairwise-unify common fields (labels in both)
  3. Compute onlyA, onlyB (excess fields)
  4. Dispatch on tails:
     - Both closed, no excess → success
     - Both closed, any excess → MissingFields error
     - A open, B closed → if onlyA non-empty, error; else solve tailA = RowType(onlyB, empty)
     - B open, A closed → symmetric
     - Both open → fresh r3; solve tailA = RowType(onlyB, r3); solve tailB = RowType(onlyA, r3)
  5. Occurs check before solving row metas
```

This is a single recursive function. The "Leijen adaptation" is that our rows are flat maps
with optional tails, not recursive `RowCons` constructors. The algorithm is equivalent but
operates on field-set differences rather than head/tail decomposition.

**Ref**: Leijen — *Extensible records with scoped labels* (2005). See references.md.

## TypeWalker Changes

### 5. `TypeWalker.java` — remove `resolveDeep`, kind-aware instantiation

**Remove `resolveDeep`**: Per the no-substitution principle (D-005), we do not rewrite type
trees with resolved metas. The zonker is a lookup table. Consumers that need a "fully
resolved" type for display (e.g., `CorePrinter`) resolve inline at point of use, calling
`state.zonk(type).orElse(type)` at each leaf.

The `generalize` method currently calls `resolveDeep` before collecting metas. After removal,
`collectMetas` should resolve metas inline (via zonk) to follow chains correctly. If a meta is
solved, follow the solution; if unsolved and at the right binding level, collect it.

**Kind-aware `collectMetas`**: returns `Map<Integer, Kind>` instead of `Set<Integer>`.

**Kind-aware `instantiate`**: reads the kind from `TypeScheme.quantified` to allocate the
correct flavor of fresh meta:

```java
static MonoType instantiate(TypeScheme scheme, int bindingLevel, ElaborationState state) {
    if (scheme.quantified().isEmpty()) return scheme.body();
    var freshening = new HashMap<Integer, MonoType>();
    for (var entry : scheme.quantified().entrySet()) {
        freshening.put(entry.getKey(), switch (entry.getValue()) {
            case TYPE -> state.freshType(bindingLevel);
            case ROW -> state.freshRow(bindingLevel);
        });
    }
    return walkType(scheme.body(), freshening);
}
```

`**walkType**`: must handle `MonoType.Rigid` — check substitution map, pass through if absent.

## ANTLR Grammar Changes

### 6. Lexer: split `IDENTIFIER` for type positions

The current `IDENTIFIER` rule matches both `foo` and `Foo`. For type annotations, we need to
distinguish them at the grammar level (D-033). Two approaches:

**Option A — Two lexer tokens**: `UPPER_IDENT` and `LOWER_IDENT` replace `IDENTIFIER`. All
parser rules using `IDENTIFIER` must choose which to use. This is clean but affects every
`IDENTIFIER` reference in the parser.

**Option B — Keep `IDENTIFIER`, split in type rules only**: The lexer keeps a single
`IDENTIFIER` token. The parser's `typePrimary` rule is duplicated:

```antlr
typePrimary
    : IDENTIFIER { /* runtime check: lowercase → TypeVar, uppercase → TypeCon */ }
    | ...
    ;
```

But this defeats the purpose — we want the grammar to enforce the convention.

**Decision**: Option A. Split the lexer:

```antlr
// In PiescriptLexer.g4:
UPPER_IDENT : [A-Z] (LETTER | DIGIT | '_')* ;
LOWER_IDENT : [a-z] (LETTER | DIGIT | '_')* ;

// Update IDENTIFIER references:
// - Expression positions (variable names, field names): accept UPPER_IDENT | LOWER_IDENT
// - Type constructor position: UPPER_IDENT only
// - Type variable position: LOWER_IDENT only
```

Parser changes:

- `typePrimary`: split `IDENTIFIER` into `UPPER_IDENT # TypeCon` and `LOWER_IDENT # TypeVar`
- `Variable` in `primary`: `(UPPER_IDENT | LOWER_IDENT) # Variable`
- `param`: `(UPPER_IDENT | LOWER_IDENT)` for variable names
- `recordField`, `recordUpdate`: `(UPPER_IDENT | LOWER_IDENT)` for field names
- `topBinding`, `LetExpr`, `BlockLet`: `(UPPER_IDENT | LOWER_IDENT)` for binding names
- `rowType` tail: `BAR LOWER_IDENT` (row variable is always lowercase)
- `Accessor`, `Projection`: `DOT (UPPER_IDENT | LOWER_IDENT)` for field names

Introduce a parser helper rule to avoid repetition:

```antlr
ident : UPPER_IDENT | LOWER_IDENT ;
```

Then most expression-level rules use `ident` while type rules use `UPPER_IDENT` or
`LOWER_IDENT` explicitly. Keywords that overlap with lowercase identifiers (let, fn, in,
etc.) remain separate tokens — ANTLR matches keywords before identifiers.

**Impact on existing parser tests**: All tests that use identifiers will need to be checked.
Most use lowercase variable names, which will now tokenize as `LOWER_IDENT` — the CST node
text is unchanged, only the token type differs. The elaborator's `getText()` calls are
unaffected.

## Elaborator Changes

### 7. `resolveTypeAnnotation` returns `TypeScheme`

**Current**: `resolveTypeAnnotation` returns `MonoType`, treating all identifiers in type
position as type constructors (looked up in `KNOWN_TYPES`).

**New behavior**:

```
resolveTypeAnnotation(typeCtx) → TypeScheme:
  1. Create empty rigidScope: Map<String, MonoType.Rigid>
  2. Walk the CST type, collecting LOWER_IDENT names as Rigids:
     - LOWER_IDENT "a" → rigidScope.computeIfAbsent("a", _ -> allocRigid(KIND.TYPE))
     - UPPER_IDENT "Integer" → lookup in KNOWN_TYPES, error if not found
     - Row tail LOWER_IDENT "r" → rigidScope.computeIfAbsent("r", _ -> allocRigid(Kind.ROW))
  3. Construct the MonoType body using Rigids in place of type variables
  4. If rigidScope is empty → return TypeScheme.mono(body)
  5. Otherwise → return new TypeScheme(rigidScope.toKindMap(), body)
```

The `allocRigid` function allocates a fresh Rigid ID (can reuse the metaSupply counter on
ElaborationState, or a separate counter — implementation detail).

### 8. Checking rule for universal types

When checking an expression against a `TypeScheme` (from an annotation):

```
checkAgainstScheme(expr, scheme, ctx):
  if scheme.quantified().isEmpty():
    // Monomorphic — standard check
    check(expr, scheme.body(), ctx)
  else:
    // Universal type — Rigids are already in the body (from annotation elaboration)
    // Just check the body against the mono type with Rigids in scope
    check(expr, scheme.body(), ctx)
```

Wait — the Rigids are *already* in the body of the TypeScheme from step 7 above. There's no
need to "peel" and "instantiate" — the body already contains `Rigid(0)`, `Rigid(1)`, etc.
We just check the expression against `scheme.body()` directly.

The key constraint is that **Rigids cannot be solved by unification**. If the body is
`fn x -> x` and the type is `Rigid(0) → Rigid(0)`, then `x : Rigid(0)` and the body `x`
has type `Rigid(0)`, which unifies with the return type `Rigid(0)`. No meta is involved.

If the body were `fn x -> 42`, the return type `Integer` would fail to unify with `Rigid(0)`
— correct! The annotation says "for all a", but the body doesn't work for all a.

### 9. Binding flow for annotated vs unannotated definitions

**Annotated**: `let f : a -> a = fn x -> x in body`

```
1. Parse annotation CST:     ArrowType(TypeVar("a"), TypeVar("a"))
2. Elaborate annotation:     TypeScheme({Rigid(0): TYPE}, Rigid(0) → Rigid(0))
3. Check body against it:    fn x -> x  checked against  Rigid(0) → Rigid(0)  ✓
4. Bind in env:              env[f] = TypeScheme({Rigid(0): TYPE}, Rigid(0) → Rigid(0))
                             (directly from annotation — no generalization)
5. Elaborate body:           uses of f instantiate: replace Rigid(0) with fresh Meta
```

**Unannotated**: `let f = fn x -> x in body`

```
1. No annotation
2. Elaborate body (infer):   fn x -> x  →  ?0 → ?0
3. Generalize:               unsolved ?0 at binding level → Rigid(0)
                             TypeScheme({Rigid(0): TYPE}, Rigid(0) → Rigid(0))
4. Bind in env:              env[f] = TypeScheme({Rigid(0): TYPE}, Rigid(0) → Rigid(0))
5. Elaborate body:           uses of f instantiate: replace Rigid(0) with fresh Meta
```

Both paths produce the same TypeScheme. The difference is *how* we arrive at it.

### 10. Accessor sugar with open rows (supersedes D-021)

```java
// Before (closed row):
var paramType = new MonoType.RecordType(RowType.closed(Map.of(label, resultType)));

// After (open row):
var rowTail = state.freshRow(ctx.bindingLevel());
var paramType = new MonoType.RecordType(RowType.open(Map.of(label, resultType), rowTail));
```

Same pattern for update sugar, projection, and record update.

### 11. Projection and update via unification (not direct lookup)

**Projection** (`elaborateProjection`):

```java
var fieldType = state.freshType(ctx.bindingLevel());
var rowTail = state.freshRow(ctx.bindingLevel());
var expected = new MonoType.RecordType(RowType.open(Map.of(label, fieldType), rowTail));
unifyOrThrow(exprType, expected, source);
return new CoreProject(source, expr, label, fieldType);
```

**Record update** (`elaborateUpdate`): similar — unify base with open row containing the
update fields.

## CorePrinter Changes

### 12. Inline zonk, handle Rigid

Remove dependency on `TypeWalker.resolveDeep`. When printing a type, resolve each meta
inline: `state.zonk(type).orElse(type)`. For `Rigid` variants, print as a type variable
name derived from the id (e.g., `a`, `b`, `c`, `r` for ROW kind).

## Tests

### UnifierTests — open-row unification

- Open row unifies with closed row (excess absorbed by tail)
- Open row unifies with larger closed row (multiple excess fields)
- Two open rows unify (fresh tail linking)
- Open row with excess fields vs closed row (error)
- Row meta chaining (r1 → r2 → RowType)
- Occurs check on row variables
- Empty open row `{ | r }` unifies with closed row
- Rigid vs Rigid (same id: OK, different id: error)
- Rigid vs Meta: error (Rigids cannot be solved)
- Rigid vs TCon: error

### ElaboratorTests — type variables, annotation elaboration, row polymorphism

- Accessor on record with extra fields: `{ x: 1, y: 2 } |> .x` types as Integer
- Update sugar on record with extra fields
- Row-polymorphic let binding: `let get = fn r -> r.x in get { x: 1, y: 2 }` succeeds
- Row-polymorphic function applied to different record shapes
- Type annotation with type variables: `let id : a -> a = fn x -> x`
- Type annotation with row variable: `let getMsg : { msg: a | r } -> a = fn rec -> rec.msg`
- Annotation mismatch: `let f : a -> a = fn x -> 42` — error (Integer ≠ Rigid)
- Row variable generalization: same polymorphic function applied to two different record types
- Error: accessing non-existent field on closed record literal

### EvaluatorTests — end-to-end row polymorphism

- Evaluate accessor on record with extra fields
- Evaluate row-polymorphic function
- Evaluate update sugar preserving extra fields

## Implementation Order

Dependencies flow top-down:

1. **MonoType.Rigid** (t1d-1) — leaf change, no dependencies
2. **TypeScheme kind map** (t1d-2) — depends on nothing new
3. **ANTLR split** (t1d-3) — grammar change, independent of type system
4. **ElaborationState zonk + resolveRow** (t1d-4) — depends on t1d-1
5. **Unifier rewrite** (t1d-5) — depends on t1d-1, t1d-4
6. **TypeWalker cleanup** (t1d-6) — depends on t1d-1, t1d-2, t1d-4
7. **Elaborator changes** (t1d-7) — depends on all above
8. **CorePrinter** (t1d-8) — depends on t1d-1, t1d-4
9. **Tests** (t1d-9) — depends on all above
10. **Living docs** (t1d-10) — after implementation

