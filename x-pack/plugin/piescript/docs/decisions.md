# Architectural Decisions

> **Living doc** — add a new entry whenever a non-trivial design choice is made. Entries are
> append-only; if a decision is revisited, add a new entry that references the original.

## Format

Each decision follows this lightweight ADR (Architecture Decision Record) structure:

- **Context**: What problem or question prompted the decision.
- **Decision**: What was chosen.
- **Rationale**: Why this option was picked over alternatives.
- **Status**: `accepted`, `superseded by D-NNN`, or `under review`.

---

## D-001: Plugin Architecture — ActionRequest, not LegacyActionRequest

**Phase**: 0 | **Status**: accepted

**Context**: Elasticsearch has two request base classes: the modern `ActionRequest` and the older
`LegacyActionRequest`. New plugins need to pick one.

**Decision**: Use `ActionRequest` (the modern path).

**Rationale**: `LegacyActionRequest` exists for backwards compatibility with older code.
New plugins should use the current API surface. `ActionRequest` integrates cleanly with
`HandledTransportAction` and the modern action registration mechanism.

---

## D-002: Response Type — Raw EsqlQueryResponse, no wrapper

**Phase**: 0 | **Status**: accepted (will be revisited in Phase 1c)

**Context**: The transport action needs a response type. Options: (a) return `EsqlQueryResponse`
directly, (b) create a `PiescriptResponse` wrapper.

**Decision**: Return `EsqlQueryResponse` directly.

**Rationale**: Phase 0 is a pure passthrough — there's nothing to add to the response. A wrapper
would be empty ceremony. When Phase 1c introduces non-query expressions that produce values (not
tabular results), a custom response type will be introduced.

---

## D-003: Security Model — CompositeIndicesRequest + RBAC delegation

**Phase**: 0 | **Status**: accepted

**Context**: Piescript programs touch indices determined at runtime (the ESQL query inside may
reference any index). The security subsystem needs to know how to authorize the action.

**Decision**:
- `PiescriptRequest` implements `CompositeIndicesRequest`, signaling that index resolution is
  deferred.
- The action name `indices:data/read/piescript` is registered in
  `RBACEngine.shouldAuthorizeIndexActionNameOnly()`.
- Index-level authorization is handled by the ESQL engine when it executes the inner query.

**Rationale**: This is the same pattern ESQL itself uses. It avoids duplicating authorization logic
and ensures piescript inherits ESQL's security guarantees automatically. The alternative — parsing
the program to extract index names for upfront authorization — would require a full parser, which
doesn't exist yet in Phase 0.

---

## D-004: Executor — DIRECT_EXECUTOR_SERVICE

**Phase**: 0 | **Status**: accepted (will be revisited in Phase 1c)

**Context**: `HandledTransportAction` requires an executor. Options: (a) `DIRECT_EXECUTOR_SERVICE`
(run on the calling thread), (b) a named thread pool.

**Decision**: `DIRECT_EXECUTOR_SERVICE`.

**Rationale**: Phase 0 does no computation — it extracts a string and delegates to ESQL, which
manages its own threading. Adding a thread-pool hop would add latency for no benefit. When Phase 1c
adds type checking and evaluation (CPU-bound work), a dedicated thread pool should be introduced.

---

## D-005: Type System — Bidirectional Hindley-Milner with zonker-based elaboration

**Phase**: 1 | **Status**: accepted

**Context**: The expression language needs type inference. Options considered:
(a) simple structural typing, (b) classic Algorithm W, (c) bidirectional HM,
(d) full System F with explicit type applications.

**Decision**: Bidirectional Hindley-Milner inference with zonker-based elaboration.

**Rationale**:
- *Bidirectional* checking gives better error messages than pure inference (Algorithm W) because
  checking mode propagates expected types inward.
