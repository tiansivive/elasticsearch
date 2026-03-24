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

## D-004: Executor — `ThreadPool.Names.GENERIC`

**Phase**: 0 → 2 | **Status**: accepted (revised in Phase 2)

**Context**: `HandledTransportAction` requires an executor. Options: (a) `DIRECT_EXECUTOR_SERVICE`
(run on the calling thread), (b) a named thread pool.

**Original decision** (Phase 0): `DIRECT_EXECUTOR_SERVICE`. Phase 0 did no computation — it
extracted a string and delegated to ESQL, which manages its own threading.

**Revised decision** (Phase 2): `threadPool.executor(ThreadPool.Names.GENERIC)`.

**Rationale**: When the evaluator gained synchronous query execution (Phase 2.8), running on the
transport thread via `DIRECT_EXECUTOR_SERVICE` caused a deadlock: the evaluator called
`client.execute(EsqlQueryAction).actionGet()`, blocking the transport thread needed to deliver the
ESQL response. Additionally, the `IndexResolutionPrePass` callback runs on `search_coordination`
threads — blocking those with `actionGet()` caused the same deadlock. Both `doExecute` (via the
GENERIC executor) and the resolve callbacks (via `executor.execute(() -> ...)`) now fork to GENERIC
threads, which are safe to block. This aligns with the Phase 3 vision where the Evaluator becomes
a pure synchronous engine wrapped by an Executor on its own thread pool.

**Ref**: [Phase 2 eager eval + deadlock fix](303bcf3e-9eef-4719-a47d-24c1ff27a675)

---

## D-005: Type System — Bidirectional Hindley-Milner with zonker-based elaboration

**Phase**: 1 | **Status**: accepted (refined by D-031, D-032, D-034 in Phase 1d; clarified by D-035)

**Context**: The expression language needs type inference. Options considered:
(a) simple structural typing, (b) classic Algorithm W, (c) bidirectional HM,
(d) full System F with explicit type applications.

**Decision**: Bidirectional Hindley-Milner inference with zonker-based elaboration.

The **surface language** is HM-style — no explicit `forall`, no explicit type application
syntax. Type abstractions and applications are **implicit in the surface language**: the user
never writes them.

