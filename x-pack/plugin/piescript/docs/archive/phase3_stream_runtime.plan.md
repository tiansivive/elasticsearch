---
name: ""
overview: ""
todos: []
isProject: false
---

# Phase 3: Stream Runtime & Plan Graph

**Parent:** `scripting_language_design_9286506e.plan.md`
**Status:** Blocked on Phase 2 + open design questions
**Dependencies:** Phase 2 (row types, index resolution, query typing)

## Goal

Data actually flows. User-defined transforms (`map`, `filter`, `fold`) execute over real ES data.
This phase introduces the **plan graph** — the architectural centerpiece that enables future
distributed execution. The evaluator builds plan graph nodes for process-level operations; the
executor interprets the plan locally (v0) but the abstraction supports distributed dispatch.

The key deliverables:

1. The `CoreProcess` IR layer (process descriptions alongside the existing `CoreExpr`) — only
  `Query` lives here; stream combinators are prelude built-ins, not IR nodes (D-016)
2. The plan graph IR (free monad over π-calculus effects)
3. A v0 local executor that runs the plan on the coordinator node
4. Stream combinators (`map`, `filter`, `fold`) as **prelude built-in functions** whose
  implementations construct plan graph nodes when applied to `StreamVal`s (D-016)
5. The mobility check — determining which lambdas can travel with plan nodes
6. Stream fan-out via DAG — using a stream multiple times creates fan-out in the plan graph,
  handled transparently by the executor (D-017)

## Vertical Slice

```bash
# With real data in logs-test index

curl -X POST "localhost:9200/_piescript/eval" \
  -d '{
    "program": "let docs = query FROM logs-test | WHERE status >= 500; in docs |> map (fn d -> { svc: d.service, msg: d.message }) |> filter (fn d -> d.svc != \"unknown\")"
  }'

# Returns columnar result: columns [svc: keyword, msg: keyword], with filtered rows
```

Also:

```bash
# Fold to aggregate
"let docs = query FROM logs-test; in docs |> fold (fn acc d -> acc + 1) 0"
# Returns: { columns: [{ name: "result", type: "integer" }], values: [[42]] }
```

## Conceptual Model

### The Evaluator/Planner Split

When the evaluator walks Core IR and encounters a `CoreProcess` node, it does not execute the
operation directly. Instead, it constructs a **plan graph node** — a data structure describing
what work should happen. The evaluator continues walking, wiring plan nodes together as it goes.

For example, given:

```
let docs = query FROM logs-* | WHERE status >= 500;
in docs |> map (fn d -> { svc: d.service }) |> filter (fn d -> d.svc != "unknown")
```

The evaluator:

1. Encounters `query` (a `CoreProcess` node) → builds `QueryPlanNode("FROM logs-* ...")`
2. Binds `docs` to `StreamVal(queryPlanNode)` in the environment
3. Encounters `map` — a **normal function application** (`map` is a prelude built-in). The
  built-in receives a lambda and a `StreamVal`, constructs `MapPlanNode(code, source)`
4. Encounters `filter` — same pattern: built-in constructs `FilterPlanNode(code, source)`
5. The final plan graph is: `QueryPlanNode → MapPlanNode → FilterPlanNode → ResultNode`

No data has moved. The plan is a description. The executor then runs it.

Note: `map` and `filter` are NOT `CoreProcess` IR nodes. They are regular prelude functions whose
runtime implementations construct plan graph nodes. The Core IR for `docs |> map f` is just
`CoreApp(CoreApp(CoreVar("map"), f), docs)` — standard function application. The special behavior
is in the built-in function value, not the IR. This keeps the IR uniform and enables a clean path
to typeclasses (`map` → `Functor.fmap`). See D-016.

### Stream Fan-Out (DAG Semantics)

Because `StreamVal` wraps a plan description (not a running computation), using a stream twice
is natural and free:

