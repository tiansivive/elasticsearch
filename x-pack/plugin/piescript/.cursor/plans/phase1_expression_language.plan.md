---
name: "Phase 1: Expression Language"
overview: "Pure functional expression language with bidirectional HM type inference and tree-walking interpreter. Vertical slice: let f = fn x -> x + 1 in f 42 → 43."
todos:
  - id: t1a.1
    content: "Update formal grammar in master plan"
    status: completed
  - id: t1a.2
    content: "Write ANTLR grammar (lexer + parser)"
    status: completed
  - id: t1a.3
    content: "Parser tests (PiescriptParserTests.java)"
    status: completed
  - id: t1b.1
    content: "Define type data structures (Kind, MonoType, RowType, TypeScheme, LitVal, Op)"
    status: completed
  - id: t1b.2
    content: "Define Core IR (CoreExpr node types extending Node)"
    status: completed
  - id: t1b.3
    content: "Implement elaboration state (context, metavar supply, binding level, zonker)"
    status: completed
  - id: t1b.4
    content: "Implement unification (Robinson, occurs check, null-as-bottom)"
    status: completed
  - id: t1b.5
    content: "Implement elaborator (bidirectional type checker + desugaring)"
    status: completed
  - id: t1b.6
    content: "Elaborator tests"
    status: completed
  - id: t1b.7
    content: "Wire elaborator into dev endpoint (CorePrinter, tree + core + type output)"
    status: completed
  - id: t1c.1
    content: "Define runtime value types (Eval, Value, Closure)"
    status: completed
  - id: t1c.2
    content: "Implement tree-walking interpreter"
    status: completed
  - id: t1c.3
    content: "Wire into transport action (dual dispatch: query passthrough + expression pipeline)"
    status: completed
  - id: t1c.4
    content: "PiescriptResponse (expression result + ESQL query wrapper)"
    status: completed
  - id: t1c.5
    content: "Integration and unit tests for interpreter + wiring (includes deferred elaborator tests: occurs check, cross-type arithmetic rejection, update sugar applied, lambda wrong-type app)"
    status: completed
  - id: t1d.1
    content: "Open-row unification in Unifier (Leijen-style, not Rémy — see D-030)"
    status: completed
  - id: t1d.2
    content: "Type variable convention in elaborator (D-028: lowercase = variable, uppercase = constructor)"
    status: completed
  - id: t1d.3
    content: "Update accessor/update sugar to use open rows (supersede D-021)"
    status: completed
  - id: t1d.4
    content: "Row-polymorphic function tests and updated CorePrinter"
    status: completed
isProject: false
---

# Phase 1: Expression Language

**Parent:** `scripting_language_design_9286506e.plan.md`
**Status:** Phase 1a–1c complete; Phase 1d (Open Rows & Row Polymorphism) is next. Pattern matching postponed to 1e (D-029).
**Dependencies:** Phase 0 (REST endpoint and plugin wiring)

## Goal

A pure functional expression language that parses, typechecks (with bidirectional HM inference), and evaluates via a tree-walking interpreter. No ES data, no streams, no queries, no branching. Proves the entire compiler pipeline works end-to-end.

## Vertical Slice

```bash
curl -X POST "localhost:9200/_piescript/eval" \
  -H "Content-Type: application/json" \
  -d '{ "program": "let f = fn x -> x + 1 in f 42" }'

# Returns: { "columns": [{ "name": "result", "type": "integer" }], "values": [[43]] }
```

Also:

```
"let greet = fn (name: Keyword) -> { greeting: \"hello\", who: name } in greet \"world\""
# Returns: { "columns": [{ "name": "greeting", "type": "keyword" }, { "name": "who", "type": "keyword" }], "values": [["hello", "world"]] }
```

## Sub-steps

Phase 1 is split into independently testable increments:


| Sub-step | Name                     | Status | Deliverable                                                                                          | Depends on |
| -------- | ------------------------ | ------ | ---------------------------------------------------------------------------------------------------- | ---------- |
| 1a       | Parser                   | :white_check_mark: | ANTLR grammar + CST; parse all v0 surface forms                                                      | Phase 0    |
| 1b       | Type system + Elaborator | :memo: | MonoType, CoreExpr, unification, bidirectional elaboration; given CST produce typed Core IR + zonker | 1a         |
| 1c       | Interpreter + Wiring     | :memo: | Eval/Value/Closure, tree-walker, transport action; the vertical slice works end-to-end via REST      | 1b         |
| 1d       | Pattern matching         | :memo: | Match node, if/then/else as sugar, boolean patterns — **separate sub-plan**                          | 1c         |


Each sub-step has its own tests. Sub-step 1c is the Phase 1 completion gate. Sub-step 1d is a separate plan that extends Phase 1 with branching.

---

## Terminology: Indices, Levels, and Binding Levels

Three distinct numbering concepts appear in this plan. They are easily confused:

1. **De Bruijn index** — used in the Core IR to reference variables. Counts *inward* from the variable occurrence to its binder. Index 0 = the immediately enclosing binder. `fn x -> fn y -> x` has `x` at index 1 (one binder between the occurrence and `x`'s `fn`). This is the standard de Bruijn representation.
2. **De Bruijn level** — an alternative variable representation found in PL literature. Counts *outward* from the root to the binder. Level 0 = the outermost binder. **We do NOT use de Bruijn levels for variables.** Mentioned only to prevent confusion.
3. **Binding level** — used in the elaborator for let-generalization. Tracks the nesting depth of `let`-bindings during elaboration. Each `let` increments the binding level; each meta records the binding level at which it was created. After elaborating a `let`-RHS, metas at the current binding level that remain unsolved in the zonker are generalized. This is orthogonal to de Bruijn indices — binding levels are an elaboration-time bookkeeping device, not a runtime representation.

Summary: de Bruijn *indices* appear in the Core IR. Binding *levels* appear in the elaborator. They serve different purposes and are never interchangeable.

---

## Resolved Design Decisions

### D1.1: Typing rules (bidirectional judgments)

**Status: Resolved.**

#### Legend

```
Γ          context: de Bruijn-indexed list of TypeScheme
e          term (surface or Core IR)
A, B       monotypes (MonoType)
σ          type scheme (TypeScheme)
α, β, ρ   metavars (Meta): α, β for types; ρ for rows
⇒          synthesis / inference: given Γ and e, produce A
⇐          checking: given Γ, e, and A, succeed or fail
~          unification constraint
Gen(Γ, A)  generalize A: quantify metas in A at current binding level, absent from zonker
σ ⊳ A      instantiation: if σ = ∀{α₁..αₙ}.B, allocate fresh metas, A = B[αᵢ ↦ freshᵢ]
Γ, A       extend context with a monomorphic binding (TypeScheme.mono(A))
Γ, σ       extend context with a polymorphic binding
```

#### Formal rules

**Var**

```
Γ(n) = σ       σ ⊳ A
───────────────────────
    Γ ⊢ Var(n) ⇒ A
```

**Lit**

```
─────────────────────         ─────────────────────
Γ ⊢ 42 ⇒ Integer             Γ ⊢ 3.14 ⇒ Double

─────────────────────         ─────────────────────
Γ ⊢ "s" ⇒ Keyword            Γ ⊢ true ⇒ Boolean

─────────────────────         ─────────────────────
Γ ⊢ false ⇒ Boolean          Γ ⊢ null ⇒ Null
```

Integer literals exceeding 32-bit range synthesize `Long`.

**Lam-Infer-Unann** (unannotated lambda, synthesis)

```
α fresh       Γ, α ⊢ e ⇒ B
─────────────────────────────
  Γ ⊢ fn x -> e ⇒ α → B
```

**Lam-Infer-Ann** (annotated lambda, synthesis)

```
    Γ, A ⊢ e ⇒ B
─────────────────────────────
Γ ⊢ fn (x: A) -> e ⇒ A → B
```

**Lam-Check** (lambda, checking)

```
  Γ, A ⊢ e ⇐ B
──────────────────────────
Γ ⊢ fn x -> e ⇐ A → B
```

If the lambda has annotation `A'`, also unify `A' ~ A`.

**App** (application)

```
Γ ⊢ e₁ ⇒ A → B       Γ ⊢ e₂ ⇐ A
────────────────────────────────────
       Γ ⊢ e₁ e₂ ⇒ B
```

If `e₁ ⇒ α` (unsolved meta), allocate `α₁, α₂` fresh, unify `α ~ α₁ → α₂`, check `e₂ ⇐ α₁`, result `α₂`.

**Let-Infer** (let binding, synthesis, with generalization)

```
bindingLevel++
Γ ⊢ e₁ ⇒ A       σ = Gen(Γ, A)
bindingLevel--
Γ, σ ⊢ e₂ ⇒ B
──────────────────────────────────
  Γ ⊢ let x = e₁ in e₂ ⇒ B
```

**Let-Check**

```
bindingLevel++
Γ ⊢ e₁ ⇒ A       σ = Gen(Γ, A)
bindingLevel--
Γ, σ ⊢ e₂ ⇐ B
──────────────────────────────────
  Γ ⊢ let x = e₁ in e₂ ⇐ B
```

**Let-Annot** (annotated let)

```
bindingLevel++
Γ ⊢ e₁ ⇐ A       σ = Gen(Γ, A)
bindingLevel--
Γ, σ ⊢ e₂ ⇒ B
──────────────────────────────────
  Γ ⊢ let x : A = e₁ in e₂ ⇒ B
```

**Record**

```
Γ ⊢ eᵢ ⇒ Aᵢ       for each i ∈ 1..n
──────────────────────────────────────────────────────
Γ ⊢ { l₁: e₁, ..., lₙ: eₙ } ⇒ { l₁: A₁, ..., lₙ: Aₙ }
```

Produces a closed row.

**Project**

```
Γ ⊢ e ⇒ A       α, ρ fresh       A ~ { l: α | ρ }
─────────────────────────────────────────────────────
                 Γ ⊢ e.l ⇒ α
```

**Update**

```
Γ ⊢ e ⇒ A       A ~ { l₁: β₁, ..., lₙ: βₙ | ρ }
Γ ⊢ eᵢ ⇒ Bᵢ       for each i
────────────────────────────────────────────────────────
Γ ⊢ { e | l₁ = e₁, ..., lₙ = eₙ } ⇒ { l₁: B₁, ..., lₙ: Bₙ | ρ }
```

**Ascription** (subsumption switch)

```
     Γ ⊢ e ⇐ A
──────────────────────
  Γ ⊢ (e : A) ⇒ A
```

**PrimOp** (ad-hoc, v0)

```
Γ ⊢ e₁ ⇒ A₁       Γ ⊢ e₂ ⇒ A₂       resolve(op, A₁, A₂) = B
────────────────────────────────────────────────────────────────
                   Γ ⊢ e₁ op e₂ ⇒ B
```

See D1.8 for the `resolve` table.

**Block**

```
Γ ⊢ s₁ ⊣ Γ₁       Γ₁ ⊢ s₂ ⊣ Γ₂       ...       Γₙ ⊢ e ⇒ A
────────────────────────────────────────────────────────────────
               Γ ⊢ { s₁; s₂; ...; e } ⇒ A
```

Where `Γ ⊢ let x = e ⊣ Γ'` extends Γ with the generalized binding.

#### Sub-decisions

- Unannotated lambdas: infer with fresh metavars (standard bidir HM).
- Generalization: at every `let` binding, binding-level-based (see D1.7). No value restriction — pure language, no mutable refs.
- Subsumption: at application sites and ascription. Standard.

### D1.2: Core IR with de Bruijn indices

**Status: Resolved.**

Variables use de Bruijn indices. Binders carry optional debug names for error messages and pretty-printing. Every node carries its `MonoType` (filled by elaboration). Metavars in types are resolved via the zonker — no separate "ground type."

**No `If` node in Phase 1.** Branching is deferred to Phase 1d (pattern matching). `if/then/else` will arrive as sugar for `match` — see separate sub-plan.

No `Query` or `Par` nodes — deferred to Phase 2+ and Phase 4+.

```
CoreExpr
  = Var(index: Int, debugName: String?, type: MonoType)
  | Lit(value: LitVal, type: MonoType)
  | Lam(debugName: String?, paramType: MonoType, body: CoreExpr, type: MonoType)
  | App(fn: CoreExpr, arg: CoreExpr, type: MonoType)
  | Let(debugName: String?, bindType: MonoType, rhs: CoreExpr, body: CoreExpr, type: MonoType)
  | Record(fields: List<(Label, CoreExpr)>, type: MonoType)
  | Project(expr: CoreExpr, label: Label, type: MonoType)
  | Update(expr: CoreExpr, fields: List<(Label, CoreExpr)>, type: MonoType)
  | PrimOp(op: Op, args: List<CoreExpr>, type: MonoType)
```

**Node infrastructure:** CoreExpr nodes extend `Node<CoreExpr>` from esql-core. This provides:
- `Source` on every node (error locations).
- `transformDown`/`transformUp`/`forEachDown` for tree-to-tree rewrite passes (optimizer, Phase 2+).
- Structural contract (`info()`, `replaceChildren()`) for ESQL interop.

The sealed-interface definitions in this document serve as the design spec; the implementation uses classes extending Node.

**Where `Node<T>` is and isn't used:**
- The **elaborator** (T1b.5) does not use `Node<T>` traversal — it is hand-written recursive descent over ANTLR parse trees, producing `CoreExpr` as output.
- The **evaluator** (T1c.2) does not use `Node<T>` traversal — it is hand-written recursive descent over `CoreExpr` subtypes, producing runtime values as output.
- The **optimizer** (Phase 2+) will use `Node<T>` traversal — `transformDown`/`transformUp` and potentially ESQL's `RuleExecutor` for rewrite-rule-based optimization passes on Core IR and the plan graph.

> **Possible future direction:** If `Node<T>`'s baggage (NamedWriteable, ordered children list) proves too costly for the optimizer, we may investigate replacing `transformDown`/`transformUp` with pattern-matching-based recursive descent over sealed classes. This would be the stepping stone to dropping `Node<T>` for Core IR entirely. However, `Node<T>` is important for ESQL interop, so any such change must be evaluated carefully. Not planned — revisit when the optimizer is being built.

**Types, values, and other data structures do NOT extend Node.** They use records and sealed interfaces directly. Rationale:

- `MonoType`: simple, Map-based rows don't fit Node's ordered children model.
- `Value`/`Eval`: produced by the interpreter, never tree-transformed.
- `LitVal`, `TypeScheme`, `RowType`: pure data, no traversal needed.

### D1.3: Literals aligned with ES DataType

**Status: Resolved.**


| Surface syntax   | DataType  | Java runtime | Notes                               |
| ---------------- | --------- | ------------ | ----------------------------------- |
| `42`             | `INTEGER` | `int`        | 32-bit; auto-Long if overflows      |
| `9999999999`     | `LONG`    | `long`       | Integer overflow → Long             |
| `3.14`           | `DOUBLE`  | `double`     | 64-bit float                        |
| `"hello"`        | `KEYWORD` | `BytesRef`   | Matches `Literal.keyword()` in ESQL |
| `true` / `false` | `BOOLEAN` | `boolean`    |                                     |
| `null`           | `NULL`    | `null`       |                                     |


Grammar addition:

```
e ::= ...
    | n                       -- integer literal (Integer; Long if overflows 32-bit)
    | n.m                     -- floating-point literal (Double)
    | "s"                     -- string literal (Keyword)
    | true | false            -- boolean literals (Boolean)
    | null                    -- null literal (Null)
```

### D1.4: Branching cut from Phase 1

**Status: Resolved.**

No `if/then/else` and no `match` in Phase 1. The vertical slice doesn't require branching. When pattern matching is added (Phase 1d, separate sub-plan), `if c then a else b` arrives as sugar for `match c | true -> a | false -> b`, giving us a single branching mechanism.

Phase 1d sub-plan scope: `Match` Core IR node, boolean literal patterns (v0), if/then/else desugaring, exhaustiveness checking (at minimum for boolean).

### D1.5: Query type for v0

**Status: Resolved.**

`query` produces `Stream (Record ρ)` directly. No `Process` wrapper until Phase 4. Not implemented in Phase 1 — deferred to Phase 2.

**Master plan update needed:** Section 6.3 says `query` produces `Process (Stream { ... })`. Needs correction.

### D1.6: Result serialization

**Status: Resolved.**

Everything is a columnar table. A scalar `43` is a 1x1 table. A record is a 1-row table with fields as columns. Top-level Closure result is a type error — "cannot serialize function value."

### D1.7: Elaboration state and generalization (zonker-based, no substitution)

**Status: Resolved.**

**Core principle: the elaborator never substitutes into terms.** Unification writes solutions to the zonker. The rest of the pipeline carries the zonker and resolves metavars by lookup.

**Elaboration carries two parameters through recursive descent:**

1. **`ElaborationContext` (immutable)** — passed by value; the call stack handles scoping.
   - **Context Γ** — de Bruijn-indexed list of named type schemes. Index 0 = most recent binding. `ctx.bind(name, scheme)` returns a new context.
   - **Binding level** — current let-nesting depth. `ctx.enterBindingLevel()` / `ctx.exitBindingLevel()` return new contexts. Each meta records the binding level at which it was created.
   - **Name-to-index resolution** — `ctx.lookup(name)` returns `Optional<LookupResult>` with de Bruijn index + scheme.

2. **`ElaborationState` (mutable)** — shared across the entire pass; genuinely requires global mutation.
   - **Metavar supply** — monotonic counter. `state.freshType(ctx.bindingLevel())` / `state.freshRow(ctx.bindingLevel())` return `Meta(id, bindingLevel, kind)`.
   - **Zonker** — `Map<Integer, Object>` mapping metavar IDs to solutions. The meta's `kind` determines whether the solution is `MonoType` (for kind=Type) or `RowType` (for kind=Row). `state.resolve(id)` returns `Optional<Object>`.

**Generalization algorithm (standard HM with binding levels):**

```
elaborate(Let(x, rhs, body), ctx, state):
    letCtx = ctx.enterBindingLevel()                       // new ctx with bindingLevel++

    rhsType = elaborate(rhs, letCtx, state)                // unification runs eagerly inline

    // Generalize: unsolved metas at current binding level
    resolved = resolve(rhsType, state.zonker)
    generalizable = collectMetas(resolved)
                        .filter(m -> m.bindingLevel >= letCtx.bindingLevel)
                        .filter(m -> !state.isSolved(m.id))
    scheme = TypeScheme(generalizable.ids(), resolved)

    bodyCtx = ctx.bind(x, scheme)                          // new ctx with binding; level restored
    bodyType = elaborate(body, bodyCtx, state)

    return bodyType
```

**Instantiation at use sites:**

```
elaborate(Var(name), ctx, state):
    (index, scheme) = ctx.lookup(name).orElseThrow(...)    // name → de Bruijn index + scheme
    freshening = {}
    for id in scheme.quantified:
        freshening[id] = state.freshType(ctx.bindingLevel())
    instantiated = walkType(scheme.body, freshening)
    return instantiated
```

The `walkType` in instantiation is the one type-level substitution — proportional to the type size, not the program size.

```
let id = fn x -> x in       -- id : ∀α. α → α
let v = id 1 in              -- instantiate: fresh β, id : β → β, unify β ~ Integer
let s = id "hello" in        -- instantiate: fresh γ, id : γ → γ, unify γ ~ Keyword
```

**No zonking pass.** The zonker is carried as a lookup table throughout the pipeline.

### D1.8: Primop resolution (ad-hoc, v0)

**Status: Resolved.**


| op                  | A₁      | A₂      | Result                                                  |
| ------------------- | ------- | ------- | ------------------------------------------------------- |
| `+`,`-`,`*`,`/`,`%` | Integer | Integer | Integer                                                 |
| `+`,`-`,`*`,`/`,`%` | Long    | Long    | Long                                                    |
| `+`,`-`,`*`,`/`,`%` | Double  | Double  | Double                                                  |
| `==`,`!=`           | A       | A       | Boolean (A ∈ {Integer, Long, Double, Keyword, Boolean}) |
| `<`,`>`,`<=`,`>=`   | A       | A       | Boolean (A ∈ {Integer, Long, Double, Keyword})          |
| `&&`,`||`           | Boolean | Boolean | Boolean                                                 |
| `!` (unary)         | Boolean | —       | Boolean                                                 |
| `-` (unary, `NEG`)  | Numeric | —       | same type as operand                                    |


Numeric widening: `Integer + Long` → widen Integer to Long, result Long. `Integer + Double` → widen to Double. Mirrors ESQL's `EsqlDataTypeConverter.commonType`.

### D1.9: Types representation (single MonoType, kinded metas, flat rows)

**Status: Resolved.**

```
MonoType
  = TCon(name: String)                             -- "Integer", "Keyword", ...
  | Arrow(param: MonoType, result: MonoType)
  | RecordType(row: RowType)
  | AppType(constructor: MonoType, argument: MonoType)
  | Meta(id: Int, bindingLevel: Int, kind: Kind)   -- unsolved metavar

Kind = TYPE | ROW

RowType(
  fields: Map<String, MonoType>,                   -- known fields (commutative)
  rowVar: Optional<Meta>                           -- open tail (kind=ROW)
)

TypeScheme(quantified: Set<Integer>, body: MonoType)
```

**Kinded metavars:** Each `Meta` carries a `Kind` (TYPE or ROW) and a `bindingLevel` (the let-nesting depth at allocation time — see Terminology section). When generating a fresh meta, the caller specifies the kind; the binding level is recorded automatically from the elaboration state. The zonker is a single `Map<Integer, Object>` — the meta's kind determines whether the solution is `MonoType` or `RowType`. This trivializes the distinction between type and row metas without needing separate supplies or separate zonker maps.

**Flat rows:** `RowType` uses `Map<String, MonoType>` instead of recursive `Empty | Extend(label, type, tail)`. Rows are commutative — a Map captures this naturally. Direct translation from Rémy / Daan Leijen, simplified (no duplicate labels). Row unification algorithm deferred to Phase 2 since Phase 1 only has closed records.

### D1.10: Values and closures (interpreter)

**Status: Resolved.**

```
Eval (sealed)
  ├── Value (sealed)                           -- observable, serializable data
  │   ├── IntegerVal(int)
  │   ├── LongVal(long)
  │   ├── DoubleVal(double)
  │   ├── KeywordVal(BytesRef)
  │   ├── BooleanVal(boolean)
  │   ├── NullVal()
  │   └── RecordVal(Map<String, Eval>)         -- fields can hold closures
  └── Closure(env: List<Eval>, body: CoreExpr) -- WHNF: env + unevaluated body
```

- `eval(Lam(...), env)` → `Closure(env, body)`
- `apply(Closure(cEnv, body), arg)` → `eval(body, arg :: cEnv)`
- `eval(Var(n, ...), env)` → `env.get(n)`

### D1.11: Null semantics

**Status: Resolved.**

**v0:** `Null` is typeless — unifies with any type. `null` is a valid value of any type. Implementation: unification treats `Null` as a bottom type that unifies with everything (when unifying `Null ~ A`, succeed immediately without constraining A).

This avoids the need for `Option`/`Maybe` types in v0 and matches ESQL semantics (any column can be null).

> **Post-v0 note:** Consider moving `Null` toward a Unit-like type where nullability is explicit. This would require `Option a` or similar. Document this as a future type-system refinement.

### D1.12: Shadowing

**Status: Resolved.** Allowed. De Bruijn indices make shadowing trivially correct — inner binding is index 0, outer gets pushed deeper. No extra checking needed.

### D1.13: Block `return` keyword

**Status: Resolved.** Dropped from v0. A block's value is its last expression. No early exit.

> **Future enhancement:** If `return` is ever needed, add alongside non-local control flow (continuations or similar). Not expected to be needed in a pure expression language.

### D1.14: Error strategy

**Status: Resolved.** Fail-fast. The elaborator stops at the first type error.

> **Future enhancement:** Error accumulation (report all errors at once) for better UX.

### D1.15: Unification algorithm

**Status: Resolved.**

Robinson unification over `TCon`, `Arrow`, `RecordType` (closed only in Phase 1), `AppType`, and `Meta`:

1. `unify(Meta(id, ..), B)` → if `id` is in the zonker, `unify(zonker[id], B)`. Otherwise, occurs check (is `id` free in B?), then `zonker[id] = B`.
2. `unify(A, Meta(id, ..))` → symmetric.
3. `unify(TCon(a), TCon(b))` → succeed iff `a == b`.
4. `unify(Arrow(a1, a2), Arrow(b1, b2))` → `unify(a1, b1); unify(a2, b2)`.
5. `unify(RecordType(r1), RecordType(r2))` → field-by-field (Phase 1: closed rows only, so fields must match exactly; Phase 2 adds open-row unification via Rémy).
6. `unify(Null, A)` or `unify(A, Null)` → succeed (Null is bottom in v0).
7. Otherwise → type error.

**Occurs check:** Prevents infinite types. `unify(α, α → Integer)` is an error. Standard.

**Zonker chain resolution:** When looking up a meta in the zonker, follow chains: if `zonker[α] = β` and `zonker[β] = Integer`, resolve to Integer. This is a simple recursive lookup, not a separate pass.

### D1.16: Source locations

**Status: Noted for awareness.**

ANTLR tokens carry location info (line, column). ESQL's `Source` class (from esql-core) bundles this. Core IR nodes inherit `Source` from `Node<CoreExpr>`.

For Phase 1, source locations should be:

- Propagated from ANTLR tokens during elaboration
- Attached to Core IR nodes (comes free from Node's constructor)
- Included in error messages (type errors, parse errors)

This is NOT a full provenance system (tracking data lineage through transformations). Just lexer token locations for human-readable errors. A provenance system is a future concern.

### D1.17: Surface Grammar

**Status: Resolved.**

Complete grammar specification for Phase 1a (Parser). Design principle: **when in doubt, follow Haskell**.

#### Comments

```
// line comment
/* multi-line comment */
```

Same as ESQL/Java for familiarity.

#### String escapes

Use the same escape sequences as ESQL: `\t`, `\n`, `\r`, `\"`, `\\`.

#### Literals

```
42              Integer (32-bit; Long if overflows)
9999999999      Long (automatic promotion)
3.14            Double
"hello"         Keyword (ESQL string escapes)
true / false    Boolean
null            Null
```

No signed literals. `-1` is `UnaryMinus(IntegerLiteral(1))`, constant-folded by the elaborator.

#### Program structure

```
program ::= topBinding* expr

topBinding ::= 'let' IDENT (':' type)? '=' expr ';'
```

A program is zero or more top-level `let` bindings (semicolon-terminated) followed by a final expression (the result). `let ... in ...` remains available for local/inline bindings within the body.

> **Master plan update needed:** add top-level binding syntax to Section 13.3.

#### Operator precedence (low to high)


| Prec | Operator(s)       | Fixity     | Notes                                  |
| ---- | ----------------- | ---------- | -------------------------------------- |
| 1    | `|>`              | left-assoc | pipe — desugars to flipped application |
| 2    | `||`              | left-assoc | logical or                             |
| 3    | `&&`              | left-assoc | logical and                            |
| 4    | `==` `!=`         | non-assoc  | equality                               |
| 5    | `<` `>` `<=` `>=` | non-assoc  | comparison                             |
| 6    | `+` `-`           | left-assoc | additive                               |
| 7    | `*` `/` `%`       | left-assoc | multiplicative                         |
| 8    | `!` `-` (prefix)  | unary      | not, negate — tighter than infix       |
| 9    | application       | left-assoc | juxtaposition — tightest               |


Postfix `.field` (projection) binds tightest of all — part of primary expressions.

#### Expression grammar

```
expr ::= 'let' IDENT (':' type)? '=' expr 'in' expr     -- local binding
       | 'fn' param+ '->' expr                           -- lambda
       | pipe_expr

pipe_expr  ::= pipe_expr '|>' or_expr   | or_expr
or_expr    ::= or_expr '||' and_expr    | and_expr
and_expr   ::= and_expr '&&' eq_expr    | eq_expr
eq_expr    ::= cmp_expr ('==' | '!=') cmp_expr  | cmp_expr
cmp_expr   ::= add_expr ('<' | '>' | '<=' | '>=') add_expr  | add_expr
add_expr   ::= add_expr ('+' | '-') mul_expr    | mul_expr
mul_expr   ::= mul_expr ('*' | '/' | '%') unary_expr  | unary_expr
unary_expr ::= ('!' | '-') unary_expr   | app_expr

app_expr   ::= app_expr primary                          -- left-assoc juxtaposition
             | primary

primary ::= primary '.' IDENT                            -- field projection
          | '.' IDENT                                     -- accessor sugar → fn x -> x.IDENT
          | INTEGER_LITERAL | DECIMAL_LITERAL
          | QUOTED_STRING
          | 'true' | 'false' | 'null'
          | IDENT                                         -- variable
          | '(' expr ')'                                  -- parenthesized
          | '(' expr ':' type ')'                         -- type ascription (requires parens)
          | '{' field (',' field)* '}'                     -- record literal
          | '{' expr '|' update (',' update)* '}'         -- record update
          | '{' '_' '|' update (',' update)* '}'          -- update sugar
          | 'if' expr 'then' expr 'else' expr             -- parsed for forward-compat (elaboration deferred to 1d)
          | block

field  ::= IDENT ':' expr
update ::= IDENT '=' expr

block  ::= '{' stmt* expr '}'
stmt   ::= 'let' IDENT (':' type)? '=' expr ';'
         | expr ';'
```

#### Lambda parameters

```
param ::= IDENT                      -- unannotated: fn x y -> ...
        | '(' IDENT ':' type ')'     -- annotated:   fn (x: Integer) (y: Integer) -> ...
```

Annotated and unannotated can be mixed: `fn x (y: Integer) -> x + y`. Each annotated parameter must be individually parenthesized.

> **Future work:** `(x: Integer, y: Integer) -> Integer` multi-param tuple-style syntax. Deferred because it conflicts with tuples and row type syntax.

#### Application

Left-associative curried application via juxtaposition. `f x y` is `(f x) y`. Application binds tighter than all infix operators: `f x + 1` is `(f x) + 1`.

> **Future work:** Application spines (`f(x, y)` as a single multi-argument call) may be added for familiarity. Surface syntax extension that desugars to curried application. Not needed for correctness — purely a UX consideration.

#### Type ascription

Type ascription `(e : T)` always requires parentheses to avoid ambiguity with record field syntax `{ x : e }`. In expression position, bare `e : T` is not valid.

#### Type syntax

Type annotations appear only in specific contexts: after `:` in `let x: T = ...`, `fn (x: T) -> ...`, and `(e : T)`.

```
type ::= type_primary '->' type        -- function type (right-assoc)
       | type_primary

type_primary ::= IDENT                 -- TCon: Integer, Keyword, Boolean, Long, Double, Null, ...
               | '{' row_type '}'      -- record type: { name: Keyword, age: Integer }
               | '(' type ')'         -- parenthesized

row_type ::= field_type (',' field_type)* ('|' IDENT)?
field_type ::= IDENT ':' type
```

`->` in types does not conflict with `->` in lambdas — the parser knows from context whether it is inside a type annotation or a term.

#### Pipe operator

`e1 |> e2` desugars to `e2 e1`. Lowest-precedence binary operator so the entire left-hand side is captured:

```
x + 1 |> f       →  f (x + 1)
xs |> map .name   →  map .name xs    →  map (fn x -> x.name) xs
```

#### Desugaring summary (elaboration-time)


| Surface         | Desugars to             |
| --------------- | ----------------------- |
| `fn x y -> e`   | `fn x -> fn y -> e`     |
| `e1 |> e2`      | `e2 e1`                 |
| `.field`        | `fn x -> x.field`       |
| `{ _ | f = e }` | `fn x -> { x | f = e }` |
| `-e` (unary)    | `PrimOp(NEG, [e])`      |
| `!e`            | `PrimOp(NOT, [e])`      |


#### Record disambiguation

`{` can start four things. Disambiguation strategy:

1. **Record literal:** `{ IDENT ':' ...` — identifier followed by `:`
2. **Record update:** `{ expr '|' ...` — expression followed by `|`
3. **Update sugar:** `{ '_' '|' ...` — underscore followed by `|`
4. **Block:** `{ 'let' ...` or `{ expr ';' ...` — starts with `let` keyword, or expression followed by `;`

In practice: if the first token after `{` is an identifier and the second is `:`, it is a record. If the first token is `_` and the second is `|`, it is update sugar. Otherwise parse as expression — if `|` follows it is a record update, if `;` follows it is a block.

> **Fallback:** If ANTLR conflicts prove unmanageable, fall back to `do { ... }` for blocks.

#### Reserved words

Reserved for Phase 1 and forward-compatibility:

```
let  in  fn  if  then  else  match  true  false  null  query  par  do
```

`match`, `query`, `par`, `do` are not used in Phase 1 but reserved to prevent future breakage.

#### Lexer tokens (new, beyond ESQL)

```
FN           : 'fn'
LET          : 'let'
IN           : 'in'
IF           : 'if'
THEN         : 'then'
ELSE         : 'else'
ARROW        : '->'
PIPE         : '|>'
UNDERSCORE   : '_'
AND_OP       : '&&'
OR_OP        : '||'
BANG         : '!'
```

Plus all tokens ESQL already defines (arithmetic ops, comparison ops, braces, parens, dot, comma, semicolon, colon, literals, identifiers).

---

## Discrepancies with Master Plan (to be reconciled)

The master plan (`scripting_language_design_9286506e.plan.md`) has several items that are now outdated or inconsistent with Phase 1 decisions. These should be updated when the master plan is next revised:

1. **Section 3.2** — type names `Int`, `Bool` → `Integer`, `Boolean` (match ESQL DataType enum).
2. **Section 3.6** — row types described as recursive sequences → implementation uses flat `Map<String, MonoType> + Optional<Meta>`. The formal grammar can retain the mathematical notation; add an implementation note.
3. **Section 6.3** — `query` produces `Process (Stream { ... })` → should be `Stream (Record ρ)` for v0. `Process` deferred to Phase 4.
4. **Section 13.3** — missing literal productions (null, integer-overflow-to-Long), `if/then/else` production, top-level binding syntax (`topBinding* expr`), boolean operators (`&&`, `||`), pipe operator (`|>`), unary operators (`!`, `-`), and complete precedence table.
5. **Section 13.4** — should note that elaboration output uses de Bruijn indices and the zonker (no term-level substitution).
6. **Section 10.2** — should note de Bruijn indices for Core IR variable representation.

---

## Tasks by Sub-step

### Phase 1a: Parser :white_check_mark:

**T1a.1: Update formal grammar in master plan** :white_check_mark:

Updated master plan Section 13.3 with literal productions, `if/then/else`, top-level binding syntax,
boolean operators, pipe operator, unary operators.

**T1a.2: Write ANTLR grammar** :white_check_mark:

Implemented the full surface grammar specified in D1.17:

- `PiescriptLexer.g4` — tokens for all keywords, operators, literals, identifiers, comments
- `PiescriptAntlrParser.g4` — full expression grammar with precedence tower, lambdas, let-bindings,
  records, projections, updates, if/then/else, blocks, pipes, type annotations
- Generated Java sources via `regenLexer`/`regenParser` Gradle tasks
- Also added `RestPiescriptDevAction` (`POST /_piescript/dev`) for CST inspection during development

**T1a.3: Parser tests** :white_check_mark:

`PiescriptParserTests.java` — comprehensive unit tests covering: literals (int, decimal, exponent,
string, escapes, boolean, null), variables, arithmetic, comparison, boolean operators, precedence,
pipe, let-in, top-level bindings, lambdas, application, records, projections, ascription,
if/then/else, blocks, types, comments, and error cases (empty input, missing operand, missing let
body, unclosed paren/string, missing lambda arrow).

**Deliverable:** Given a program string, produce an ANTLR parse tree. :white_check_mark:

### Phase 1b: Type System + Elaborator

**T1b.1: Define type data structures**

Records and sealed interfaces (NOT Node):

- `Kind` enum (TYPE, ROW)
- `MonoType` sealed interface (TCon, Arrow, RecordType, AppType, Meta)
- `RowType` record (Map + Optional)
- `TypeScheme` record (Set + MonoType)
- `LitVal` sealed interface
- `Op` enum

**T1b.2: Define Core IR**

Classes extending `Node<CoreExpr>`:

- Var, Lit, Lam, App, Let, Record, Project, Update, PrimOp
- Each carries `MonoType` and `Source` (from Node)
- Add sealed-interface spec as doc comments for design context

**T1b.3: Implement elaboration state**

- Context (de Bruijn deque of named TypeSchemes)
- Name-to-index resolution
- Metavar supply (with kinded allocation)
- Binding level counter
- Zonker (`Map<Integer, Object>`)

**T1b.4: Implement unification**

Robinson unification with occurs check. Null-as-bottom rule. Closed-row field-by-field matching. Zonker chain resolution.

**T1b.5: Implement elaborator**

Bidirectional type checker implementing D1.1 rules:

- Inference and checking modes
- Binding-level-based let-generalization
- Instantiation at use sites
- Ad-hoc primop resolution
- Desugaring: multi-param fn → nested Lam, pipe → flipped App, `.field` → Lam+Project, `{ _ | ... }` → Lam+Update, unary `-` → `PrimOp(NEG, ...)`, unary `!` → `PrimOp(NOT, ...)`

Output: Core IR (with MonoType on every node) + zonker.

**T1b.6: Elaborator tests**

- Type inference for each term form
- Let-polymorphism (`let id = fn x -> x in id 1; id "hello"`)
- Generalization correctness (polymorphic lets, monomorphic lambdas)
- Type errors: clear messages with source locations
- Null unification
- Numeric widening

**T1b.7: Wire elaborator into dev endpoint**

- `CorePrinter` utility: pretty-prints Core IR as S-expressions and types as readable strings.
- `RestPiescriptDevAction` now runs the full parse → elaborate pipeline and returns `tree` (CST),
  `core` (pretty-printed Core IR), and `type` (resolved top-level type). Parse errors return
  `parse_error`; type errors return `tree` + `type_error`.

**Deliverable:** Given a parse tree, produce typed Core IR + zonker, or a type error with location.

### Phase 1c: Interpreter + Wiring

**T1c.1: Define runtime value types**

Records and sealed interfaces (NOT Node):

- `Eval` sealed interface
- `Value` sealed sub-interface (IntegerVal, LongVal, DoubleVal, KeywordVal, BooleanVal, NullVal, RecordVal)
- `Closure` record (env + body)

**T1c.2: Implement tree-walking interpreter**

Hand-written recursive descent over `CoreExpr` subtypes (not `Node<T>` transforms — see D1.2).
Immutable de Bruijn environment: `eval(expr, env)` where `env` is an immutable `List<Eval>`;
entering a binder prepends a value to produce a new env for the recursive call. Closures capture
the env at definition time. Same immutable-context pattern as the elaborator.

**T1c.3: Wire into transport action**

Replace Phase 0 hack: parse → elaborate → interpret → serialize. Top-level Closure is a type error.

**T1c.4: Serialization**

Value → columnar response. Scalar = 1x1 table. Record = 1-row table.

**T1c.5: Tests**

- Unit tests: evaluation of each Core IR node type
- Integration test: REST endpoint evaluates `let f = fn x -> x + 1 in f 42`, returns columnar 43
- Error tests: type error and parse error via REST produce clear JSON errors
- Polymorphism integration: `let id = fn x -> x in let a = id 42 in let b = id "hello" in { a_val: a, b_val: b }`
- Deferred elaborator tests (from T1b.6 review):
  - Occurs check: `fn x -> x x` must fail with infinite type error
  - Cross-type arithmetic rejection: `"hello" + 1`, `3.14 + 1` must fail
  - Lambda applied to wrong type: `(fn (x : Integer) -> x) true` must fail
  - Update sugar applied: `{ x: 1 } |> { _ | x = 2 }` type resolves correctly

**Deliverable:** The vertical slice works end-to-end.

### Phase 1d: Pattern Matching (separate sub-plan)

Scope: `Match` Core IR node, `if c then a else b` → `match c | true -> a | false -> b` sugar, boolean literal patterns, exhaustiveness checking (boolean case). Extends the vertical slice with branching. See separate plan file.

---

## Java Type Definitions (Reference)

Production: CoreExpr uses Node classes. Everything else uses records/sealed interfaces. These definitions are the design spec — add as doc comments on the Node classes.

```java
// === Kinds ===

public enum Kind { TYPE, ROW }

// === Types (records/sealed interfaces — NOT Node) ===

public sealed interface MonoType {
    record TCon(String name) implements MonoType {}
    record Arrow(MonoType param, MonoType result) implements MonoType {}
    record RecordType(RowType row) implements MonoType {}
    record AppType(MonoType constructor, MonoType argument) implements MonoType {}
    record Meta(int id, int bindingLevel, Kind kind) implements MonoType {}
}

public record RowType(
    Map<String, MonoType> fields,
    Optional<MonoType.Meta> rowVar
) {
    public static RowType closed(Map<String, MonoType> fields) {
        return new RowType(fields, Optional.empty());
    }
    public static RowType open(Map<String, MonoType> fields, MonoType.Meta var) {
        return new RowType(fields, Optional.of(var));
    }
}

public record TypeScheme(Set<Integer> quantified, MonoType body) {
    public static TypeScheme mono(MonoType type) {
        return new TypeScheme(Set.of(), type);
    }
}

// === Literals (records — NOT Node) ===

public sealed interface LitVal {
    record IntegerLit(int value) implements LitVal {}
    record LongLit(long value) implements LitVal {}
    record DoubleLit(double value) implements LitVal {}
    record KeywordLit(BytesRef value) implements LitVal {}
    record BooleanLit(boolean value) implements LitVal {}
    record NullLit() implements LitVal {}
}

// === Core IR (extends Node<CoreExpr> in production — see D1.2) ===
// Spec as sealed interface for design clarity:

public sealed interface CoreExpr {
    MonoType type();

    record Var(int index, String debugName, MonoType type) implements CoreExpr {}
    record Lit(LitVal value, MonoType type) implements CoreExpr {}
    record Lam(String debugName, MonoType paramType, CoreExpr body, MonoType type) implements CoreExpr {}
    record App(CoreExpr fn, CoreExpr arg, MonoType type) implements CoreExpr {}
    record Let(String debugName, MonoType bindType, CoreExpr rhs, CoreExpr body, MonoType type) implements CoreExpr {}
    record Record(List<Field> fields, MonoType type) implements CoreExpr {}
    record Project(CoreExpr expr, String label, MonoType type) implements CoreExpr {}
    record Update(CoreExpr expr, List<Field> updates, MonoType type) implements CoreExpr {}
    record PrimOp(Op op, List<CoreExpr> args, MonoType type) implements CoreExpr {}

    record Field(String label, CoreExpr value) {}
}

public enum Op {
    ADD, SUB, MUL, DIV, MOD,
    EQ, NEQ, LT, GT, LTE, GTE,
    AND, OR, NOT, NEG
}

// === Runtime values (records/sealed interfaces — NOT Node) ===

public sealed interface Eval {}

public sealed interface Value extends Eval {
    record IntegerVal(int value) implements Value {}
    record LongVal(long value) implements Value {}
    record DoubleVal(double value) implements Value {}
    record KeywordVal(BytesRef value) implements Value {}
    record BooleanVal(boolean value) implements Value {}
    record NullVal() implements Value {}
    record RecordVal(Map<String, Eval> fields) implements Value {}
}

public record Closure(List<Eval> env, CoreExpr body) implements Eval {}

// === Elaboration (immutable context + mutable state) ===

// Immutable: passed by value through recursive descent. Call stack handles scoping.
public final class ElaborationContext {
    static final ElaborationContext EMPTY = new ElaborationContext(List.of(), 0);
    private final List<NamedScheme> bindings;   // de Bruijn: index 0 = most recent
    private final int bindingLevel;             // let-nesting depth

    ElaborationContext bind(String name, TypeScheme scheme) { /* returns new ctx */ }
    Optional<LookupResult> lookup(String name) { /* scans from head */ }
    ElaborationContext enterBindingLevel() { /* returns new ctx with level+1 */ }
    ElaborationContext exitBindingLevel()  { /* returns new ctx with level-1 */ }

    record NamedScheme(String name, TypeScheme scheme) {}
    record LookupResult(int index, TypeScheme scheme) {}
}

// Mutable: shared globally. Only metavar supply + zonker.
public final class ElaborationState {
    private int metaSupply;                         // fresh metavar counter
    private final Map<Integer, Object> zonker;      // meta ID → MonoType or RowType (by kind)

    public MonoType.Meta freshType(int bindingLevel) {
        return new MonoType.Meta(metaSupply++, bindingLevel, Kind.TYPE);
    }
    public MonoType.Meta freshRow(int bindingLevel) {
        return new MonoType.Meta(metaSupply++, bindingLevel, Kind.ROW);
    }
    public Optional<Object> resolve(int metaId) { /* chain-following lookup */ }
}
```

---

## Scope Notes

**In scope for Phase 1 (sub-steps 1a–1c):**

- ANTLR grammar per D1.17 (no query, no par, no match — if/then/else parsed for forward compat but not elaborated)
- Elaborator with bidirectional HM, binding-level-based generalization, zonker
- Core IR with de Bruijn indices (Node classes + sealed-interface spec as comments)
- Tree-walking interpreter
- REST integration (Phase 0 wiring → real pipeline)
- Unification over simple types (TCon, Arrow, closed RecordType)
- Source locations on Core IR nodes and in error messages

**Explicitly out of scope:**

- Branching / if / match (Phase 1d, separate sub-plan)
- Row polymorphism / open rows (Phase 2)
- Query execution (Phase 2)
- Stream runtime (Phase 3)
- Process primitives / par (Phase 4)
- Optimizer (Phase 2+)
- Type normalization (no type-level computation in v0)
- Error accumulation (future UX improvement)
- `return` keyword / non-local control flow (future)
- Full provenance system (future — v0 has token locations only)

## Estimated Scope

Medium. Without branching, the scope is tighter: ANTLR grammar, elaborator, and interpreter, each substantial but focused. The elaborator is the hardest — HM with bidirectional checking, binding-level-based generalization, and the zonker. Expect ~20-35 Java files across parser, elaborator, Core IR, interpreter, and tests per sub-step.