- *HM* provides full type inference without annotations, which keeps the surface syntax clean.
- *Zonker-based elaboration* avoids substituting into the term tree entirely. The zonker is a
  `Map<Integer, Object>` (metavar ID → solution). Unification writes solutions to the zonker.
  Metavars in types are resolved by chain-following lookup when encountered — during elaboration,
  evaluation, or lowering. There is **no zonking pass** that rewrites the AST. The zonker is
  carried as a lookup table throughout the pipeline. This avoids the cost of repeated substitution
  and keeps the Core IR immutable. See Phase 1 plan, D1.7.
- *System F* was rejected because it requires explicit type applications, which conflicts with the
  "types are inferred, not annotated" philosophy.

**Ref**: [Phase 1 discussion](3cd2a822-792c-4179-a00e-0ba98b875f52)

---

## D-006: Variable Binding — De Bruijn Indices

**Phase**: 1 | **Status**: accepted

**Context**: The Core IR needs a variable representation. Options: (a) named variables with
alpha-renaming, (b) de Bruijn indices, (c) de Bruijn levels, (d) locally nameless.

**Decision**: De Bruijn indices in the Core IR. The surface AST uses named variables; elaboration
converts to de Bruijn indices.

**Rationale**:
- De Bruijn indices eliminate alpha-equivalence issues entirely — structurally equal terms are
  equal.
- They simplify substitution (no capture-avoidance needed).
- The trade-off is readability of the IR, but the IR is not user-facing.
- De Bruijn *levels* were considered but indices are more standard for evaluation (they count
  inward from the binding site, matching the natural stack discipline of an evaluator).

**Ref**: [Phase 1 discussion](3cd2a822-792c-4179-a00e-0ba98b875f52)

---

## D-007: Null Semantics (v0) — Null unifies with Any

**Phase**: 1 | **Status**: accepted (known to be unsound; will be refined)

**Context**: ESQL has null values. The type system needs to handle them somehow. Options:
(a) proper Option/Maybe type from day one, (b) Null as a special type that unifies with everything,
(c) ignore nulls entirely.

**Decision**: v0: `Null` behaves like `Any` — it unifies with every type.

**Rationale**: A proper Option type requires algebraic data types and pattern matching (Phase 1d).
Adding it in Phase 1b would create a circular dependency. Treating Null as Any is unsound (a `Null`
value can appear where an `Integer` is expected) but pragmatic for v0. It lets the type checker
proceed without blocking on ADT infrastructure. The plan is to revisit with a proper Option type
when ADTs land (post-Phase 2).

**Ref**: [Phase 1 discussion](3cd2a822-792c-4179-a00e-0ba98b875f52)

---

## D-008: Node Infrastructure — Only Core IR extends Node

**Phase**: 1 | **Status**: accepted

**Context**: Elasticsearch has a `Node` base class used by ESQL's AST infrastructure (with tree
traversal, rewriting, etc.). Which piescript structures should extend it?

**Decision**: Only `CoreExpr` (the Core IR expression type) extends `Node`. Types (`MonoType`,
`PolyType`), values, and other structures use plain records and sealed interfaces.

**Rationale**: The `Node` infrastructure adds overhead (visitor patterns, attribute maps, immutable
tree rewriting). This is valuable for the IR, which needs traversal and transformation. But types
and values are small, short-lived structures where records are simpler and more performant. Keeping
the Node dependency narrow also reduces coupling to ESQL internals.

**Ref**: [Phase 1 discussion](3cd2a822-792c-4179-a00e-0ba98b875f52)

---

## D-009: Literal Types — Aligned with ESQL DataType

**Phase**: 1 | **Status**: accepted

**Context**: Piescript needs literal types (Integer, String, etc.). Should they be independent or
aligned with ESQL's `DataType` enum?

**Decision**: Aligned with ESQL `DataType`. Piescript literals map to: `INTEGER`, `LONG`, `DOUBLE`,
`KEYWORD` (for strings), `BOOLEAN`, `NULL`.

**Rationale**: Since piescript delegates query execution to ESQL, type compatibility at the boundary
is critical. Using the same type universe avoids lossy conversions and ensures that piescript values
can flow into ESQL expressions (and vice versa) without surprises.

**Ref**: [Phase 1 discussion](3cd2a822-792c-4179-a00e-0ba98b875f52)

---