```
let s = query FROM logs-*;
let a = s |> map f;      -- MapPlanNode(f, queryNode)
let b = s |> filter g;   -- FilterPlanNode(g, queryNode)  ← same queryNode
```

The plan graph is a DAG with fan-out. No query is re-executed. No data is buffered. The executor
handles fan-out using standard techniques (Exchange operators, reference-counted Pages).
See D-017.

### The Mobility Check

Each lambda attached to a plan node is analyzed: can it travel to a remote execution context?

- **Mobile**: the lambda's body is expressible in the target context (ESQL evaluators, compute
operators). It contains only field access, primops, literals, and record construction. No
closures over complex values, no higher-order functions, no recursion.
- **Non-mobile**: the lambda uses features that require the full tree-walking interpreter (closures
over arbitrary values, higher-order functions, nested applications). These stay on the
coordinator.

For v0, the mobility check determines whether a lambda can be **compiled to an
`ExpressionEvaluator` tree** (fast, vectorized on Blocks) or must be **interpreted per-row**
(slow, but supports the full language). In future phases, "mobile" will additionally mean
"serializable and shippable to a remote data node."

### Plan Graph as Free Monad

The plan graph is a free monad over the π-calculus effect functor. Each plan node is a constructor
of the effect functor, carrying its continuation (what happens with the result):

```
QueryNode(esql, continuation: Stream -> next)
MapNode(code, source, continuation: Stream -> next)
FilterNode(code, source, continuation: Stream -> next)
FoldNode(code, init, source, continuation: Value -> next)
```

The executor is the interpreter of this free monad. v0's executor runs everything locally. A
future distributed executor dispatches plan fragments to data nodes.

## Open Questions (Must Resolve Before Implementation)

### Q3.1: Mobility check criteria — what can travel?

The mobility check determines which lambdas are compiled to `ExpressionEvaluator` trees (fast
path) vs. interpreted per-row (slow path). This is the v0 manifestation of the broader question
"can this code be serialized and sent to a remote node?"

**Criteria for mobility (v0):**

- Body contains only: `Var`, `Lit`, `PrimOp`, `Project`, `Record`, `Update`, `If`
- No free variables referencing non-primitive values (closures over other closures, streams, etc.)
- No higher-order function application (no `App` nodes except primop application)

**Sub-questions:**

- What's the exact set of `CoreExpr` nodes that pass the mobility check?
- How do we handle record construction in evaluators? ESQL evaluators produce single values, not
records. We'd need to produce multiple output columns from one plan node.
- Is there an existing pattern in ESQL for multi-column expression output?

**Status:** Needs investigation of `ExpressionEvaluator` capabilities.

### Q3.2: Plan graph representation

How is the plan graph represented in Java?

**Option A: Sealed interface hierarchy**

```java
sealed interface PlanNode {
    record QueryNode(String esql) implements PlanNode {}
    record MapNode(CoreExpr lambda, Env captures, PlanNode source) implements PlanNode {}
    record FilterNode(CoreExpr lambda, Env captures, PlanNode source) implements PlanNode {}
    record FoldNode(CoreExpr lambda, CoreExpr init, Env captures, PlanNode source)
        implements PlanNode {}
    record ResultNode(PlanNode source) implements PlanNode {}
}
```

**Option B: Extend ES `Node<T>` infrastructure**

Use ESQL's tree infrastructure for traversal, rewriting, and rule-based optimization.

**Recommend Option B** — the plan graph needs optimization passes (push-down, fusion, dead-code
elimination), and ESQL's `RuleExecutor` framework is designed for exactly this. Using `Node<T>`
gives us `transformDown`, `transformUp`, `forEachDown` for free.

**Status:** Leaning Option B, needs confirmation.

### Q3.3: StreamVal representation

How does the interpreter "hold" a stream? It needs to be a value in the environment.

**Decision (from D-012):** `StreamVal` wraps a `PlanNode`, not a runtime data structure. When the
evaluator encounters `docs |> map f`, it takes the `PlanNode` from `docs` and wraps it in a
`MapNode(f, source=existingNode)`. Plan construction is pure and cheap. Execution is deferred.

