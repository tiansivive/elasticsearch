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
- `spawn expr` — launch `expr` asynchronously, return a channel (`Chan τ`) that will carry the
  result. Implementation: fork to `threadPool.executor(GENERIC)`, write result to a
  `SubscribableListener<Value>`.
- `join (c₁ x₁) & (c₂ x₂) & ... -> body` — synchronize on one or more channels. When all
  specified channels have delivered values, bind each value to its variable and evaluate `body`.
  Implementation: compose `SubscribableListener` callbacks; for n-ary joins, use
  `GroupedActionListener` to collect all results before firing.
- `Chan τ` — a typed channel. Block A: single-value (future-like), backed by
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
- **Block A**: `spawn` + single-value `join`. Channels are `SubscribableListener<Value>`. Queries
  complete fully before delivering results. Evaluator becomes async.
- **Block B**: Multi-value channels. `newchan`, `send` primitives. Lightweight concurrent queue.
  Join automaton for pattern matching over streaming values.
- **Block C**: `writeTo` sink + scheduler. Persistence and scheduled execution.
- **Block D**: Push-down compilation. Core IR → ESQL text compiler. Mobility analysis.
- **Block E**: Exchange integration. Piescript in ESQL's streaming pipeline.

**Rationale**:
- The Join Calculus primitives map directly to ES infrastructure (`SubscribableListener` for
  single-value channels, `GroupedActionListener` for n-ary synchronization,
  `threadPool.executor(GENERIC)` for spawn). No new distributed infrastructure is needed for v0.
- The evaluator-as-interpreter model is simpler than evaluator + planner + executor. The plan
  graph indirection provided optimization hooks that require significant compiler work to exploit
  — work that is better scoped as a dedicated Block (D) rather than a prerequisite for basic
  concurrency.
- `spawn` + `join` are strictly more expressive than `par`: any `par` block can be expressed as
  spawns + a join, but joins also support multi-way synchronization, streaming coordination, and
  reaction rules that `par` cannot express.
- The theoretical foundation (Join Calculus) guarantees that all coordination patterns have
  efficient distributed implementations, future-proofing the design for cross-node execution.

**Ref**: [Join Calculus redesign](f54fd3b6-dcf8-4af9-9af0-6a33818de6ef),
[references.md § Join Calculus](references.md),
[references.md § Sangiorgi (agent-passing)](references.md)