## D-010: Control Flow — Pattern matching first, if/then/else as sugar

**Phase**: 1d | **Status**: accepted

**Context**: The language needs conditional execution. Options: (a) `if/then/else` as a primitive,
(b) pattern matching as the primitive with `if` as sugar.

**Decision**: `match` is the primitive control-flow mechanism. `if cond then a else b` desugars to
`match cond with | true -> a | false -> b`. `if` is cut from Phase 1a-c; it arrives with pattern
matching in Phase 1d.

**Rationale**: Making `match` the primitive avoids having two redundant constructs. It also
encourages exhaustive handling from the start. The desugar is trivial and the surface syntax
for `if` can still feel natural.

**Ref**: [Phase 1 discussion](3cd2a822-792c-4179-a00e-0ba98b875f52)

---

## D-011: ESQL Plugin Dependency — extendedPlugins, not runtime classpath

**Phase**: 0 | **Status**: accepted

**Context**: Piescript needs access to ESQL classes (`EsqlQueryAction`, `EsqlQueryRequest`,
`EsqlQueryResponse`). Options: (a) `extendedPlugins` declaration, (b) runtime classpath dependency,
(c) copy ESQL interfaces into piescript.

**Decision**: `extendedPlugins = ['x-pack-esql']` in `build.gradle`, with `compileOnly` dependency
on `xpackModule('esql')` and `xpackModule('esql-core')`.

**Rationale**: `extendedPlugins` is the ES-native mechanism for plugin-to-plugin dependencies. It
ensures ESQL is loaded before piescript at runtime and makes the dependency explicit in the build
graph. `compileOnly` means piescript doesn't bundle ESQL classes — they're provided by the ES
distribution. Copying interfaces would create a maintenance burden and divergence risk.

---

## D-012: Execution Model — Plan Graph, Not Direct Interpretation

**Phase**: 3–4 | **Status**: accepted

**Context**: The interpreter walks Core IR. When it encounters process nodes (queries, stream
combinators, `par` blocks), should it execute them directly (fire queries, iterate over pages,
manage async coordination) or build a plan that is executed separately?

**Decision**: The evaluator builds a **plan graph** for process nodes. Pure functional nodes are
evaluated directly by the tree-walking interpreter. Process nodes produce plan graph fragments that
are wired together as the evaluator walks the IR. The completed plan graph is then optimized and
dispatched by a separate executor.

**Rationale**:
- **Separation of concerns**: the evaluator handles the "what" (program semantics), the executor
  handles the "where" and "how" (placement, scheduling, data flow). This enables swapping
  executors without changing the language semantics.
- **Optimization**: a plan graph can be inspected and transformed before execution. Push-down of
  simple transforms into ESQL queries, dead-code elimination of unused `par` branches, and fusion
  of adjacent combinators are all plan-level optimizations that require seeing the full computation
  structure before executing it.
- **Distributed execution path**: the plan graph is the abstraction that enables distributing
  computation to data nodes. A direct-interpretation model would require re-architecting when
  distribution is needed. The plan graph makes the transition incremental — swap the local executor
  for a distributed one.
- **Free monad interpretation**: the plan graph is a free monad over π-calculus effects (Query,
  Par, MapStream, Send, Recv, etc.). The executor is the interpreter of this free monad. This is a
  well-understood pattern with known optimization techniques (handler fusion).

The v0 executor runs everything locally on the coordinator node. Future executors dispatch plan
fragments to data nodes.

**Ref**: [architecture.md § The Plan Graph](architecture.md), [references.md § Free Monads](references.md)

---

## D-013: Two-Layer IR — Functional Expressions and Process Descriptions

**Phase**: 1–3 | **Status**: accepted

**Context**: The Core IR needs to represent both pure computation (let-bindings, lambdas, records)
and effectful operations (queries, parallel composition, stream transforms). Should these be a
single hierarchy or separate?

**Decision**: The Core IR is partitioned into two sealed hierarchies: `CoreExpr` (functional) and
`CoreProcess` (process descriptions). Process nodes may contain functional subexpressions (e.g.,
the lambda argument to `MapStream`), but functional nodes never contain process nodes.