The **Core IR** is System F. The elaborator infers type abstractions and type applications and
emits them as explicit nodes in the Core IR (`CoreTypeAbs`, `CoreTypeApp` — see D-035). This
is the standard compilation model: HM surface in, System F core out (Dunfield & Krishnaswami,
GHC's OutsideIn(X)).

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
- *System F* was rejected as the **surface language** because it requires explicit type
  applications, which conflicts with the "types are inferred, not annotated" philosophy.
  However, the **Core IR is System F**: the elaborator infers type abstractions (`CoreTypeAbs`)
  at generalization sites and type applications (`CoreTypeApp`) at instantiation sites. Rigid
  type variables (`MonoType.Rigid`, D-031) in the Core IR refer to binders introduced by
  `CoreTypeAbs`. At downstream passes (printing, optimization, lowering), Rigids are resolved
  via environment-based lookup — no tree-walking substitution needed. See D-035.

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

**Phase**: 3–4 | **Status**: superseded by D-040

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

**Superseded by D-040**: The explicit plan graph data structure was replaced by direct
interpretation via the Join Calculus model. The evaluator interprets coordination effects directly
via `SubscribableListener`-based channels (Block A). The theoretical free monad perspective is
preserved: the coordination effects form an algebraic effect signature, and the residual of partial
evaluation is a free monad over this signature. Block D will introduce a lowering pass that
materializes this residual for optimization (push-down, fusion) before runtime interpretation.
See D-040 and [architecture.md § Theoretical Model](architecture.md).

**Ref**: [architecture.md § The Plan Graph](architecture.md), [references.md § Free Monads](references.md)

---

## D-013: Two-Layer IR — Functional Expressions and Process Descriptions

**Phase**: 1–3 | **Status**: superseded by D-040

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

**Superseded by D-040**: The two-layer IR split is no longer planned. `CoreSpawn` and `CoreJoin`
are added as `CoreExpr` variants, not a separate `CoreProcess` hierarchy. The evaluator handles
coordination primitives directly via async callbacks, rather than building a separate plan. The
effect boundary is maintained at the *runtime* level (sync evaluation vs. async channel
operations), not at the IR level.

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

**Phase**: 4+ | **Status**: subsumed by D-040

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

**Subsumed by D-040**: The join calculus is now the primary execution model, not just an influence.
`spawn` and `join` are first-class primitives; `par` is removed. See D-040 for the full decision.

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

**Implementation note (revised by D-040)**: The plan-graph-based implementation described above
(constructing `MapPlanNode` etc.) was superseded. Built-ins now operate over materialized
`StreamVal(List<Value>)` via `applyFunction` callbacks. The core decision — combinators as
prelude built-ins, not Core IR nodes — is unchanged.

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

**Implementation note (revised by D-040)**: The plan-graph-based fan-out mechanism described
above was superseded. Currently `StreamVal` is an immutable `List<Value>` — sharing it is
trivially safe. The core decision — streams allow multi-use without linearity — is unchanged.
In Block D's lowering pass, fan-out over described (not materialized) streams will be handled
by exchange operators or reference counting, as originally envisioned.

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

**Phase**: 1b | **Status**: accepted (superseded by open-row sugar in Phase 1d; see D-029, D-030)

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

---

## D-023: PiescriptResponse — Wrapper, Not Subclass

**Phase**: 1c | **Status**: accepted

**Context**: The eval endpoint needs to return either an expression evaluation result (for piescript
expressions) or an ESQL query response (for the Phase 0 passthrough path). Options: (a) subclass
`EsqlQueryResponse`, (b) create a wrapper that delegates to `EsqlQueryResponse` for queries, (c)
use two separate `ActionType`s.

**Decision**: `PiescriptResponse` is a wrapper. It implements `ChunkedToXContentObject` and
`Releasable`. For expression results, it holds a `Value` and type string and serializes as
`{"type": "...", "result": ...}`. For query passthrough, it holds an `EsqlQueryResponse` and
delegates chunked serialization and cleanup.

**Rationale**: `EsqlQueryResponse` is tightly coupled to ESQL internals (Pages, BlockFactory,
ref-counting for off-heap memory). Subclassing it from an external plugin would create a fragile
dependency. A wrapper cleanly separates concerns: piescript owns its value format, ESQL owns its
columnar format. A single `ActionType<PiescriptResponse>` keeps the REST handler and transport
layer unified.

---

## D-024: Evaluator Architecture — De Bruijn Environment Machine

**Phase**: 1c | **Status**: accepted

**Context**: The evaluator needs a runtime environment for variable lookup. Options: (a) named
variable map, (b) de Bruijn environment (array indexed by index), (c) substitution-based.

**Decision**: De Bruijn environment machine. The environment is a `Value[]` indexed by de Bruijn
index, with position 0 being the most recently bound variable. Lambda application prepends the
argument to the closure's captured environment.

**Rationale**: The Core IR already uses de Bruijn indices (D-006). An indexed array is the natural
and fastest lookup mechanism. No hashing, no name comparisons. `prepend` is a single array copy.
Closures capture the environment by cloning the array at lambda creation time.

---

## D-025: Evaluator Trusts the Type Checker

**Phase**: 1c | **Status**: accepted

**Context**: When the evaluator encounters a `CoreApp` node, it evaluates the function position
and expects a `ClosureVal`. What if it's not? Similarly, `CoreProject` expects a `RecordVal`.

**Decision**: The evaluator uses pattern matches. If the value is not of the expected variant, it
throws `AssertionError` (internal bug), not `EvaluationException` (user error). The only user-facing
runtime errors are null-in-arithmetic (D-027) and division by zero.

**Rationale**: The type checker guarantees that a well-typed program cannot produce a non-closure
in function position or a non-record in projection position. If it happens, the type checker has a
bug. Using `AssertionError` makes this distinction clear: it's an invariant violation, not a
user-caused condition.

---

## D-026: KeywordVal Uses String, Not BytesRef

**Phase**: 1c | **Status**: accepted (deliberate deviation from D-009)

**Context**: D-009 aligns literal types with ESQL's `DataType`. ESQL represents keywords as
`BytesRef` (Lucene's byte-array wrapper). Should the runtime `Value.KeywordVal` also use `BytesRef`?

**Decision**: `KeywordVal` uses `String`. The conversion from `BytesRef` to `String` happens in the
evaluator's `CoreLit` handler (`BytesRef.utf8ToString()`).

**Rationale**: `BytesRef` is a Lucene internal optimized for index storage, not for general-purpose
string manipulation. For Phase 1c's expression evaluator (which doesn't touch indices), `String` is
simpler, safer, and sufficient. The conversion boundary is narrow (one line in `litToValue`).

**Future**: when ESQL query results flow into piescript expressions (Phase 2+), `BytesRef` values
from ESQL result pages will need conversion to `String` at the boundary. The reverse conversion
(`String` to `BytesRef`) will be needed if piescript values flow into ESQL query parameters.

---

## D-027: Null in Arithmetic — Runtime Error

**Phase**: 1c | **Status**: accepted

**Context**: D-007 allows `null` to unify with any type (null-as-bottom). This means `let x :
Integer = null in x + 1` passes type checking. What should the evaluator do when `null` reaches
an arithmetic operation?

**Decision**: `NullVal` in an arithmetic or boolean PrimOp throws `EvaluationException("null value
in <op> operation")`. Division by zero also throws `EvaluationException`.

**Rationale**: The type system is deliberately unsound w.r.t. null (D-007). The evaluator must
catch the cases where this unsoundness surfaces at runtime. Treating null in arithmetic as a
user-facing error (not an `AssertionError`) is correct because it stems from a user-written
program (`null` is user-provided), not a type checker bug.

---

## D-028: Type Variable Convention — Lowercase = Variable, Uppercase = Constructor

**Phase**: 1 | **Status**: accepted

**Context**: Type annotations need to distinguish type variables (`a`, `r`) from concrete type
constructors (`Integer`, `Keyword`). The master plan's formal grammar (Section 13.2) already uses
lowercase identifiers for type variables (`a : Type or a : Row`) and PascalCase for constructors
(`"Keyword"`, `"Int"`), but this convention was not formalized as a decision or implemented in
the parser/elaborator.

**Decision**: In type annotations, identifier case determines interpretation:

- **Lowercase-initial** identifiers (`a`, `r`, `elem`, `row`) are **type variables**. They are
  implicitly universally quantified at the nearest enclosing `let` binding.
- **Uppercase-initial** identifiers (`Integer`, `Keyword`, `Boolean`, `Stream`) are **type
  constructors**. They must resolve to known concrete types.

Examples:
```
let id : a -> a = fn x -> x
let getMsg : { msg: a | r } -> a = fn rec -> rec.msg
let inc : Integer -> Integer = fn x -> x + 1
```

**Rationale**:
- This is the standard convention in ML-family languages (Haskell, OCaml, Elm, PureScript).
  Piescript's target audience overlaps with users of these languages' type annotation styles.
- It avoids the need for explicit `forall` quantifiers in most cases — the elaborator can
  collect all lowercase identifiers in a type annotation and implicitly quantify them.
- The ANTLR grammar's `typePrimary` rule currently treats all `IDENTIFIER` tokens as `TypeCon`.
  Implementation requires splitting this into two cases based on the first character's case.
- Aligns with master plan Section 13.2: `| a -- type variable (a : Type or a : Row)`.

**Implementation notes**: The ANTLR lexer is split into `UPPER_IDENT` and `LOWER_IDENT`
tokens (see D-033). In type annotation positions, the parser uses `UPPER_IDENT` for type
constructors and `LOWER_IDENT` for type variables. Expression-level rules accept both via
a helper `ident` rule. This enforces the convention at the grammar level, not the elaborator.

---

## D-029: Phase Reordering — Open Rows Before Pattern Matching

**Phase**: 1 | **Status**: accepted (supersedes Phase 1d ordering)

**Context**: The original roadmap scheduled Phase 1d as Pattern Matching and deferred row
polymorphism to Phase 2 (bundled with index resolution). After completing Phase 1c (Evaluator),
the accessor/update sugar limitation (D-021: closed-row constraints) became the more pressing
issue — functions like `.x` cannot accept records with extra fields.

**Decision**: Reorder the phases:

- **Phase 1d becomes: Open Rows & Row Polymorphism** — open-row unification, row variables in
  accessor/update sugar, type variable convention (D-028), and row-polymorphic function tests.
- **Phase 1e becomes: Pattern Matching** — the original Phase 1d content (match expressions,
  exhaustiveness, if/then/else sugar).
- **Phase 2 retains: Index Resolution** — the parts of the original Phase 2 that depend on ES
  mappings (`IndexResolver` integration, concrete-row constraints, `query` typing). Row
  unification infrastructure built in Phase 1d is a prerequisite.

**Rationale**:
- Row polymorphism is foundational for the language's expressiveness. Without it, even basic
  record-handling functions are artificially restricted to exact field sets.
- Pattern matching, while important, is independent of the row type system and can follow later
  without blocking other work.
- Pulling open-row unification forward de-risks Phase 2: the core unification algorithm is
  tested in isolation before adding the complexity of index resolution and concrete-row
  constraints.
- D-021 (closed-row accessor/update sugar) is explicitly superseded by the open-row work in
  Phase 1d.

---

## D-030: Open-Row Unification — Leijen-Style, Not Rémy-Style

**Phase**: 1d | **Status**: accepted

**Context**: Open-row unification is needed for row polymorphism. Two well-known approaches
exist: Rémy-style (recursive row constructors with head/tail decomposition, requiring 4-way
pattern matching on row shapes) and Leijen-style (flat field sets with optional tail variables,
operating on field-set differences).

**Decision**: Leijen-style row unification, adapted to piescript's flat `RowType` representation
(`Map<String, MonoType>` fields + `Optional<MonoType.Meta>` row variable tail).

**Algorithm**:
1. Flatten both rows through the zonker (`resolveRow`)
2. Pairwise-unify common fields
3. Compute `onlyA` and `onlyB` (excess field sets)
4. Dispatch on tails:
   - Both closed, no excess → success
   - Both closed, any excess → `MissingFields` error
   - A open, B closed → if `onlyA` non-empty, error; else solve `tailA = RowType(onlyB, ∅)`
   - B open, A closed → symmetric
   - Both open → fresh `r3`; solve `tailA = RowType(onlyB, r3)`; solve `tailB = RowType(onlyA, r3)`
5. Occurs check before solving row metas

**Rationale**:
- Our `RowType` is already a flat map, not a recursive row-cons structure. Rémy-style would
  require converting to/from recursive form or simulating it — unnecessary complexity.
- Leijen's approach is a single recursive function operating on set differences, which maps
  directly to our data structure.
- The commutative unification (field order doesn't matter) is natural with map-based rows.

**Ref**: Leijen — *Extensible records with scoped labels* (2005). See [references.md](references.md).

---

## D-031: Rigid Type Variables — Skolem Constants for Bound Variables

**Phase**: 1d | **Status**: accepted

**Context**: The type system needs to distinguish between unification variables (metas — "holes"
to be solved) and universally quantified type variables (which must not be solved). Without this
distinction, type annotations like `a -> a` cannot be correctly checked — the `a` would be
treated as a meta and immediately solved, losing its universal meaning.

**Decision**: Add `record Rigid(int id, Kind kind) implements MonoType {}` to the `MonoType`
sealed interface. Rigids are skolem constants representing bound type variables.

**Semantics**:
- Two Rigids with the **same id** unify successfully.
- A Rigid with a **different id**, any `TCon`, `Arrow`, `RecordType`, `AppType`, or `Meta` is
  a type error (`Mismatch`).
- Rigids appear in the **body** of `TypeScheme`s, representing the quantified variables.
- At **instantiation** (use site), Rigids are replaced with fresh Metas.
- At **annotation elaboration**, lowercase type variable names are mapped to fresh Rigids.
- At **generalization** (unannotated definitions), unsolved metas are converted to Rigids.

**Rationale**:
- Standard approach in ML/Haskell type inference (GHC calls them "skolems" or "rigid type
  variables"). Necessary for correct checking against universal types.
- Without Rigids, there is no way to verify that a body works "for all a" — metas would be
  eagerly solved, defeating universal quantification.
- `TypeScheme` remains our type abstraction (no `Forall` variant in `MonoType` needed for
  rank-1).

---

## D-032: Zonker API — `zonk` Returns `Optional<MonoType>`

**Phase**: 1d | **Status**: accepted (supersedes `resolveType` semantics in D-005)

**Context**: The current `resolveType` method on `ElaborationState` returns the meta itself
when unsolved — `state.resolveType(meta)` returns the same `Meta` on miss. This makes it
impossible for callers to distinguish "unsolved meta" from "the solution happens to be a meta
of the same shape." It also conflicts with the no-substitution principle (D-005): consumers
should use the zonker as a lookup table, not get confused by sentinel returns.

**Decision**:
- Rename `resolveType` to `zonk`.
- Return `Optional<MonoType>`: `Optional.of(solution)` when the meta is solved (following
  chains), `Optional.empty()` when unsolved.
- Remove `resolveDeep` from `TypeWalker`. It performs a full substitution pass over a type tree,
  which violates D-005's no-substitution principle. Consumers that need display-ready types
  (e.g., `CorePrinter`) resolve inline at point of use.

**Rationale**:
- `Optional.empty()` is unambiguous — the meta is unsolved.
- Callers that want the old behavior use `state.zonk(type).orElse(type)`.
- Removing `resolveDeep` keeps the system honest about the no-substitution invariant. Every
  type resolution is point-of-use, explicit, and lazy.

**Implementation status (updated after D-035)**: `resolveType` was renamed to `zonkOrKeep`
(preserving the old return-meta-on-miss semantics) rather than the `Optional`-returning `zonk`
specified here. D-035 eliminated `TypeWalker.walkType`, `TypeWalker.generalize`, and
`TypeWalker.instantiate`. `resolveDeep` remains in `TypeWalker` and is used by `CorePrinter`
for display; it will eventually be replaced by environment-based Rigid resolution in
downstream passes. The `zonkOrKeep` vs `Optional`-returning `zonk` deviation remains.

---

## D-033: ANTLR Lexer Split — `UPPER_IDENT` and `LOWER_IDENT`

**Phase**: 1d | **Status**: accepted (refines D-028 implementation)

**Context**: D-028 established the convention: lowercase identifiers are type variables,
uppercase identifiers are type constructors. The initial plan was to enforce this in the
elaborator (runtime check on the first character). This is fragile and misplaced — grammar-level
conventions belong in the grammar.

**Decision**: Split the `IDENTIFIER` lexer rule into two tokens:

```antlr
UPPER_IDENT : [A-Z] (LETTER | DIGIT | '_')* ;
LOWER_IDENT : [a-z] (LETTER | DIGIT | '_')* ;
```

Parser rules are updated:
- Expression positions (variables, field names, bindings): use helper rule `ident : UPPER_IDENT | LOWER_IDENT`
- Type constructor position (`typePrimary`): `UPPER_IDENT` only
- Type variable position (`typePrimary`): `LOWER_IDENT` only
- Row variable tail: `BAR LOWER_IDENT`

**Rationale**:
- The grammar is the source of truth for syntax. Enforcing case conventions in the elaborator
  mixes concerns (syntax analysis vs. semantic analysis).
- Parser errors for misplaced casing are immediate and clear ("expected UPPER_IDENT, got
  LOWER_IDENT 'integer'") rather than delayed elaboration errors.
- Keywords (`let`, `fn`, `in`, etc.) are matched before identifiers by ANTLR's priority rules,
  so no conflicts arise.

---

## D-034: Type Annotations Elaborate to `TypeScheme`

**Phase**: 1d | **Status**: accepted

**Context**: The current `resolveTypeAnnotation` returns `MonoType`. This cannot represent
polymorphic type annotations like `a -> a`. When type variables appear in an annotation, the
result must be a universal type (type abstraction), not a monomorphic type with dangling
unification variables.

**Decision**: `resolveTypeAnnotation` returns `TypeScheme`. The elaboration of a type annotation
proceeds:

1. Walk the CST type, collecting `LOWER_IDENT` names into a rigid scope
   (`Map<String, MonoType.Rigid>`).
2. Each new lowercase name allocates a fresh `Rigid` with the appropriate `Kind` (TYPE for type
   positions, ROW for row-variable tail positions).
3. Construct the `MonoType` body using Rigids in place of type variables.
4. If the rigid scope is empty, return `TypeScheme.mono(body)`.
5. Otherwise, return `new TypeScheme(rigidScope.toKindMap(), body)`.

**Checking against a universal type**: when checking an expression against a `TypeScheme` from
an annotation, the Rigids are already in the body. Check the expression against `scheme.body()`
directly. Rigids in the body cannot be solved by unification — they act as opaque constants.
If the body does not work for all possible types, unification will fail (e.g., `Integer` vs
`Rigid(0)` → Mismatch).

**Binding flow**:
- **Annotated definitions**: the `TypeScheme` from the annotation is stored directly in the
  environment. No generalization step — the annotation already specifies the polymorphic
  structure.
- **Unannotated definitions**: the body is inferred using Metas. Generalization collects
  unsolved metas, converts them to Rigids, and wraps in a `TypeScheme`.

Both paths produce equivalent TypeSchemes. At use sites, instantiation replaces Rigids with
fresh Metas.

**Rationale**:
- Returning `MonoType` from annotation elaboration loses the universal quantification. The
  elaborator must know which variables are bound (Rigids) vs. which are holes (Metas).
- The annotation-as-TypeScheme approach is standard in bidirectional type checkers (Dunfield &
  Krishnaswami 2013, GHC's OutsideIn(X)).

**Ref**: [System F Core IR session](8f5cc3a8-4c26-4f71-8fb0-1ea3c17f527b)

---

## D-035: Core IR is System F — Explicit `CoreTypeAbs` and `CoreTypeApp`

**Phase**: 1d+ | **Status**: accepted, **implemented**

**Context**: D-005 describes the Core IR as "System F-omega-like" where type abstractions and
applications are "implicit — inferred by the elaborator, never written by the user." The word
"implicit" was ambiguous: it means **implicit in the surface language** (the user doesn't write
them), not **absent from the Core IR**. The standard PL meaning of elaboration is translating
an implicit surface language into an explicit core language. The elaborator infers type
abstractions and applications and emits them as Core IR nodes.

**Decision**: The Core IR is System F with deferred constraint solving. Two new **unary** nodes
are added to the `CoreExpr` sealed hierarchy:

```java
CoreTypeAbs(Source, int rigidId, Kind kind, CoreExpr body, MonoType type)
CoreTypeApp(Source, CoreExpr polyExpr, MonoType typeArg, MonoType type)
```

Both are unary: multiple quantifiers/applications are represented as nested nodes.

**`CoreTypeAbs`** (type abstraction / Λ-node):
- Emitted at generalization sites (let-bindings with polymorphic types).
- Binds a single type variable: `rigidId` + `kind`.
- Multiple quantified variables → nested `CoreTypeAbs` nodes.
- `type` is the body's type.

**`CoreTypeApp`** (type application / @-node):
- Emitted at instantiation sites (use sites of polymorphic bindings).
- Applies a single type argument: `typeArg` (a fresh meta).
- Multiple type arguments → nested `CoreTypeApp` nodes.
- `type` is the instantiated type.

**Deferred constraint solving**: The elaborator does not call `Unifier.unify` inline. Instead,
it emits `Constraint(left, right, line, column)` records into an accumulator on
`ElaborationState`. Constraints are solved incrementally at generalization boundaries (so that
`collectMetas` can see through solved metas) and at the end of the program. This decouples
constraint generation from solving and keeps the elaboration logic clean.

**`generalize` creates Rigids**: At each generalization site, the elaborator solves pending
constraints, collects unsolved metas at the binding level, converts each to a `Rigid` in the
zonker, and builds a `TypeScheme` mapping Rigid IDs to kinds.

**`instantiate` uses the same fresh metas for CoreTypeApp**: At each use site,
`instantiateAndWrap` creates fresh metas for each quantified Rigid, walks the scheme body
(resolving metas through the zonker and substituting Rigids), and wraps the `CoreVar` in
nested `CoreTypeApp` nodes using those same fresh metas.

**Example**: `let f = fn x -> x in { fst: f 1, snd: f true }` elaborates to:

```
(let f : b -> b = (Λ b. (fn x : b -> x)) in { fst: ((f @Integer) 1), snd: ((f @Boolean) true) })
```

**What was eliminated**:
- `TypeWalker.walkType` — deleted.
- `TypeWalker.instantiate` — deleted; replaced by `Elaborator.instantiateAndWrap`.
- `TypeWalker.generalize` — deleted; replaced by `Elaborator.generalize`.

**What remains**:
- `TypeWalker.resolveDeep` — used by `CorePrinter` for display. Will eventually be replaced
  by environment-based Rigid resolution in downstream passes.
- `TypeWalker.collectMetas` — used by `generalize` to find unsolved metas.
- `ElaborationState.zonkOrKeep` — the zonker is the union-find; it does not go away.

**Rationale**:
- The Core IR being System F is the standard compilation model for HM languages (GHC, MLton,
  OCaml's Flambda). The surface is implicit; the core is explicit.
- Deferred constraints decouple constraint generation from solving, making the elaborator
  easier to reason about and extend.
- Solving at generalization boundaries ensures `collectMetas` sees through solved metas
  (e.g., row tails), producing correct polymorphic type schemes.
- `CoreTypeAbs`/`CoreTypeApp` nodes provide explicit information for downstream passes:
  the optimizer can see exactly where polymorphism is introduced and eliminated, enabling
  specialization and monomorphization as future optimizations.

**Ref**: Dunfield & Krishnaswami 2013 (bidirectional HM elaborating to System F),
GHC Core (System FC with explicit type abstractions and applications),
[System F Core IR session](8f5cc3a8-4c26-4f71-8fb0-1ea3c17f527b)

---

## D-036: Bidirectional Checking Mode — Missing, Tracked for Implementation

**Phase**: 1b (gap) | **Status**: accepted

**Context**: D-005 specifies "bidirectional Hindley-Milner" and states that "checking mode
propagates expected types inward." The roadmap marked "Bidirectional elaborator (infer / check
modes, desugaring)" as complete. However, the elaborator only has a single `elaborate` method
(synthesis mode). There is no checking mode — no mechanism to propagate an expected type inward
through the elaboration. All type-directed information flows outward (synthesize) and is then
constrained after the fact via `emitConstraint`.

This means:
- **Let with annotation**: the RHS is synthesized first, then constrained against the annotation
  after the fact — backwards from bidirectional checking, which should resolve the annotation
  first and check the RHS against it.
- **Ascription** (`e : T`): the canonical "switch to checking mode" form in any bidirectional
  system. Currently synthesizes `e` and constrains afterward.
- **Lambda against arrow type**: when a lambda is checked against a known function type, the
  parameter type should flow inward. Currently a fresh meta is created and constrained later.
- **∀-CHECK rule**: to check an expression against a universal type (`∀a. τ`), the standard
  rule introduces the type variable and checks against the body, producing a `CoreTypeAbs`.
  Currently, `CoreTypeAbs` insertion is hardcoded at the let level via `wrapTypeAbs`, not a
  general elaboration rule.

For rank-1 HM with deferred constraints, the current approach produces correct results — the
semantics are equivalent. But the structure loses the key benefit of bidirectional checking:
propagating known types inward for better error locality and enabling the ∀-CHECK rule as a
general principle.

**Decision**: Add a `check` method to the elaborator that accepts a `TypeScheme` as the expected
type. The `elaborate` method remains as the synthesis mode. The checking mode:

1. If the expected `TypeScheme` has quantifiers, introduce `CoreTypeAbs` nodes (the ∀-CHECK
   rule) and recurse with the body.
2. Delegate to form-specific checking rules where beneficial (e.g., lambda checked against
   arrow decomposes the arrow and assigns the parameter type directly).
3. Fall back to synthesize + constrain for forms without specialized checking rules.

`TypeScheme` is used instead of adding a `Forall` variant to `MonoType` — the scheme already
represents universal quantification and is the natural carrier for the expected type.

**Immediate fix (D-036a)**: refactor `Let.let` and `Let.topBindings` to resolve the annotation
(or create a fresh meta) *before* elaborating the RHS, and constrain/generalize in the correct
order. This is the minimal structural fix.

**Full implementation (D-036b)**: add the `check` method with ∀-CHECK, lambda-against-arrow,
and ascription-as-check. This is the full bidirectional checking mode.

**Rationale**: The elaborator should match the system it claims to implement. Bidirectional
checking is not just a label — it provides concrete benefits (better error messages, natural
∀-handling, cleaner let-binding flow). The current synthesis-only approach is Algorithm J with
deferred solving, not bidirectional HM.

**Ref**: [Bidirectional elaborator session](3308f68e-e239-4a60-912c-47cfba6eabcc),
[Bidir refinements & D-038](303bcf3e-9eef-4719-a47d-24c1ff27a675)

---

## D-037: Environment-Carrying Instantiation — Future Enhancement

**Phase**: 1b (future) | **Status**: proposed

**Context**: Instantiation of polymorphic variables currently walks the entire `TypeScheme` body,
substituting each quantified `Rigid` with a fresh `Meta`. This is complicated by the fact that
`generalize` does not rebuild the scheme body — it records `meta → rigid` solutions in the
zonker but stores the original type (still containing `Meta` nodes) as the body. Therefore
`instantiateBody` must zonk through metas to find the rigids underneath before substituting,
leading to the tangled logic in `Polymorphism.instantiateBody`.

The project's design principle is to avoid eager substitutions where possible (the zonker itself
embodies this — metas are resolved lazily via lookup, not by rewriting type trees). Instantiation
currently violates this principle: it walks and rebuilds the type on every use of a polymorphic
variable.

**Proposed approach**: Attach a small type-level environment (a `Map<Integer, MonoType>` mapping
rigid IDs to their instantiated metas) to the instantiated type, analogous to how closures pair
a term body with a value environment. Instantiation becomes O(k) (extend the env with k fresh
metas) instead of O(n) (walk an n-node type body). Rigids resolve lazily through this environment
at the point of use — unification, lowering, printing, etc.

This eliminates:
- The `instantiateBody` walk entirely.
- The need for `generalize` to rebuild or zonk the body.
- The `meta → rigid → meta` round-trip through the zonker.

**Trade-offs**: Downstream consumers (unifier, printer, evaluator) must be aware of the env and
resolve rigids through it. This is the same trade-off as the zonker itself — lazy resolution
requires cooperation from all readers.

**Decision**: Deferred as a future enhancement. The current substitution-based instantiation is
correct and the type bodies are small in practice. Revisit when type complexity grows or
instantiation becomes a measurable cost.

**Ref**: [Bidirectional elaborator session](3308f68e-e239-4a60-912c-47cfba6eabcc)

---

## D-038: `MonoType` → `Type` with `Forall` Variant

**Phase**: 1b | **Status**: planned

**Context**: The type representation `MonoType` currently has no way to express polymorphic types
(`∀a. τ`). In System F, type abstraction (`Λa. e`) has type `∀a. τ`, which is itself a type —
not a separate metalinguistic concept. Our current design represents polymorphism out-of-band via
`TypeScheme`, which maps quantified Rigid IDs to Kinds alongside a monomorphic body. This works
for let-bindings (where generalization produces a scheme stored in the context) but breaks for
expression-level polytypes such as polytype ascription.

**Symptom**: `(fn x -> x : a -> a)` correctly elaborates to `CoreTypeAbs(a, CoreLam(x, x))` via
the ∀-CHECK rule, but `CoreTypeAbs.type()` returns the body's monotype `Arrow(Rigid(0), Rigid(0))`
rather than a proper `∀a. a → a`. When the surrounding `let` binding calls `generalize`, it finds
no unsolved metas (only rigids) and produces a monomorphic scheme. The rigids then leak into
unification, where `Rigid ~ Integer` fails. The fundamental issue is that `CoreTypeAbs` cannot
express its own type because `MonoType` has no `Forall` constructor.

**Proposed approach**: Rename `MonoType` to `Type` and add a `Forall(int rigidId, Kind kind, Type body)`
variant. `CoreTypeAbs.type()` would then return `Type.Forall(...)`, and consumers (let-binding,
application, unifier) can pattern-match on it. `TypeScheme` remains useful as a convenience for
let-generalization and will later serve as the representation for qualified types (e.g., type-class
constraints `∀a. C a => τ`), so it should not be removed.

**Impact**:
- `CoreExpr.type()` return type changes from `MonoType` to `Type`.
- The unifier must handle `Forall` (likely by instantiation before unifying).
- `generalize` can detect `Forall` in the RHS type and extract the scheme directly.
- The evaluator and printer must handle `Forall` in type positions.
- Constraint emission and `emitConstraint` signatures change accordingly.

**Decision**: Planned. Polytype ascription tests are skipped (`@AwaitsFix`) until this is
implemented. The annotated-let path (`let f : a -> a = ...`) works because the scheme is
constructed directly from the annotation, bypassing `generalize`.

**Ref**: [Bidir refinements & D-038](303bcf3e-9eef-4719-a47d-24c1ff27a675)

---

## D-039: Eager Stream Evaluation — Synchronous Evaluator with Client Injection

**Phase**: 2 | **Status**: accepted

**Context**: Phase 2.8 requires executing ESQL queries from within the evaluator and representing
the results as piescript values. Key design questions: (1) Where does the query fire? (2) How are
streams represented? (3) How do built-ins operate over streams?

**Options considered**:
- (a) Fire queries in the pre-pass, store results in a map, pass to evaluator — breaks alignment
  with Phase 3 where the executor walks the plan graph and fires queries at evaluation time.
- (b) Make the evaluator fully asynchronous (CPS) — over-engineering for Phase 2 and
  counter-productive for Phase 3, where the evaluator's role is pure synchronous expression
  evaluation.
- (c) Inject a `Client` into the evaluator; `CoreQuery` fires `EsqlQueryAction` synchronously
  and converts the response to an eagerly materialized `StreamVal(List<Value>)`. Built-ins
  (`map`, `filter`, `reduce`) operate over the materialized list via `applyFunction` callbacks.

**Decision**: Option (c).

**Rationale**: The evaluator stays synchronous, which matches its Phase 3 role as a pure expression
engine. The `CoreQuery` logic will move to the `Executor` in Phase 3 — the evaluator itself won't
need to change. `StreamVal` uses `List<Value>` (not `List<RecordVal>`) because `map` can transform
records into scalars. `EsqlValueConverter` bridges ESQL's Java types to piescript `Value`s via
`instanceof` dispatch. The deadlock from synchronous query execution on transport/coordination
threads is resolved by running on `ThreadPool.Names.GENERIC` (see D-004 revision).

**Trade-offs**: Eager materialization loads all rows into memory. This is acceptable for Phase 2's
prototype scope. Phase 3 introduces streaming/push-down to ESQL for efficient processing.

**Ref**: [Phase 2 eager eval session](303bcf3e-9eef-4719-a47d-24c1ff27a675)

---

## D-040: Join Calculus Execution Model — `spawn`/`join` Replace Plan Graph + `par`

**Phase**: Block A | **Status**: accepted

**Context**: The original plan (D-012, D-013, D-015) called for a plan graph architecture where
process nodes (`CoreProcess`) produce a DAG of distributed operations (a free monad over π-calculus
effects), which is optimized and dispatched by a separate executor. `par` blocks were the initial
concurrency primitive (Phase 4), with richer join patterns deferred to Phase 6+.

After completing Phase 2 (eager evaluation with materialized streams), a critical re-evaluation
found that:

1. The plan graph's optimization benefits (push-down into ESQL, combinator fusion) require
   significant compiler engineering (closure conversion, lambda lifting, defunctionalization)
   that is not immediately justified. Simple cases map to ESQL, but complex lambdas (recursion,
   HOFs, closures, sub-queries) do not.
2. The plan graph's distributed execution benefits are not realized until a distributed executor
   exists (originally Phase 5). The v0 local executor would run the plan graph on the coordinator
   anyway — adding an indirection layer with no immediate payoff.
3. The `par` primitive is a restricted form of join pattern that does not generalize well. It
   requires a separate `CoreProcess` IR hierarchy, yet delivers only independent concurrent
   bindings — no multi-way synchronization, no reaction rules, no streaming coordination.
4. The Join Calculus (Fournet & Gonthier) provides a more fundamental and flexible set of
   primitives (`spawn`, `join`, channels) that subsume `par` while being efficiently implementable
   on Elasticsearch's existing `ActionListener` / `SubscribableListener` infrastructure.

**Decision**: Replace the plan graph + `par` architecture with a Join Calculus execution model.

**Primitives**:
- `spawn expr` — launch `expr` asynchronously, return a channel (`Channel τ`) that will carry the
  result. Implementation: fork to `threadPool.executor(GENERIC)`, write result to a
  `SubscribableListener<Value>`.
- `join (c₁ x₁) & (c₂ x₂) & ... -> body` — synchronize on one or more channels. When all
  specified channels have delivered values, bind each value to its variable and evaluate `body`.
  Implementation: compose `SubscribableListener` callbacks; for n-ary joins, use
  `GroupedActionListener` to collect all results before firing.
- `Channel τ` — a typed channel. Block A: single-value (future-like), backed by
  `SubscribableListener<Value>`. Block B: extended to multi-value with `newchan`, `send`, and a
  lightweight concurrent queue implementation.

**IR representation**: `CoreSpawn` and `CoreJoin` are added as `CoreExpr` variants, not a
separate `CoreProcess` hierarchy. The two-layer IR split (D-013) is no longer needed — the effect
boundary is maintained at the runtime level (sync vs. async evaluation), not the IR level.

**Evaluator model**: The evaluator becomes asynchronous (CPS / ActionListener-based). When it
encounters `CoreSpawn`, it creates a `SubscribableListener`, forks the computation, and returns
a `SpawnVal`. When it encounters `CoreJoin`, it registers callbacks on the channels.
`TransportPiescriptAction` wires the final result to the transport `ActionListener`.

**What is preserved**:
- D-005 (HM type system) — unchanged
- D-006 (de Bruijn indices) — unchanged
- D-014 (traveling closures) — concept preserved; implementation deferred to distributed execution
- D-016 (combinators as prelude built-ins) — unchanged; `map`/`filter`/`reduce` remain eager
  built-ins over materialized `StreamVal`
- D-017 (stream fan-out) — concept preserved for future streaming channels
- D-018 (linearity for channels) — still the plan for Phase 6

**What is superseded**:
- D-012 (plan graph, not direct interpretation) — replaced by direct interpretation with async
  channels
- D-013 (two-layer IR) — `CoreSpawn`/`CoreJoin` are `CoreExpr` nodes
- D-015 (join calculus as future influence) — join calculus is now the primary model, not just
  an influence
- Old Phase 3 (stream runtime + plan graph) — replaced by Block A
- Old Phase 4 (`par` blocks) — replaced by Block A (`spawn` + `join` subsume `par`)
- Old Phase 5 (distributed executor) — subsumed by Blocks D + E (push-down compilation + Exchange
  integration) with distribution achieved incrementally through ES infrastructure

**What is deferred (not removed)**:
- Push-down compilation (piescript → ESQL expressions) — Block D, significant compiler work
- Exchange integration (streaming Pages) — Block E
- Distributed dispatch (ship closures to data nodes) — achievable incrementally via transport
  layer, does not require a plan graph

**The free monad is preserved, not eliminated.** The Join Calculus effects (`spawn`, `join`,
`query`, `newchan`, `send`) form an algebraic effect signature. The evaluator is an effect handler.
The free monad arises naturally as the **residual of partial evaluation**: the evaluator reduces
pure expressions to values, and gets stuck on coordination effects. The stuck residual is
`Free JoinF Value` — a free monad over the Join Calculus effect signature. In Block A, the
evaluator eagerly interprets this residual (continuation monad / CPS via ActionListener callbacks).
In Block D, the evaluator splits into a partial evaluator (producing the residual) and a runtime
interpreter (executing the optimized residual), with an optimization pass in between. This is
piescript's lowering pass. See [architecture.md § Theoretical Model](architecture.md).

**Phased implementation**:
- **Block A**: `spawn` + single-value `when` (D-041). Channels are `SubscribableListener<Value>`.
  Queries complete fully before delivering results. Evaluator becomes uniformly async.
- **Block B**: Multi-value channels. `newchan`, `send` primitives. Lightweight concurrent queue.
  Join automaton for pattern matching over streaming values.
- **Block C**: `writeTo` sink + scheduler. Persistence and scheduled execution.
- **Block D**: Push-down compilation. Core IR → ESQL text compiler. Mobility analysis.
- **Block E**: Exchange integration. Piescript in ESQL's streaming pipeline.

**Rationale**:
- The Join Calculus primitives map directly to ES infrastructure (`SubscribableListener` for
  single-value channels, positional collector for `when` synchronization (D-041),
  `threadPool.executor(GENERIC)` for spawn). No new distributed infrastructure is needed for v0.
- The evaluator-as-interpreter model is simpler than evaluator + planner + executor. The plan
  graph indirection provided optimization hooks that require significant compiler work to exploit
  — work that is better scoped as a dedicated Block (D) rather than a prerequisite for basic
  concurrency.
- `spawn` + `when` are strictly more expressive than `par`: any `par` block can be expressed as
  spawns + a `when`, but `when` also supports multi-way synchronization, streaming coordination,
  and reaction rules that `par` cannot express.
- The theoretical foundation (Join Calculus) guarantees that all coordination patterns have
  efficient distributed implementations, future-proofing the design for cross-node execution.

**Ref**: [Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef),
[references.md § Join Calculus](references.md),
[references.md § Sangiorgi (agent-passing)](references.md)

---

## D-041: Block A Implementation Decisions — `when` Keyword, Uniformly Async Evaluator, Positional Collector

**Phase**: Block A | **Status**: accepted

**Context**: Block A implements the Join Calculus coordination primitives (`spawn`, channel
synchronization) from D-040. During planning, three design decisions were made that refine or
deviate from the roadmap's initial description.

### 1. `when` keyword instead of `join`

The surface keyword for channel synchronization is `when`, not `join`.

**Rationale**: In the ES/ESQL ecosystem, "join" universally means data joining — SQL JOIN, enrich
joins, lookup joins. The MVP example program (vision.md) performs both channel synchronization
*and* data joining (matching orders to customers). Using `join` for both would be confusing within
a single program. `when` reads naturally as reactive coordination: "when these channels are ready,
do this." It matches the mental model of Watcher/alerting users ("when X happens, do Y") without
any existing ES terminology collision.

The Core IR node is `CoreWhen`, the elaboration helper is `Whens.java`, and the grammar token is
`WHEN`. The underlying Join Calculus theory is unchanged — `when` is the user-facing name for join
patterns. Documentation and architecture references to "join patterns" continue to refer to the
theoretical concept; the keyword is `when`.

**Syntax**:

```
spawn <expr>
when (<chanExpr> <var>) & (<chanExpr> <var>) & ... -> <body>
```

### 2. Uniformly async evaluator

The evaluator uses a single async code path for all programs. Every `evaluate` call takes an
`ActionListener<Value>`. For pure expressions (no `spawn`/`when`), `listener.onResponse(value)`
fires immediately on the calling thread — the async signature has zero overhead when callbacks
complete synchronously.

**Rationale**: The alternative — maintaining separate sync/async evaluator paths — would duplicate
evaluation logic, require a pre-pass to detect coordination primitives, and fail for the edge case
where `spawn` is hidden inside a closure that might or might not be called. A uniform signature is
simpler, consistent with ES's own `ActionListener` conventions throughout the transport layer, and
degrades naturally to synchronous execution for pure programs.

Built-in functions (`map`, `filter`, `reduce`) use an **iterative while-loop** pattern for stream
element processing (not recursive callbacks) to avoid stack growth. The behavior is synchronous for
pure lambda bodies; the async API handles the exotic case of genuinely async lambda bodies (e.g.,
containing `when`) without special-casing.

### 3. Positional collector for `when` (not `GroupedActionListener`)

`CoreWhen` uses a hand-rolled positional collector (`AtomicArray<Value>` + `CountDown`) instead of
`GroupedActionListener`.

**Rationale**: `GroupedActionListener` stores results by *arrival order*
(`pos.incrementAndGet() - 1`), not by binding order. Since `when` bindings map to de Bruijn
indices, the body must see values at their *declared binding positions* — the first binding at
the highest index, the last at index 0. Arrival-order storage would corrupt the environment when
channels complete out of binding order. The positional collector writes each result to a known
slot index, guaranteeing correct ordering regardless of completion timing.

**Ref**: [Block A plan](../../.cursor/plans/block_a_implementation_2fdbab36.plan.md)

---

## D-042: Distributed Execution Model — Explicit Control, `spawn!`, Block Restructure

**Phase**: Block B–D | **Status**: accepted

**Context**: After completing Block A (local async coordination), a design discussion examined how
piescript should approach distributed execution. The original roadmap envisioned deep ESQL
integration: Block D would compile piescript lambdas into ESQL text, Block E would integrate with
ESQL's Exchange for streaming. This approach treated piescript as a frontend to ESQL's compute
engine.

The discussion revealed a fundamental tension: ESQL takes control of distributed execution away
from the user (it decides where and how to query). Piescript should **expose** that control. The
user should be able to name nodes, send code to them, and coordinate results explicitly via
channels. This is precisely what the Join Calculus was designed for (Fournet & Gonthier, Section 5:
locations, code mobility, message routing to definition sites).

**Decisions**:

### 1. `spawn` is sugar, not a primitive

Following Join Calculus Section 1.3 (asynchronous core), the true primitives are: channel creation,
`send`, parallel composition, and `when` (join patterns). `spawn body` desugars to:
`let ch = channel() in fork(send ch body) in ch`. This is not just a theoretical observation — it
is the design principle that makes distributed execution work: the body of a `spawn` auto-sends
its result, but distributed code requires explicit `send` to route results back to coordinator-
owned channels.

### 2. `spawn!` for bare channel creation

`spawn!` creates a channel without executing a body. Mechanically: `new SubscribableListener<>()`
wrapped in a `SpawnVal`. The user completes it via explicit `send`. This avoids introducing a
`channel` keyword while acknowledging that channel creation is the real primitive.

Note: `spawn` could also be a builtin function rather than a keyword. Kept as a keyword for now
for clarity. The eventual clean-up may expose `channel` as the primitive and define `spawn` as
sugar over it.

### 3. Piescript gives explicit control over distributed computing

ESQL is declarative about data retrieval (user says what, ESQL decides where/how). Piescript is
explicit about distributed computation: nodes are values, shards are values, the user sends code
to named nodes and coordinates results via channels. Libraries build higher-level abstractions.

This means:
- `index_topology "pattern"` returns cluster topology as typed records (nodes, shards)
- `send node.inbox closure` ships code to a remote node (closure captures channel references)
- `scan shard` accesses local data on a data node (Lucene queries)
- The user orchestrates the distributed plan; libraries provide convenience

### 4. Block restructure

The roadmap blocks are restructured around the distributed vertical slice:

| Block | Old | New |
|-------|-----|-----|
| B | Multi-value channels | ES topology & node types |
| C | `writeTo` + scheduler | Cross-node code execution (`send`, `spawn!`, closure serialization) |
| D | Push-down compilation | Local data access (`scan`) |
| E | Exchange integration | `writeTo` (stretch goal) |

Old Block B (multi-value channels) is deferred — only relevant for streaming patterns, not the
distributed vertical slice. Old Block D (push-down to ESQL text) is deprioritized — the typeclass
approach (see below) is more general. Old Block E (Exchange integration) is reframed: Exchange is
ES infrastructure that piescript orchestrates explicitly via channels, not infrastructure piescript
is built on.

### 5. Channel serialization via named registry

Channels are named as `<ownerNodeId>:<channelUuid>`. Each node maintains a
`ConcurrentHashMap<String, SubscribableListener<Value>>` channel registry. When a remote closure
does `send ch value`, the runtime sends a transport message to the owner node, which looks up the
`SubscribableListener` and completes it. This implements the Join Calculus locality property:
messages travel to their channel's definition site.

### 6. Exchange as explicit orchestration, not hidden optimization

The compute engine (Page/Block/Exchange) is ES infrastructure that piescript **orchestrates via
channels**, not infrastructure piescript is built on. For scale, the user (or a library) explicitly
sets up an Exchange via a sequence of channel messages: send closure to data node → data node
scans and initializes Exchange sink → sends back metadata → coordinator connects Exchange source →
Pages stream with back-pressure. Piescript doesn't abstract over scale decisions — the user
chooses when to use simple `Value` messages vs. Exchange streaming.

### 7. The type stack: RawData / Page / Value

Three representations of data, chosen explicitly:
- `RawData` — description of shard-local data (`scan` returns this). Typeclass instances push
  operations down (filter → Lucene query). No I/O until materialization.
- `Page`/`Block` — columnar, batched, ref-counted. What Lucene produces on materialization.
  Used for Exchange streaming. Not a piescript concern by default.
- `Stream` of `Value` — what piescript code operates on. `StreamVal(List<Value>)` today; backed
  by Page iterators or Exchange sources at scale.

Typeclass-driven push-down (e.g., `Filterable RawData` → Lucene query construction) replaces the
old Block D (push-down to ESQL text) as the principled optimization path. This is future work
requiring typeclasses in the language.

**Supersedes**: Old Block B/C/D/E definitions in roadmap.md. Old Block D (push-down to ESQL text)
is deprioritized.

**Does not supersede**: D-012 and D-013 were already superseded by D-040. D-040 itself remains
valid — the Join Calculus model is unchanged, only the block structure and distributed strategy
are refined.

**Ref**: [Distributed execution discussion](14bf4826-a39e-4012-ab4c-d73ad902a95f)

---

## D-043: `Stream` → `List` Rename

**Phase**: Block B | **Status**: accepted

**Context**: The piescript type constructor `Stream` and runtime value `StreamVal` represent fully
materialized, in-memory lists — not lazy streams, not Exchange-backed streaming, not back-pressure-
aware data flows. The name was inherited from ESQL's `Stream` concept during Phase 2, but as the
language evolves toward real streaming (Exchange integration, multi-value channels), the name
becomes actively misleading.

**Decision**: Rename `TCon("Stream")` to `TCon("List")` and `StreamVal` to `ListVal` throughout.
The name "List" accurately reflects the current semantics (finite, eager, in-memory). "Stream" is
reserved for future lazy/Exchange-backed streaming (post-MVP).

**Scope**: Type system (`Elaborator.LIST`), values (`Value.ListVal`), Prelude type schemes
(`map`/`filter`/`reduce` signatures), `EvalBuiltins`, `EsqlValueConverter`, `PiescriptResponse`
serialization, all tests.

**Ref**: Block B implementation session

---

## D-044: `topology` Builtin Design — Both Views, Closed Rows

**Phase**: Block B | **Status**: accepted

**Context**: The distributed vertical slice (D-042) requires piescript to "see" the cluster before
it can send code to data nodes. Block B introduces a `topology` builtin that makes ES cluster
topology available as typed piescript values.

**Decisions**:

### 1. Builtin name: `topology`

Simple, unambiguous. Takes a single `Keyword` argument (index name).

### 2. Returns both shard-centric and node-centric views

A runtime flag to select views doesn't work with HM inference (return type must be statically
determined). Instead, `topology` returns a record with both views:

```
topology : Keyword → {
  shards: List { index: Keyword, shard_id: Integer, primary: Boolean, state: Keyword,
                 node: { id: Keyword, name: Keyword, address: Keyword } },
  nodes:  List { id: Keyword, name: Keyword, address: Keyword,
                 shards: List { index: Keyword, shard_id: Integer, primary: Boolean, state: Keyword } }
}
```

### 3. Only STARTED shards

Unassigned and initializing shards have no node and are not useful for the distributed vertical
slice (Block C: shipping code to data nodes). This is a documented limitation.

### 4. Exact index name only

No wildcard, alias, or data stream resolution. Uses `ClusterState.routingTable(ProjectId.DEFAULT).index(name)` directly. Pattern support is deferred.

### 5. EvalDependencies context object

The Evaluator now takes an `EvalDependencies` record bundling `Client`, `Executor`, and
`ClusterService`. This replaces the growing constructor parameter list and scales to Block C
(which will add `TransportService` and a channel registry).

### 6. List utility builtins

`head`, `tail`, `length`, `isEmpty` added as prelude builtins alongside the rename from `Stream`
to `List`. These operate on `ListVal.elements()` — trivial implementations, but essential for
working with topology results and other list values.

**Deferred**: Wildcard patterns, multi-project support (`ProjectId.DEFAULT` used), `node.inbox`
field (Block C), non-STARTED shard states.

**Ref**: Block B implementation session

---

## D-045: Block C Design — ChannelVal, Registry, Transport, Inbox

**Phase**: Block C | **Status**: accepted

**Context**: Block B delivers cluster topology as typed piescript values. Block C makes that
topology actionable: ship a closure to a remote node, get a result back. The design discussion
explored channel representation, transport architecture, inbox semantics, and how `send`/`spawn!`
compose with the existing `spawn`/`when` model.

**Decisions**:

### 1. `SpawnVal` renamed to `ChannelVal(nodeId, channelId)`

The `SpawnVal(SubscribableListener<Value>)` representation from Block A held a JVM-local listener
directly in the value. This cannot serialize — `SubscribableListener` is a local object. The
value is renamed to `ChannelVal(String nodeId, String channelId)` — pure metadata that identifies
the channel and the node that owns it. The `SubscribableListener` moves to the `ChannelRegistry`
(see below). `ChannelVal` is trivially serializable (two strings) and can travel across nodes
inside closures' captured environments.

The rename also reflects that channels are the concept; `spawn` is just one way to create them.

### 2. `spawn!` as bare channel creation

`spawn!` creates a channel without executing a body. Grammar: `SPAWN BANG`. Core IR: `CoreSpawn`
with a nullable body (null = bare channel). Evaluator: generates a UUID, creates a
`SubscribableListener`, registers it in the `ChannelRegistry`, returns `ChannelVal(localNodeId,
channelId)`. The user completes the channel later via explicit `send`.

`spawn expr` (with body) remains unchanged in surface syntax but now also registers via the
registry internally, returning `ChannelVal` instead of holding the listener directly.

Following D-042 §1, `spawn body` is sugar for `let ch = spawn! in fork(send ch body) in ch`.
This is the design principle that makes distributed execution work: the body of `spawn` auto-sends
its result, but distributed code uses explicit `send` to route results to coordinator-owned
channels.

### 3. `send` primitive

`send channel value` completes a channel with a value. Grammar: `SEND expr expr`. Core IR:
`CoreSend(channel, value, type)`. Type: `Channel a -> a -> Null`.

Evaluation depends on locality:
- Local (`nodeId == localNodeId`): look up channel in registry, call `onResponse(value)`.
- Remote: serialize the value, send a transport message to the owner node.

`send` is a keyword/Core IR node, not a builtin function, because it requires custom elaboration
logic (constraining the channel type parameter against the value type).

### 4. ChannelRegistry: `ActionListener<Value>` per entry

Each node maintains a `ChannelRegistry` backed by `ConcurrentHashMap<String, ActionListener<Value>>`.
This stores both regular channels and the inbox.

- **Regular channels**: registered by `spawn!` (or `spawn expr`). The entry wraps a
  `SubscribableListener<Value>` and auto-removes from the registry on completion (to prevent
  leaks). One-shot.
- **Inbox**: registered at plugin startup. A persistent, reusable `ActionListener<Value>` that
  creates a new `Evaluator` for each received value and applies the closure. Never removed.

The `when` evaluator needs `SubscribableListener.addListener`. The registry provides a `lookup`
method that returns the underlying `SubscribableListener` for local channels. The inbox is not
accessible via `lookup`, so `when` on the inbox naturally fails.

### 5. Single transport handler: `piescript/send`

One transport action handles all cross-node communication. The request carries `(channelId,
serialized Value)`. The handler on the receiving node calls
`channelRegistry.get(channelId).onResponse(deserializedValue)`. It never inspects the value type
or the channel ID. Uniform, value-agnostic, channel-agnostic.

There is no separate "execute closure" handler. When a closure is sent to a node's inbox, the
transport handler delivers it like any other value. The inbox's registered `ActionListener` is
what evaluates the closure — that behavior is determined at registration time, not delivery time.

### 6. Inbox argument = local node info (dependency injection via lambda abstraction)

The inbox closure receives the local node's information record as its lambda argument. The inbox
handler applies each received closure with the local node's `RecordVal` (containing `id`, `name`,
`address`, etc.). This eliminates the need for a `local_node` primitive — the node identity is
just a lambda parameter.

This is dependency injection via the most fundamental mechanism in the language: lambda
abstraction. As piescript evolves, the inbox argument type can widen to include shard handles,
local capabilities, or other node-specific context without changing the transport protocol.

Inbox type: `Channel (NodeInfo -> Null)`. Topology node records gain an `inbox` field:
`ChannelVal(nodeId, "INBOX")`.

### 7. `when` only works on local channels

`when` requires a local `SubscribableListener` to call `addListener` on. The detection mechanism
is natural:

- **Remote channels (including remote inboxes)**: the evaluator checks `nodeId == localNodeId`.
  If remote, it fails with "cannot wait on remote channel."
- **Local inbox**: the `ChannelRegistry.lookup` method only returns `SubscribableListener`s
  created by `spawn!`/`spawn`. The inbox is registered as a plain `ActionListener`, not a
  `SubscribableListener`, so `lookup` returns nothing.

In practice, the piescript program only sees other nodes' inboxes (via `topology`). The
initiator's own inbox is never exposed to the program. So `when` on an inbox fails at the first
check (remote), making the second check redundant — but both layers are present for robustness.

### 8. Channels carrying channels: `Channel (Channel a)`

Passing channel references through channels is a natural pi-calculus pattern (name passing). A
channel of type `Channel (Channel a)` is a "rendezvous point for exchanging channel references."
This falls out of HM inference automatically — `Channel` is just a regular type constructor.

This enables the "setup remote node" pattern: send a closure to a data node, the closure creates
a local channel (`spawn!`), sends its reference back to the coordinator, and sets up a `when`
handler. The coordinator then routes messages to the remote channel via the received reference.

### 9. Both Value and CoreExpr serialization required

Closures are `ClosureVal(CoreExpr body, Value[] env)`. To ship a closure across nodes:
- The `CoreExpr` body (unevaluated) must serialize — this is the code.
- The `Value[]` captured environment must serialize — this is the data (which may contain other
  closures, channel refs, records, etc., recursively).

All 11 `Value` variants and all 16 `CoreExpr` variants need `Writeable` implementations, plus
`MonoType`, `RowType`, `LitVal`, and `Op`.

**Supersedes**: D-042 §5 (channel serialization via named registry) is refined — the registry
now stores `ActionListener<Value>` instead of `SubscribableListener<Value>`, and `ChannelVal`
replaces the `<ownerNodeId>:<channelUuid>` naming scheme with structured fields.

**Ref**: Block C design discussion

---

## D-046: Value restriction for let-generalization

**Status**: Accepted
**Date**: 2026-03-17

### Context

Piescript uses Hindley-Milner type inference with let-polymorphism: `let id = fn x -> x` generalizes
to `∀a. a → a`. This is sound for pure values (lambdas, literals) but unsound for side-effecting
expressions. Specifically, `let ch = spawn!` generalizes `Channel ?a` to `∀a. Channel a`, making
each use of `ch` a different instantiation. Downstream constraints from `send` and `when` never
meet, leaving types unresolved.

This is the classic problem that the **value restriction** (Wright 1995, adopted by OCaml/SML)
solves: only *syntactic values* are safe to generalize.

### Decision

Apply the value restriction at both let-generalization sites (`Let.topBindings` and `Let.letImpl`).
Before generalizing, check `isSyntacticValue(rhs)`:

- **Values** (safe to generalize): `CoreLit`, `CoreLam`, `CoreVar`, `CoreFree`,
  `CoreRecord` (if all fields are values), `CoreTypeAbs` (if body is a value).
- **Non-values** (keep monomorphic): `CoreApp`, `CoreLet`, `CorePrimOp`, `CoreProject`,
  `CoreUpdate`, `CoreQuery`, `CoreSpawn`, `CoreWhen`, `CoreSend`.

For non-values, constraints are solved eagerly and the type remains monomorphic.

### Consequences

- `let ch = spawn!` stays `Channel ?a` (monomorphic) — all uses share one meta, unification works.
- `let ch = spawn 42` stays `Channel Integer` (already concrete, no change).
- `let id = fn x -> x` still generalizes to `∀a. a → a` (lambda is a value).
- `let x = f 1` does **not** generalize (application is not a value). This matches OCaml behavior.
- Future: if relaxed generalization is needed (e.g., for partially applied builtins), the check
  can be expanded. The value restriction is conservative but safe.

**Ref**: Wright (1995) "Simple Imperative Polymorphism", OCaml value restriction

---

## D-047: Send semantics — fire-and-forget, error responsibility model

**Status**: Accepted
**Date**: 2026-03-17

### Context

`send` delivers a value to a channel (local or remote). When the target is an inbox channel,
the payload is a closure that gets evaluated on the remote node. Two classes of error can arise:

1. **Delivery errors** — the transport layer itself fails (network partition, node down, handler
   bug). This is a runtime infrastructure error.
2. **Closure evaluation errors** — user code sent to a remote node throws during evaluation.
   This is a user-logic error that happens to execute on the target node.

The initial implementation blocked the sender's transport response until the remote closure
finished evaluation. This conflated the two error classes and broke the fire-and-forget model
that the pi-calculus semantics require.

### Decision

**`send` is always fire-and-forget.** The transport response is sent as soon as the message is
accepted by the target node, before any closure evaluation occurs. Specifically:

- **Regular channels**: the value is deposited in the `ChannelRegistry` and the response is
  returned immediately.
- **Inbox channels**: the payload is validated as a `ClosureVal`, the response is returned
  immediately, and the closure is evaluated asynchronously on the executor.

**Error responsibility is split by category:**

| Error class | Responsibility | Current handling | Future handling |
|---|---|---|---|
| **Delivery failure** | Initiator node | Propagated as exception (no user-facing handling yet) | `send` returns a `Result` value (requires sum types / variants). The `Err` case carries structured error info the piescript program can inspect and react to. |
| **Closure evaluation failure** | Target node | Logged locally at WARN level | Node-local error handler, and/or a global error reporting channel that aggregates uncaught errors to a coordinator for user visibility. |

Closure evaluation errors are **never** propagated back to the sender. It would be semantically
wrong for a fire-and-forget dispatch to report back errors from code that has been handed off.
The initiator's only contract is "the message was delivered (or not)."

### Consequences

- `send` now has consistent fire-and-forget semantics everywhere (local channels, remote
  channels, inbox).
- Remote closure errors are visible only in the target node's logs (WARN level). No silent
  swallowing — operators can diagnose via node logs.
- Future error handling is split into two independent tracks:
  - **Delivery errors**: model `send`'s return type as `Result<Null, SendError>` once sum
    types / variants land. Piescript code can pattern-match on the result.
  - **Evaluation errors**: design a node-local error handler or a global error reporting
    channel. This is orthogonal to `send` semantics and will be addressed in a later roadmap
    block.
- The `handleInbox` method now forks closure evaluation on the executor, decoupling the
  transport response from the evaluation lifecycle.

**Ref**: Block C design discussion, pi-calculus asynchronous output semantics

---

## D-048: Split topology into cluster topology and index routing

**Status**: Accepted
**Date**: 2026-03-17

### Context

The `topology "index"` builtin (D-044) conflated two distinct concerns:

1. **Cluster topology** — what nodes exist, which one is the local (coordinator) node, their
   addresses and inboxes. Not tied to any index.
2. **Index routing** — which shards of a specific index are placed on which nodes. Inherently
   index-centric.

This made it impossible for piescript code to identify the local node. The coordinator running
the program had no way to find "itself" in the nodes list returned by `topology`. Target nodes
(inside inbox closures) receive their identity via the closure argument, but the initiator did not.

In ES terminology, these map to `ClusterState.nodes()` (cluster topology) vs.
`ClusterState.routingTable().index(name)` (shard routing).

### Decision

Split into four Prelude builtins:

| Builtin | Signature | What it returns |
|---|---|---|
| `topology "cluster"` | `Keyword → { local: NodeBase, nodes: List NodeBase }` | Cluster-level: local node (coordinator) and all nodes with inboxes. The argument is a placeholder until nullary application is supported. |
| `routing "index"` | `Keyword → { shards: List ShardRecord, nodes: List NodeRecord }` | Index-level: shard and node views of shard placement. Same behavior as the old `topology "index"`. |
| `shards "index"` | `Keyword → List ShardRecord` | Convenience: equivalent to `(routing "index").shards`. |
| `nodes "index"` | `Keyword → List NodeRecord` | Convenience: equivalent to `(routing "index").nodes`. |

All are Prelude builtins — no grammar or Core IR changes needed.

### Consequences

- Piescript code can now identify the coordinator: `(topology "cluster").local.inbox`.
- The full send-to-self test case becomes trivial: `send (topology "cluster").local.inbox (fn info -> ...)`.
- `routing` uses the correct ES term for shard-to-node mapping.
- `topology` is freed up for future cluster-level expansion (node roles, attributes, health,
  remote clusters for CCS).
- `shards` and `nodes` reduce boilerplate for the common case of needing one view.

**Ref**: Block C cross-node execution discussion

---

## D-049: Polymorphic equality (`==` / `!=`)

**Status**: accepted
**Date**: 2026-03-18

### Context

Equality and inequality operators (`==`, `!=`) were typed as `Integer → Integer → Boolean`,
matching the ordering operators (`<`, `>`, `<=`, `>=`). This prevented comparing `Keyword` values
(e.g. `n.id != topo.local.id`), which is essential for filtering nodes in cross-node programs.

### Decision

Make `==` and `!=` **fully polymorphic**: `∀a. a → a → Boolean`. Both operands must unify to the
same type, but that type is unconstrained. The elaborator emits a fresh meta variable for the
operand type instead of constraining to `Integer`. The evaluator uses Java's `Object.equals()` on
`Value` record instances (structural equality).

Ordering operators (`<`, `>`, `<=`, `>=`) remain `Integer → Integer → Boolean`.

### Semantics

| Value type | Equality behavior |
|---|---|
| `IntegerVal` | Numeric equality (Java `int ==`) |
| `KeywordVal` | String equality (Java `String.equals`) |
| `BooleanVal` | Logical equality |
| `NullVal` | `Null == Null` is `true` |
| `RecordVal` | Structural: all fields must match (Java record `.equals`) |
| `ListVal` | Structural: element-wise equality (Java `List.equals`) |
| `ClosureVal` | Reference equality (Java record `.equals` on body + env). Semantically questionable — comparing closures is not meaningful, but it won't crash. |
| `ChannelVal` | Structural: same `nodeId` and `channelId` |

### Consequences

- `filter (fn n -> n.id != topo.local.id) topo.nodes` now type-checks and evaluates correctly.
- Cross-node debug scripts work without workarounds.
- Comparing closures or complex values for equality is allowed but not recommended. A future
  `Eq` typeclass (or similar mechanism) could restrict equality to sensible types.
- No changes to the grammar, parser, or Core IR.

**Ref**: Block C manual testing, multinode debug scripts

---

## D-050: Block D — Local data access via `use`, `Shard.open`, `Shard.consume`, `Shard.read`

**Status**: Accepted
**Date**: 2026-03-20

### Context

Piescript can distribute closures to data nodes (Block C), but has no mechanism to read index data
once there. The current `query` path delegates to ESQL and returns tabular results — useful, but
opaque. For the piescript vision of composable data pipelines, the language needs pull-based,
cursor-style access to Lucene data that gives the user control over iteration and field reading.

Block D introduces the first direct interaction between piescript programs and the local Lucene
store on each data node. The design prioritizes minimal surface area: three primitives (`open`,
`consume`, `read`) that expose Lucene's `DocIdSetIterator`-based iteration model without leaking
Lucene internals into the piescript type system.

### Decision

#### 1. `use` declaration and `Index r` type

A `use .index-name as idx` declaration introduces a binding of type `Index r`, where `r` is a
concrete row type resolved at elaboration time via field capabilities. The elaborator's index
resolution pre-pass extracts the index name, calls field caps, and resolves `r` to a row of
mapped fields (e.g., `{ user.name: Keyword, user.age: Double }`).

At runtime, `use` desugars to a `CoreLet` binding an `IndexVal` literal. No new Core IR node is
needed. `IndexVal` carries the index name, UUID, and field metadata. It is serializable — it
travels in closures sent to data nodes.

`Index r` is the **only** way to reference an index. The `routing`, `shards`, and `nodes` builtins
change from `Keyword →` to `Index r →` signatures.

#### 2. Qualified builtin names (namespacing)

All builtins move to qualified names (`Namespace.name`). The parser supports
`UPPER_IDENT DOT LOWER_IDENT` as a qualified name in expression position. The elaborator looks up
qualified names in the Prelude module map. Core IR and runtime use the qualified string key
(e.g., `CoreFree("Math.abs")`, `BuiltinVal("List.map", 2, [])`).

Namespace assignments:
- `Math`: `abs`, `floor`, `ceil`, `round`, `sqrt`, `log`, `min`, `max`, `pow`, `toInt`
- `List`: `map`, `filter`, `reduce`, `head`, `tail`, `length`, `isEmpty`, `at`
- `Cluster`: `topology`
- `Index`: `routing`, `shards`, `nodes`
- `Shard`: `open`, `consume`, `read`
- `Query`: `matchAll`, `term`, `range`, `bool`

This is a clean break — no backwards compatibility with unqualified names.

#### 3. Three pull-based primitives

**`Shard.open : ∀r. Index r → Shard → Query → Channel (Searcher r)`** — acquires an
`Engine.Searcher`, compiles a query (`RecordVal` → `QueryBuilder` → Lucene `Query` → `Weight`),
creates per-segment scorers, and completes a channel with the `Searcher r` value. Async because
searcher acquisition and query compilation involve local I/O.

**`Shard.consume : ∀r. Double → Searcher r → List DocRef`** — advances the internal cursor by up
to N positions, returning a list of `DocRef` handles. If the list has fewer than N elements, the
searcher is exhausted. POSIX `read()` semantics. Synchronous — advances in-memory iterators over
mmap'd posting lists.

**`Shard.read : DocRef → Keyword → Value`** — reads a single field's DocValues for the referenced
document. `Shard.read ref "*"` reads all mapped fields and returns a `RecordVal` matching type
`r`. DocValues reading dispatches by ES field type (Keyword → `SortedDocValues`, numeric →
`SortedNumericDocValues`, boolean → `SortedNumericDocValues` as 0/1, datetime → epoch millis).

#### 4. Queries as plain records

Queries are piescript records converted to `QueryBuilder` at runtime inside `Shard.open`:
`{ match_all: true }` → `MatchAllQueryBuilder`, `{ term: { field: "status", value: "active" } }`
→ `TermQueryBuilder`, etc. No new `Value` variant, no compile-time query validation. Convenience
builtins (`Query.matchAll`, `Query.term`, `Query.range`, `Query.bool`) are optional.

#### 5. `Searcher r` and `DocRef` — opaque, non-serializable, node-local

`Searcher r` holds `Engine.Searcher`, compiled `Weight`, per-segment `Scorer` instances, and
iteration cursor state. `DocRef` holds a segment-local doc ID and a reference to its
`LeafReaderContext`. Both are non-serializable (runtime rejection if serialization is attempted)
and only meaningful on the node where they were created.

#### 6. Resource management

For the vertical slice: auto-release when the searcher's cursor is fully exhausted. If never
fully consumed, the searcher leaks (documented limitation). Explicit `Shard.release` deferred.
Future options include scope-based release (spawn cleanup) or bracket patterns.

#### 7. New `Value` variants

- `IndexVal(name, uuid, fieldMetadata)` — serializable
- `SearcherVal(...)` — non-serializable (holds JVM resources)
- `DocRefVal(leafReaderContext, localDocId)` — non-serializable (tied to its Searcher)

### Consequences

- Piescript programs can read index data directly on data nodes, enabling fan-out search patterns:
  ship a closure, open a shard, consume docs, read fields, send results back.
- The three-primitive design (`open` / `consume` / `read`) is the minimal pull-based surface. It
  maps directly to Lucene's `DocIdSetIterator` model and is the foundation for the future
  three-layer architecture (LuceneM free monad → pull/push patterns → declarative combinators).
- `use` with elaboration-time field caps introduces static index resolution — the type system
  knows the index schema before runtime.
- No size limit for the vertical slice (documented limitation).
- No security enforcement beyond the existing `internal:data/read/piescript/send` permissions
  (documented limitation).
- Recursive/batched consumption requires recursion support (not yet available); the vertical slice
  assumes a single large `consume` call.

### Deferred items

- `Local` kind for type-level serialization prevention
- Size limits on search results
- Projection (read only requested fields)
- Query type safety via ADTs
- Schema introspection on `Index r`
- `Maybe` / ADTs for consume exhaustion signaling

**Ref**: [Block D design discussion](01e7770e-9e20-41ae-a116-2e78142bb672), Block D plan

---

## D-051: Block E — Write primitives (`Shard.writer`, `Shard.write`, `Shard.refresh`, `Shard.globalCheckpoint`, `Index.bulk`) + list literal syntax

**Status**: Accepted
**Date**: 2026-03-22

### Context

The distributed vertical slice (Blocks A–D) is complete: piescript can discover topology, ship
closures to data nodes, open Lucene searchers on shards, iterate and read documents, and
coordinate results back via channels. But the data flow is read-only. Block E adds the write
counterpart — shard-level document writes and a high-level Bulk API wrapper — closing the
read-transform-write loop.

The design mirrors Block D's read primitives: a two-tier architecture with shard-level control
(bypassing transport for the primary write) alongside a high-level convenience that delegates to
ES's Bulk API. Both tiers were designed through extensive analysis of ES's internal write path
(Engine, IndexShard, TransportShardBulkAction, replication, indexing pressure, ingest pipelines)
and the Transform execution model.

Additionally, piescript lacked list literal syntax — lists could only be produced by `query`,
`Shard.consume`, or list builtins. Block E adds `[e1, e2, ...]` syntax to unblock `Index.bulk`
and general list construction.

### Decision

#### 1. Two-tier write architecture (mirrors read side)

| Layer | Read (Block D) | Write (Block E) |
|-------|---------------|----------------|
| High-level | `query \`FROM idx\`` (ESQL) | `Index.bulk "dest" records` (Bulk API) |
| Shard-level | `Shard.open` / `consume` / `read` | `Shard.writer` / `write` / `refresh` |
| Monitoring | — | `Shard.globalCheckpoint` |

#### 2. Shard-level primitives

- `Shard.writer : ∀r. Index r → ShardRecord → Channel (Writer r)` — acquires a write context on
  a primary shard. Validates primary + started state. Returns non-serializable `WriterVal` via
  channel (same async pattern as `Shard.open`). Must run on the node hosting the primary shard.

- `Shard.write : ∀r. Writer r → Keyword → r → WriteResult` — writes a single document to the
  primary via `IndexShard.applyIndexOperationOnPrimary()`. The `Keyword` argument is the
  document `_id` (separate from the record body — see §3). `WriteResult` is
  `{ seq_no: Double, version: Double, result: Keyword }`. Primary-only write: no replication,
  no ingest, no routing.

- `Shard.refresh : ∀r. Writer r → Channel { refreshed: Boolean }` — triggers
  `indexShard.refresh("piescript")`. Returns result via channel so the user can synchronize on
  refresh completion before reading back written docs.

- `Shard.globalCheckpoint : ∀r. Index r → ShardRecord → Double` — reads the global checkpoint
  for a shard via `indexShard.seqNoStats().getGlobalCheckpoint()`. This is the same checkpoint
  system Transforms use (`GetCheckpointAction`). Lets the user monitor replication progress.

#### 3. Document `_id` as separate argument (D-050 §5 workaround)

`Shard.write` takes `_id` as a separate `Keyword` argument rather than extracting it from the
record body. This is a workaround for the `RowType`-not-first-class-`MonoType` limitation
(D-050 deviation §5): since the type parameter `r` in `Writer r` has `Kind.TYPE` (a full record
type) instead of `Kind.ROW`, we cannot express `{ _id: Keyword | r }` — extending a row with an
additional field.

With row-kinded type parameters, the signature would be:
`Shard.write : ∀(r : Row). Writer r → { _id: Keyword | r } → WriteResult`

This is now a concrete, practical motivation for the `RowType` → `MonoType` fix — it's not just
a theoretical soundness issue but blocks natural API design.

#### 4. `Index.bulk` (high-level Bulk API)

`Index.bulk : ∀r. Keyword → List r → Channel { total: Double, written: Double, failed: Double }`

Takes an index name (string) and a list of records. Converts each `RecordVal` to JSON via
`XContentBuilder`, builds `IndexRequest`s (with optional `_id` extraction), assembles a
`BulkRequest`, and executes via `client.execute(TransportBulkAction.TYPE, ...)`. Handles routing,
replication, ingest pipelines, and index auto-creation. Result delivered via channel.

#### 5. `RecordVal` → XContent conversion

Recursive conversion from piescript `Value` to JSON for `IndexRequest` source. Handles `DoubleVal`
(whole numbers as longs for clean JSON), `KeywordVal`, `BooleanVal`, `NullVal`, `RecordVal`
(nested objects), `ListVal` (arrays). Non-convertible values (`ClosureVal`, `ChannelVal`,
`SearcherVal`, `WriterVal`, etc.) throw `EvaluationException`.

This is distinct from `ValueSerialization` (binary wire format for transport) and from
`PiescriptResponse` (XContent for REST response display). Different output targets, different
accepted value types.

#### 6. `WriterVal` — non-serializable, node-local

`WriterVal(WriterState)` holds `IndexShard` + `IndexService`. Non-serializable (throws `IOException`
on serialization attempt, same as `SearcherVal`/`DocRefVal`). `Writer r` type constructor in the
type system, registered in `Elaborator.TYPE_CONSTRUCTORS`.

#### 7. List literal syntax `[e1, e2, ...]`

New lexer tokens (`LBRACKET`, `RBRACKET`), parser rules (`EmptyList`, `ListLiteral`), and
`CoreList` Core IR node (17th variant in the `CoreExpr` sealed hierarchy). Elements are elaborated
with a shared meta for the element type — all elements must have the same type. `[]` is polymorphic
(`List ?a`). Evaluation collects elements sequentially into a `ListVal`.

#### 8. Batching: single-doc primitive, user-controlled batching

The write primitive (`Shard.write`) is single-doc. Batching is user-controlled via `List.map`:
`List.map (fn r -> Shard.write writer id r) records`. This mirrors the read side where
`Shard.consume` pulls N docs and `Shard.read` reads one doc at a time.

`List.map` with an effectful function is semantically `traverse` (effects are sequenced). This
works correctly today because the evaluator sequences effects via `SubscribableListener` chains.
Future: proper `List.traverse` / `mapM` combinator when effect tracking is explicit.

### Known bypasses (explicitly deferred)

These are intentional simplifications, not forgotten items:

1. **Replication**: Shard-level writes are primary-only. Replicas catch up via translog (ES
   background replication). User monitors via `Shard.globalCheckpoint`. Future: linearity (Phase 6)
   enforces write→replicate protocol via session types.

2. **Indexing pressure**: Shard-level writes bypass `IndexingPressure`. Future: integrate pressure
   tracking into `WriterState` lifecycle or the monadic write description.

3. **Ingest pipelines**: Shard-level writes skip ingest. `Index.bulk` runs default pipelines.
   Future: `WriteContext` surfaces pipeline handles as first-class values the user can inspect,
   selectively apply, or discard.

4. **Mapping updates**: `MAPPING_UPDATE_REQUIRED` from Engine causes failure. Future: handle
   mapping updates or require strict mappings via the write description.

5. **Monadic write description**: The full CPS/session-typed write pipeline
   (open → prepare → index → replicate → checkpoint → refresh) is future work, gated on linearity
   (Phase 6). Each step would produce a linear value consumed by the next, encoding the write
   protocol as a session type.

6. **Painless push-down for updates**: `Write.update shard id (fn doc -> ...)` where the lambda
   compiles to Painless via closure conversion (captured env → Painless parameters). Runs
   atomically under the Engine's per-document lock. Future work.

7. **Security pre-check**: `HasPrivilegesAction` during elaboration for write targets. Piescript
   programs declare index dependencies statically (`use` declarations, `Index.bulk` targets) —
   sufficient for a pre-flight privilege check before evaluation.

8. **Cross-shard coordination**: Saga-style multi-shard writes via channels. Already possible with
   existing primitives + Block E, but no built-in support. Global checkpoints enable cross-shard
   consistency verification.

### Consequences

- Piescript can now read, transform, and write data: the full ETL loop.
- The two-tier architecture gives 95% of users a simple `Index.bulk` path and 5% power users
  direct `Shard.writer`/`write` control over where and how writes happen.
- Shard-level writes bypass transport, ingest, and replication — maximum performance, maximum
  responsibility. The type system cannot yet enforce the replication protocol (requires linearity).
- The `_id`-as-separate-argument pattern concretely motivates the `RowType` → `MonoType` fix.
- List literal syntax (`[...]`) is a general-purpose addition that unblocks `Index.bulk` and
  benefits all piescript programs.

**Ref**: [Block E design + implementation](104647a1-8ee2-4796-a7b3-f13317d8d22c)

---

## D-052: Language-Integrated Query — T-LINQ-Style ESQL Compilation (Block F)

**Phase**: Block F | **Status**: accepted

### Context

The Phase 2 `query` backtick-delimited ESQL was a PoC: the ESQL text is opaque to the type system,
schema evolution through the pipeline is untracked, and there is no composability. Replacing it
with a typed, composable query surface is the next step for piescript's data access story.

The design draws from Cheney, Lindley & Wadler's *A Practical Theory of Language-Integrated Query*
(T-LINQ, ICFP 2013): query expressions are **quoted code** that gets **normalized** and **compiled**
to a backend query language. In piescript, piescript closures serve as implicit quotations (they
carry their `CoreExpr` body), and the ESQL compiler inspects `ClosureVal(body, env)` to produce
ESQL strings — using the captured environment for variable resolution, not substitution.

### Decision

#### 1. `ESQL r` type and `query ... ;` syntax

Introduce `ESQL r` as a type constructor (`TCon("ESQL")`) where `r` is a row-kinded type variable
(`Kind.ROW`). `ESQL r` represents an unevaluated ESQL query plan whose result rows have schema `r`.

The `query expr ;` syntax replaces the old `query \`ESQL text\`` backtick syntax entirely. The
`query` keyword acts as the quotation boundary (T-LINQ's `<@ @>`), and the `;` closes it. The
expression between them must have type `ESQL r`; the overall type of the `query` expression is
`List (Record r)` — the materialized result. The old backtick ESQL syntax is removed.

#### 2. 1:1 ESQL command mapping

Each piescript combinator maps to exactly one ESQL processing command. No ad-hoc pattern matching
on lambda bodies to decide which command to emit.

| Piescript | ESQL | Argument |
|---|---|---|
| `ESQL.from` | `FROM` | `Index r` |
| `ESQL.where` | `WHERE` | `(Record r -> Boolean)` |
| `ESQL.eval` | `EVAL` | `(Record r -> Record s)` |
| `ESQL.keep` | `KEEP` | `List Keyword` |
| `ESQL.drop` | `DROP` | `List Keyword` |
| `ESQL.limit` | `LIMIT` | `Double` |
| `ESQL.sort` | `SORT ASC` | `(Record r -> a)` |
| `ESQL.sortDesc` | `SORT DESC` | `(Record r -> a)` |
| `ESQL.rename` | `RENAME` | rename mapping |
| `ESQL.explain` | (debug) | returns compiled ESQL string |

#### 3. Two-type-var signatures for schema-changing combinators

Schema-preserving combinators (`where`, `limit`, `sort`) use one row type variable:
```
ESQL.where : forall (r : Row). (Record r -> Boolean) -> ESQL r -> ESQL r
```

Schema-changing combinators (`eval`, `keep`, `drop`, `rename`) use two:
```
ESQL.eval : forall (r : Row) (s : Row). (Record r -> Record s) -> ESQL r -> ESQL s
```

The output row `s` is fresh — constrained by downstream usage via open-row unification. The
relationship between `r` and `s` (subset, extension) is unencoded for now. Future work: Lacks
constraints or typeclasses to tighten the gap. Lambdas take `Record r` (a record value with
projectable fields), not bare `r` (a row schema).

#### 4. Environment-based compilation, no substitutions

The ESQL compiler takes `ClosureVal(body, env)` and compiles the `CoreExpr` body to an ESQL
expression fragment, using `env[index]` for captured variable resolution. `CoreVar(0)` is the
row parameter (compiles to field references). `CoreVar(n > 0)` looks up `env[n-1]` and inlines
the value as an ESQL literal. No de Bruijn substitution, no shifting — consistent with piescript's
evaluator design.

This works because piescript is pure: inlining captured values is semantically equivalent to
substitution. Closures inside queries are supported — they beta-reduce during evaluation (the
evaluator applies them normally), and the ESQL compiler only sees the final `ClosureVal` with
its resolved body and captured environment.

#### 5. `EsqlPlan` is separate from `Value`

`EsqlPlan` is its own sealed interface (not a `Value` variant). `Value.EsqlPlanVal(EsqlPlan plan)`
is a thin non-serializable wrapper that exists only for the evaluator's `Value[]` environment.
`EsqlPlanVal` is ephemeral — built during evaluation by `ESQL.*` builtins, compiled to an ESQL
string at the `query ... ;` boundary, then discarded. It is never serialized. If a closure
containing a `query ... ;` block is shipped to a remote node, the `CoreQueryExec` node and the
`ESQL.*` `CoreFree` nodes travel as Core IR — the plan is built at evaluation time on whichever
node runs the query.

#### 6. Evaluator-driven plan building, not a normalization pass

There is no separate post-elaboration normalization pass. The evaluator handles `ESQL.*` builtins
like any other builtins — each one receives its arguments and produces an `EsqlPlanVal`. The
`CoreQueryExec` node (from `query ... ;`) evaluates its inner expression, receives the final
`EsqlPlanVal`, compiles it to an ESQL string via `EsqlCompiler`, and fires `EsqlQueryAction`.
This reuses the evaluator's existing environment machine for let-inlining, closure capture, and
variable resolution.

#### 7. ESQL.stats deferred

`ESQL.stats` (STATS ... BY), aggregate builtins (`ESQL.count`, `ESQL.avg`, etc.), and the `Agg a`
typed aggregate descriptor design are deferred. STATS is the most complex ESQL command (optional
BY, multiple BY expressions, per-aggregate WHERE filters, computed grouping keys). The intended
direction: `Agg a` is a polymorphic opaque type representing an aggregate computation. Aggregates
are passed as a record whose field names become output column names
(`{ count: ESQL.count, avg_salary: ESQL.avg "salary" }`). Full design TBD in a dedicated session.

#### 8. Internal `LogicalPlan` compilation is future work

The MVP compiles to ESQL query strings. Compiling directly to ESQL's internal `LogicalPlan` IR
(bypassing the ESQL parser) is a future optimization that would enable: arbitrary lambda
compilation to ESQL expressions, full ESQL function coverage without per-function piescript
builtins, and deeper optimizer integration. Piescript already depends on `x-pack-esql` and
`x-pack-esql-core`, so the plan API is accessible.

### Rationale

- **T-LINQ over ad-hoc compilation**: The T-LINQ framework provides formal normalization
  guarantees. Even though piescript does runtime compilation (not elaboration-time normalization
  as in the T-LINQ paper), the same principles apply: quotation captures expression trees,
  normalization (via evaluation) reduces them, and the restricted sublanguage ensures the result
  compiles to a flat ESQL pipeline.

- **1:1 command mapping over `map`/`select` abstraction**: Directly exposing ESQL commands avoids
  ad-hoc pattern matching to decide between EVAL, KEEP, or EVAL+KEEP. The user thinks in ESQL
  terms; piescript adds types, composition, and captured variables on top.

- **Environment over substitution**: Piescript's evaluator and elaborator both use environments
  (de Bruijn indexed `Value[]` arrays), never substitution. The ESQL compiler follows the same
  pattern. This avoids the complexity and performance cost of de Bruijn shifting.

- **`EsqlPlan` separate from `Value`**: Query plans are not user-observable values. They exist
  only during evaluation, between `ESQL.from` and the `query ... ;` boundary. Keeping them
  separate from `Value` enforces this — they cannot be stored in records, sent over channels,
  or returned from programs.

- **Deferred stats**: Aggregation requires an `Agg a` type, aggregate descriptor values, and
  compilation of aggregate expressions to ESQL aggregate function calls. This is substantial
  independent work that should not block the core query surface.

#### 9. Implementation: NbE-style `Symbol(String)` (revised during implementation)

The original plan called for an `EsqlPlan` ADT + `EsqlCompiler` that walks `ClosureVal(body, env)`
Core IR. This was replaced with an NbE-style approach: `Value.Symbol(String esql)` carries compiled
ESQL fragments, built incrementally during evaluation. Closures are partially evaluated with
`Symbol("")` as the symbolic row. `CoreProject` on a `Symbol` produces `Symbol(field)`. `PrimOp`
with any `Symbol` operand compiles all operands and produces `Symbol("(left OP right)")`. No
separate plan ADT, no Core IR walking, no separate compiler — the evaluator IS the compiler.

This follows the NbE pattern: evaluate into a semantic domain (`Value` + `Symbol` for stuck terms),
where read-back into the target syntax (ESQL) happens inline as terms get stuck. It is also a free
monad description: `Symbol` accumulates a description of the ESQL computation, interpreted at the
`query ... ;` boundary.

### Consequences

- The old `query \`ESQL text\`` syntax is removed. Programs using it must migrate to `ESQL.*`
  combinators or, for ESQL features not yet covered (STATS, ENRICH, DISSECT, GROK), use the
  raw shard-level read primitives (`Shard.open`/`consume`/`read`).
- Schema evolution through the pipeline is partially tracked: the initial row type is sound (from
  `use` declarations), and downstream usage constrains fresh row variables. The `r → s` gap for
  schema-changing combinators is explicit and documented.
- The `query ... ;` syntax provides a clear compilation boundary. Programs can compose queries
  via let-bindings and pipes inside the boundary, with full type checking.
- Future ESQL features (STATS, JOIN, ENRICH) can be added as new combinators without changing the
  compilation architecture.
- `ESQL.keep`, `ESQL.drop`, and `ESQL.rename` take field names as runtime strings (`List Keyword`),
  not as typed closures. Field existence is NOT validated at elaboration time — ESQL validates at
  execution time. The typed path for column selection is `ESQL.eval` (closure-based, goes through
  the type checker). Future: row-level constraints or closure-based variants.

**Ref**: [T-LINQ design discussion](this session), [Block F plan](block_f_linq_query_e7171607)

## D-053: F-omega-lite Type System — Kinds as Types, `force` Normalizer, Row Operators, ESQL Stats

- **Context**: ESQL grouping (`STATS ... BY`) requires the output row type to be the merge of
  group keys and aggregate results. This needs row-level type computation (`s & t`), which System F
  + rows cannot express. Additionally, the `Kind` enum is a separate stratum with ad-hoc assertions
  instead of principled constraints.

- **Decision**: Extend the type system toward F-omega-lite in four incremental steps:

  1. **Kinds as types** (GHC `TypeInType`-style): Delete the `Kind` enum. Kinds become `MonoType`
     values (`TCon("Type")`, `TCon("Row")`). Arrow kinds use `MonoType.Arrow`. The same unifier
     solves kind constraints. `Prelude.KINDS` maps each built-in type constructor to its kind.
     `ElaborationContext` carries a `kindModule` parallel to `module`. `TypeAnnotations` emits
     kind constraints at type application sites.

  2. **`force` NbE normalizer**: `ElaborationState.force(MonoType)` subsumes `zonkOrKeep` — chases
     meta chains AND reduces built-in type operators. Types after `force` are in head-normal form:
     normal (TCon, Arrow, RowType), neutral/stuck (AppType with unsolved head), or reducible
     (AppType with known builtin head and concrete args).

  3. **Row operators**: `&` (merge, right-biased on overlap), `Pick` (keep fields in intersection),
     `Omit` (remove fields in intersection). All have kind `Row → Row → Row`. Reduce in `force`
     when both operands are concrete `RowType`s. `ESQL.keep` and `ESQL.drop` updated from
     `List Keyword` to closure-based API with `Pick`/`Omit` output types.

  4. **`ESQL.stats`/`ESQL.statsBy`**: Two combinators to avoid optionality of the BY clause.
     `ESQL.statsBy` output type is `ESQL (s & t)` — the merge of aggregate results and group keys.
     Aggregate builtins (`ESQL.count`, `ESQL.avg`, `ESQL.sum`, `ESQL.max`, `ESQL.min`) type with
     plain output types — no `Agg` wrapper. The aggregate/scalar distinction is value-level only
     (NbE compilation produces `Symbol` fragments). `ESQL.bucket` is a scalar grouping function.

- **Consequences**:
  - `Kind.java` deleted. All 17+ files updated from `Kind` enum to `MonoType` kinds.
  - Kind errors are caught by unification rather than runtime assertions.
  - Type-level computation is extensible — new reducible builtins can be added to `force`.
  - Aggregate builtins produce `Symbol` values where the type says `Double` — a type-level lie
    that propagates silently. Future: wrap in an ESQL expression type for static safety.

- **Alternatives considered**:
  - `Agg a` / `StripAgg` wrapper type: rejected — too much type-system complexity for the benefit.
  - PureScript-style `Union` constraint: rejected — relational/algebraic style doesn't fit;
    prefer TS-style reducible/evaluation approach with `&`/`Pick`/`Omit`.
  - Separate kind-checking pass: rejected — kinds-as-types reuses the existing unifier.

**Ref**: [F-omega plan](f-omega_type_system_09acfb27), [Implementation session](846bd5a8-3b35-4321-848a-c9b17a22f109)