```java
record StreamVal(PlanNode planNode) implements Value {}
```

**Sub-questions:**

- What about `fold`? It consumes a stream and produces a scalar value. The evaluator encounters
`fold` and must return a `Value`, not a `StreamVal`. This means `fold` triggers plan execution
— it is the "terminal operation" that forces the plan to run.
- Similarly, if the program's top-level result is a `StreamVal`, the transport layer forces
execution to produce response pages.

**Status:** Settled in principle; implementation details depend on Q3.2.

### Q3.4: Query delegation — PlanExecutor vs client.execute

**Option A: Keep using client.execute.** Parse the response into Pages. Simple but involves
serialization/deserialization overhead.

**Option B: Use PlanExecutor directly.** Get raw Pages without serialization. More efficient but
more coupling to ESQL internals.

**Recommend starting with Option A** (reuse Phase 0's delegation), and optimize to Option B if
performance matters. The plan graph abstraction makes this a swap inside the executor, not an
architectural change.

**Status:** Option A for now, optimize later.

## Tasks (Ordered by Dependency)

### T3.1: Design `CoreProcess` IR nodes

Define the `CoreProcess` hierarchy. Per D-016, only nodes with **dedicated surface syntax** are
`CoreProcess` nodes. For Phase 3, this is just `Query`:

```java
sealed interface CoreProcess permits CoreQuery {
    record CoreQuery(String esql, MonoType type) implements CoreProcess {}
}
```

`map`, `filter`, `fold` are NOT `CoreProcess` nodes — they are prelude built-in functions.
The `CoreExpr` / `CoreProcess` boundary is clean: `CoreQuery` is the only process node in
Phase 3. Phase 4 adds `CorePar`.

**Depends on:** Phase 2 complete
**Blocks:** T3.2, T3.3

### T3.2: Design plan graph IR

Define the plan graph node types. Decide on `Node<T>` vs sealed interface (Q3.2). Each plan node
carries:

- Typed edges (source stream, output stream)
- Code (optional `CoreExpr` + captured `Env`)
- Mobility annotation (mobile / non-mobile, from the mobility check)

**Depends on:** T3.1
**Blocks:** T3.3, T3.4

### T3.3: Implement the evaluator/planner split and prelude built-ins

Extend the tree-walking evaluator in two ways:

1. `**CoreProcess` handling**: when the evaluator encounters a `CoreQuery` node, it builds a
  `QueryPlanNode` and returns `StreamVal(planNode)`.
2. **Prelude built-in functions**: register `map`, `filter`, `fold` as built-in function values
  in the initial environment. When applied:
  - `map(lambda, StreamVal(node))` → `StreamVal(MapPlanNode(lambda, captures, node))`
  - `filter(lambda, StreamVal(node))` → `StreamVal(FilterPlanNode(lambda, captures, node))`
  - `fold(lambda, init, StreamVal(node))` → triggers plan execution (terminal operation),
  returns concrete `Value`

The evaluator returns `StreamVal(planNode)` for non-terminal stream operations and a concrete
`Value` for terminal operations (fold).

The built-in functions are curried: `map` takes a lambda, returns a function that takes a
`StreamVal`. This is consistent with the pipe syntax: `stream |> map f` desugars to
`(map f) stream`.

**Depends on:** T3.1, T3.2
**Blocks:** T3.5

### T3.4: Implement the mobility check

Analyze each lambda attached to a plan node:

- Walk the `CoreExpr` tree, checking that all nodes are in the mobile subset
- Check that all free variables resolve to serializable values in the captured environment
- Annotate the plan node with the result (mobile / non-mobile)

v0: "mobile" means "compilable to `ExpressionEvaluator`." Future: "mobile" means "serializable
and shippable to a remote node."

**Depends on:** T3.2
**Blocks:** T3.5

### T3.5: Implement the v0 local executor

The executor takes a plan graph and runs it on the coordinator node:

- `QueryPlanNode` → fire `EsqlQueryRequest` via `client.execute()`, wrap result pages
- `MapPlanNode` (mobile lambda) → compile lambda to `ExpressionEvaluator`, apply to each Page
- `MapPlanNode` (non-mobile lambda) → unpack rows, apply interpreter lambda per-row, repack
- `FilterPlanNode` (mobile) → compile predicate to boolean evaluator, apply as selection mask
- `FilterPlanNode` (non-mobile) → per-row evaluation, construct filtered Pages
- `FoldPlanNode` → consume stream, accumulate via interpreter (fold is always interpreter-level)

**Depends on:** T3.3, T3.4

### T3.6: Implement Core IR to ExpressionEvaluator compiler

Translate mobile `CoreExpr` lambdas to `ExpressionEvaluator` trees:

- `Project(field)` → `FieldAt(columnIndex(field))`
- `PrimOp(+, [a, b])` → `AddIntsEvaluator(compile(a), compile(b))`
- `Lit(value)` → constant evaluator
- Record construction → multiple output columns

This is the fast path. It runs vectorized on `Block`s.

**Depends on:** T3.4 (needs the mobility check to know which lambdas to compile)

### T3.7: Result serialization

Convert the final plan graph output to a REST response:

- If the result is a `StreamVal`: force execution, serialize output Pages to columnar JSON
- If the result is a scalar `Value` (from fold): use Phase 1's scalar serialization

**Depends on:** T3.5

### T3.8: Integration tests

- **Simple map:** `query | map .field` returns single column
- **Record construction:** `query | map (fn d -> { a: d.x, b: d.y })` returns multi-column
- **Filter:** `query | filter (fn d -> d.x > 0)` returns filtered rows
- **Fold:** `query | fold (fn acc _ -> acc + 1) 0` returns count
- **Chained:** `query | filter ... | map ... | fold ...` — full pipeline
- **Large data:** Verify pagination / streaming works for large result sets
- **Non-mobile lambda fallback:** Lambda with closure falls back to per-row interpreter
- **Stream fan-out:** `let s = query ...; in (s |> map f, s |> filter g)` — both use the same
source, plan graph is a DAG, query executes once (D-017)
- **Pure program (no process nodes):** `let x = 1 + 2 in x` — no plan graph built, evaluator
returns value directly

**Depends on:** T3.5, T3.6, T3.7

## Implications for Other Phases

- **Phase 1**: The `CoreExpr` / `CoreProcess` boundary must be designed into the Core IR from
Phase 1. Even though Phase 1 only has `CoreExpr` nodes, the sealed interface hierarchy should
have a clear seam where `CoreProcess` will be added.
- **Phase 4**: `par` blocks become plan graph composition. The plan graph IR must support parallel
branches with independent subgraphs joined at a synchronization point (fan-out and fan-in).
Phase 3's `PlanNode` hierarchy must be extensible to include `ParPlanNode`.
- **Typeclasses (v1)**: because `map`/`filter`/`fold` are prelude functions (D-016), the
migration to typeclasses is purely additive. `map` becomes `Functor.fmap` specialized to
`Stream`. No IR changes needed.
- **Linearity (Phase 6+)**: streams are unrestricted (D-017, D-018). The plan graph DAG handles
fan-out. Linearity is introduced for channel endpoints only. The function type representation
should leave room for multiplicity annotations (`A →_π B`).
- **Future distributed executor**: The plan graph and mobility check are the foundations. The
distributed executor dispatches mobile plan fragments to data nodes. The v0 local executor is
the reference implementation.

## Estimated Scope

Large. The plan graph IR (T3.2) and the evaluator/planner split (T3.3) are the most architecturally
significant tasks. The ExpressionEvaluator compiler (T3.6) is the most technically challenging
(requires deep understanding of ESQL's evaluator infrastructure). Expect ~20–35 files.