**Rationale**:
- **Clean effect boundary**: mirrors the π-calculus distinction between expressions (which compute
  values) and processes (which perform communication). Effects do not leak inward.
- **Separate handling**: the evaluator dispatches on the node type — `CoreExpr` nodes are evaluated
  to values, `CoreProcess` nodes are planned (produce plan graph fragments). Mixing them in a
  single hierarchy would require runtime discrimination at every evaluation step.
- **Analogous to Haskell's pure/IO boundary**: `CoreExpr` is the pure layer, `CoreProcess` is the
  effectful layer. Referential transparency of the pure layer is what makes distributed execution
  safe — pure subexpressions can be evaluated anywhere, and closures can be shipped to remote
  nodes without changing semantics.
- **Phase 1 designs only `CoreExpr`**. `CoreProcess` is introduced in Phase 3. The separation
  ensures that Phase 1's evaluator does not need modification when process nodes arrive — they
  go to a different handler.

**Ref**: [architecture.md § The Two-Layer IR](architecture.md)

---

## D-014: Code Mobility — Closures as Traveling Code

**Phase**: 3–4 | **Status**: accepted

**Context**: When a user writes `query FROM logs-* |> map inc`, the `inc` function needs to
execute on the nodes that hold the `logs-*` shards. How is user-defined code shipped to remote
execution contexts?

**Decision**: Lambdas and closures attached to plan nodes are "traveling code." Closed lambdas
(no free variables) are serialized as `CoreExpr` subtrees. Closures are serialized as
`(CoreExpr, Map<Name, Value>)` pairs — the code plus a snapshot of captured bindings.

**Rationale**:
- **Purity makes this safe**: the language is pure and referentially transparent. Captured values
  are immutable. Cloning the captured environment to send it to a remote node produces identical
  results to evaluating locally. No aliasing or mutation hazards.
- **Analogous to delimited continuations**: process primitives are the delimiters. The plan graph
  is the reified continuation tree. Each π-primitive captures "what happens next" as a
  continuation that can be dispatched to a remote node.
- **Mobility check**: not all closures can travel. If a closure captures a non-serializable value
  (a stream handle, a channel reference), it must stay on the coordinator. The optimizer flags
  these. For v0, all values are simple (integers, strings, booleans, records) and trivially
  serializable. Future phases with resource types will need linear/affine typing to prevent
  non-serializable captures.
- **Theoretical basis**: Sangiorgi's agent-passing paper shows that code mobility (higher-order
  π-calculus) reduces to name passing in the standard π-calculus. No fundamentally new mechanism
  is required.

**Ref**: [references.md § Sangiorgi (agent-passing)](references.md),
[architecture.md § Traveling Code](architecture.md)

---

## D-015: Join Calculus Influence on Primitive Selection

**Phase**: 4+ | **Status**: accepted (informing future design)

**Context**: The standard π-calculus includes constructs (like input-guarded choice:
`c₁?x.P + c₂?y.Q`) that are notoriously difficult to implement in distributed systems. Which
π-calculus variant should inform piescript's process primitive design?

**Decision**: The join calculus (Fournet & Gonthier) informs which process primitives piescript
exposes. Specifically: avoid synchronous rendezvous and input-guarded choice in favor of
local synchronization patterns (messages travel to a destination and interact only after arrival).

**Rationale**:
- The join calculus was specifically designed to restrict the π-calculus to primitives that have
  efficient distributed implementations, while preserving full expressiveness (encodings exist in
  both directions).
- **Local synchronization** means a process fires only when all required messages have arrived at
  a single location. This avoids the distributed consensus problem inherent in guarded choice.
- **Join patterns** (a process fires when messages from multiple channels all arrive) naturally
  express multi-way synchronization (e.g., "proceed when both query A and query B have results").
- **Practical validation**: JoCaml demonstrates that join calculus primitives embed naturally in
  an ML-family language with minimal surface syntax disruption.
- For v0, `par` blocks with independent bindings are the only process primitive. This is a
  restricted form of join pattern where all branches are independent. Richer join patterns and
  explicit channels are future work, guided by the join calculus model.

**Ref**: [references.md § The Join Calculus](references.md),
[references.md § JoCaml](references.md)

---

## D-016: Stream Combinators as Prelude Built-ins, Not Core IR Nodes

**Phase**: 3 | **Status**: accepted

**Context**: `map`, `filter`, `fold` need special runtime behavior (they construct plan graph
nodes when applied to streams). Should they be dedicated `CoreProcess` IR nodes (`MapStream`,
`FilterStream`, `FoldStream`) or normal functions in a prelude whose implementations produce plan
graph effects?

**Decision**: `map`, `filter`, `fold` are **prelude built-in functions**, not dedicated Core IR
nodes. They are typed as normal polymorphic functions. The compiler may still recognize and
optimize them, but they live in the standard prelude, not in the IR grammar.

**Rationale**:
- **Typeclass compatibility**: when typeclasses are added, `map` becomes `Functor.fmap` specialized
  to `Stream`. If `map` is a `CoreProcess` IR node, this migration requires replacing one IR
  representation with another. If `map` is a prelude function, the migration is purely additive —
  the function's implementation becomes a typeclass instance method.
- **Free monad consistency**: in the plan graph model, stream combinators are effect constructors.
  Effect constructors are functions that produce data (plan nodes), not special syntax. Making
  them normal functions aligns with the free monad interpretation.
- **Uniform Core IR**: the Core IR remains pure function applications. The "special" behavior of
  `map` is in its **runtime implementation** (construct a `MapPlanNode`), not in a dedicated IR
  node type. This keeps the IR simpler and the evaluator uniform.
- **Extensibility**: adding new combinators (`take`, `zip`, `partition`, etc.) means adding
  prelude functions, not extending the Core IR grammar. The IR is stable; the prelude grows.

**What is special syntax**: `query` and `par` remain `CoreProcess` IR nodes because they have
dedicated surface syntax (`query ... ;` and `par { ... } in ...`). Stream combinators are
ordinary functions applied via pipes (`|>`), not special syntax.

**Implementation**: the evaluator has a built-in function table. When `map` is applied to a
lambda and a `StreamVal`, the built-in implementation constructs a `MapPlanNode`:

```java
case "map" -> (Value lambda, Value stream) -> {
    var streamVal = (StreamVal) stream;
    return new StreamVal(new MapPlanNode(lambda, streamVal.planNode()));
};
```

**Ref**: Master plan § 6.1, § 6.2

---

## D-017: Stream Fan-Out — Plan Graph DAG, Not Linear Consumption

**Phase**: 3 | **Status**: accepted

**Context**: What happens when a stream is used multiple times?

```
let s = query FROM logs-*;
let a = s |> map f;
let b = s |> filter g;
```

Options: (a) type error (stream consumed twice), (b) implicit query re-execution, (c) plan graph
DAG with fan-out.

**Decision**: The plan graph is a **DAG, not a tree**. A `StreamVal` wraps a plan node (a
description, not a running computation). Using it twice creates fan-out — two downstream plan
nodes referencing the same source node. The executor handles fan-out using standard query engine
techniques (exchange operators, reference-counted pages, buffering).

**Rationale**:
- **`StreamVal` is data, not a resource.** Under the plan graph model (D-012), a `StreamVal`
  wraps a `PlanNode` — a description of computation, not a running iterator. Sharing a description
  is free. No data is consumed at plan construction time.
- **Fan-out is a solved problem.** ESQL's compute engine handles one-to-many data flow via
  Exchange operators and reference-counted `Block`s. The plan executor leverages this.
- **Ergonomics.** Requiring explicit `tee` for every multi-use stream would be hostile to the
  target audience (security analysts, data engineers). The double-use pattern is natural and
  common.
- **No linearity tax on streams.** Streams do not need to be linear. When linearity is introduced
  (for channel endpoints, Phase 6+), streams remain unrestricted (multiplicity ω). The plan graph
  handles fan-out transparently.

**What does need linearity (future):** channel endpoints (session types require single-use per
protocol step). This is a different concern — channels are communication protocol endpoints,
not data descriptions. Linearity for channels arrives with session types in Phase 6.

---

## D-018: Linearity Roadmap — QTT for Channels, Not Streams

**Phase**: 6+ | **Status**: accepted (directional)

**Context**: Where and when should linear types be introduced?

**Decision**: Linearity (via QTT-style multiplicities) is introduced in Phase 6 alongside
explicit channels and session types. Channel endpoints are linear (multiplicity 1). Streams,
closures, and all other values remain unrestricted (multiplicity ω). The type system uses
multiplicities from the semiring {0, 1, ω} on bindings, without dependent types.

**Rationale**:
- **Streams don't need linearity.** Stream fan-out is handled by the plan graph DAG (D-017).
  Making streams linear would add friction (explicit `tee`) for no safety benefit.
- **Channels do need linearity.** Session types require that each channel endpoint is used exactly
  once per protocol step. Without linearity, a channel endpoint can be aliased, breaking protocol
  safety and deadlock-freedom guarantees.
- **QTT without dependent types** is the right theoretical framework. Multiplicities are static
  annotations tracked by the type checker. No term-level multiplicity computation. This is
  essentially the Linear Haskell approach (Bernardy et al. 2018).
- **Phased introduction**: Phases 1–4 have no linear types. Phase 6 introduces multiplicities
  alongside channels. The multiplicity annotation on function types (`A →_π B`) is a backward-
  compatible extension — existing code uses the default multiplicity (ω) and is unaffected.

**Design consideration for Phase 1**: ensure the function type representation can be extended with
multiplicities later (`TFun(domain, codomain)` → `TFun(mult, domain, codomain)`). No need to
implement now, just leave room in the data types.

**Future potential (speculative)**: the same QTT machinery could enable safe mutable references,
distributed ownership, and incremental computation. These are exploratory directions, not
committed. See [vision.md § Speculative](vision.md).

**Ref**: [references.md § Linear Haskell, QTT](references.md)

---

## D-019: Eager Unification — Phase 1 Simplification

**Phase**: 1b | **Status**: accepted (known limitation; will be revisited)

**Context**: Unification can be performed eagerly (inline during elaboration) or deferred
(collect constraints, then solve). Which approach should Phase 1 use?

**Decision**: Phase 1 uses **eager (inline) unification**. When two types must agree, `Unifier.unify`
is called immediately during the elaboration walk. Solutions are written to the shared zonker and are
visible to all subsequent elaboration steps.

**Rationale**:
- **Simplicity**: eager unification requires no separate constraint language, no constraint store,
  and no solver phase. The elaborator is a single recursive-descent pass that checks and infers
  types as it goes.
- **Sufficient for HM**: standard Hindley-Milner inference (Algorithm W / Algorithm J) uses eager
  unification. It is correct and complete for the feature set in Phase 1 (let-polymorphism, records,
  functions, primops).
- **Error locality**: type errors are reported at the exact point where unification fails, with the
  source location of the triggering expression.

**Known limitation**: eager unification makes it harder to implement features that benefit from
constraint deferral:
- **GADTs / refinement types**: matching on a GADT constructor should refine type variables in the
  branch, which requires local constraint assumptions.
- **Type classes**: instance resolution interleaves with unification; deferred constraints
  (`Num a => a -> a -> a`) are the standard approach.
- **Better error messages**: constraint-based approaches can report errors at the most informative
  location rather than the first failure.

**Migration path**: when type classes or GADTs are introduced (Phase 5+), the unifier will be
replaced with a constraint-based approach (e.g., OutsideIn(X) style). The elaborator's structure
(recursive descent, bidirectional) is compatible with both — the change is in how constraints are
dispatched, not in the traversal itself.

---

## D-020: PrimOp Typing — Concrete Integer-Only (Phase 1)

**Phase**: 1b | **Status**: accepted (will be extended with coercion)

**Context**: How should arithmetic operators like `+`, `-`, `*` be typed? Options:
(a) polymorphic `α -> α -> α` with numeric constraint, (b) concrete per-type signatures,
(c) ad-hoc overloading.

**Decision**: Phase 1 uses **concrete, non-polymorphic signatures**. Arithmetic operators have type
`Integer -> Integer -> Integer`. Comparison operators have type `Integer -> Integer -> Boolean`.
Boolean operators have type `Boolean -> Boolean -> Boolean`. There are no `Long` or `Double`
variants; operands that are not `Integer` (for arithmetic/comparison) or `Boolean` (for boolean ops)
are type errors.

**Rationale**:
- Phase 1 has no type classes or numeric constraints (`Num a`). Without them, polymorphic primop
  types (`α -> α -> α`) require ad-hoc post-unification checks (a `requireNumeric` guard), which
  is fragile and doesn't compose.
- Concrete types are honest: the type tells you exactly what the operator accepts.
- `Long` and `Double` literals exist in the parser/IR but cannot be used with arithmetic until
  coercion rules are defined.

**Migration path**: when type classes arrive (Phase 5+), arithmetic operators become methods on a
`Num` type class, comparison on `Ord`, etc. Coercion rules (`Integer` widens to `Long`) can be
added as an intermediate step before type classes, using explicit coercion primitives.

---

## D-021: Accessor/Update Sugar — Closed-Row Constraints (Phase 1)

**Phase**: 1b | **Status**: accepted (will be relaxed with row polymorphism)

**Context**: The accessor sugar (`.field`) and update sugar (`{ _ | field = expr }`) desugar into
lambdas: `.x` becomes `fn $acc -> $acc.x`, and `{ _ | x = e }` becomes `fn $upd -> { $upd | x = e }`.
These lambdas need parameter types. The naive approach creates a fresh meta for the parameter type
and a separate fresh meta for the result type, but this leaves the two metas disconnected — no
constraint links them, so when the lambda is applied the result type remains unsolved.

**Decision**: Phase 1 constructs the parameter type as a **closed `RecordType`** containing exactly
the fields referenced by the accessor/update:

- Accessor `.x`: `paramType = { x: β }` where `β` is a fresh meta for the projected field's type.
  The lambda type is `{ x: β } -> β`.
- Update sugar `{ _ | x = e }`: `paramType = { x: α }` where `α` is a fresh meta. The result type
  is `{ x: T }` where `T` is the elaborated type of `e`. The lambda type is `{ x: α } -> { x: T }`.

This establishes the structural constraint between the parameter and the result. When the lambda is
applied to a concrete record, unification on the closed row resolves the metas.

**Trade-off**: this is restrictive. `.x` applied to `{ x: 1, y: 2 }` will fail because
`{ x: β }` does not unify with `{ x: Integer, y: Integer }` under closed-row matching (field sets
must be identical). This means accessor and update sugar only work with records that have exactly the
referenced fields — no extra fields allowed.

**Migration path**: when open-row unification arrives (Phase 2), the parameter type changes to
`{ x: β | ρ }` (open row with a row variable tail). This allows the sugar to accept records with
additional fields, which is the correct semantics.

---

## D-022: Lexer — DECIMAL_LITERAL Requires Digit After Dot

**Phase**: 1a | **Status**: accepted

**Context**: The original `DECIMAL_LITERAL` lexer rule `DIGIT+ '.' DIGIT*` allowed `42.` to be
tokenized as a decimal literal (zero digits after the dot). This caused `42.x` to be tokenized
as `DECIMAL_LITERAL(42.) IDENTIFIER(x)` instead of `INTEGER_LITERAL(42) DOT IDENTIFIER(x)`,
preventing the parser from recognizing field projection on integer expressions.

**Decision**: Changed `DIGIT*` to `DIGIT+` in the `DECIMAL_LITERAL` rule. A decimal literal now
requires at least one digit after the dot: `42.0` is valid, `42.` is not.

**Rationale**: `42.` as a decimal literal is unusual (most languages require digits after the dot)
and causes a real ambiguity with field projection syntax. Requiring at least one digit is standard
practice (Haskell, OCaml, Rust all require digits after the decimal point).
