---
name: ""
overview: ""
todos: []
isProject: false
---

# Phase 4: Process Primitives & Plan Composition

**Parent:** `scripting_language_design_9286506e.plan.md`
**Status:** Blocked on Phase 3 + open design questions
**Dependencies:** Phase 3 (plan graph IR, stream combinators, v0 executor)

## Goal

Orchestration via plan composition. Multiple queries run concurrently via `par` blocks, expressed
as parallel branches in the plan graph. This is the first real use of the π-calculus foundations —
parallel composition of processes communicating over channels (plan graph edges).

The key deliverables:

1. `Par` as a `CoreProcess` node and corresponding `ParPlanNode` in the plan graph
2. Plan-level optimization of `par` blocks (dead-branch elimination, push-down into branches)
3. Async execution of parallel plan branches via the v0 local executor
4. Foundation for future explicit channel primitives (`new`, `send`, `recv`)

## Vertical Slice

```bash
curl -X POST "localhost:9200/_piescript/eval" \
  -d '{
    "program": "par { let errors = query FROM logs-* | WHERE status >= 500; let metrics = query FROM metrics-*; } in errors |> map .message"
  }'

# Both queries dispatch concurrently in the plan. Optimizer eliminates the unused `metrics` branch.
# Result uses only `errors`.
```

Also (both results used):

```bash
"par {
  let errors = query FROM logs-* | WHERE status >= 500;
  let requests = query FROM logs-* | WHERE status < 500;
} in {
  let error_count = errors |> fold (fn acc _ -> acc + 1) 0;
  let request_count = requests |> fold (fn acc _ -> acc + 1) 0;
  { errors: error_count, requests: request_count }
}"
```

## Conceptual Model

### `par` as Plan Composition

Under the plan graph model (Phase 3, D-012), `par` does not fire async queries directly. It
builds a **parallel plan node** — a plan node with multiple independent branches that the
executor may dispatch concurrently.

```
par {
  let errors = query FROM logs-* | WHERE status >= 500;
  let metrics = query FROM metrics-*;
} in errors |> map .message
```

The evaluator builds:

```
ParPlanNode(
  branches = [
    ("errors",  QueryNode("FROM logs-* | WHERE status >= 500")),
    ("metrics", QueryNode("FROM metrics-*"))
  ],
  continuation = MapNode(code=.message, source=Ref("errors"))
)
```

### Channels as Plan Edges

In the plan graph, every data flow between nodes is a **channel** — a typed edge carrying a
stream of values. For v0, channels are implicit (the user names bindings, not channels). The
binding name in a `par` block (`errors`, `metrics`) is the channel name.

```
QueryNode("FROM logs-*") --[channel: errors]--> MapNode(.message) --[output]--> ResultNode
QueryNode("FROM metrics-*") --[channel: metrics]--> (unused)
```

In the π-calculus:

```
(ν c_errors)(ν c_metrics)(
    query1.send(c_errors, result1)
  | query2.send(c_metrics, result2)
  | recv(c_errors, errors).MapNode(.message, errors).ResultNode
)
```

The user writes `par` with named bindings. The plan graph has typed channel edges. The executor
implements channels as in-memory data flow (v0) or cross-node exchanges (future). The user
never sees channels directly in v0.

### Plan-Level Optimization of `par`

Because `par` builds a plan node (not fires queries), the optimizer can inspect and transform it
before execution:

- **Dead-branch elimination**: if `metrics` is never referenced in the continuation, remove the
entire `QueryNode("FROM metrics-*")` branch. No wasted I/O.
- **Push-down into branches**: if the continuation applies `map .message` to `errors`, the
optimizer can push the `MapNode` into the `errors` branch, potentially fusing it into the
`QueryNode` as an ESQL `KEEP` clause.
- **Branch merging**: if two branches query the same index pattern with different filters, the
optimizer could (future) merge them into a single query with a discriminating column.

None of these optimizations are possible if `par` executes immediately (the current async
coordination model). They require seeing the full plan structure first.

## Open Questions (Must Resolve Before Implementation)

### Q4.1: `par` block typing rules

**Decision: Option A — `par` as a binding form (not an expression)**

`par { let x = e1; let y = e2; } in body` is a binding form that introduces `x` and `y` into the
scope of `body`. The `par` block itself doesn't have a "return type" — it runs the enclosed
bindings concurrently and makes them available.

```
typing:  Γ ⊢ e1 ⇒ t1, ..., Γ ⊢ en ⇒ tn
         Γ, x1:t1, ..., xn:tn ⊢ body ⇒ t_body
         ------------------------------------------
         Γ ⊢ par { let x1 = e1; ...; let xn = en; } in body ⇒ t_body
```

Simple, natural scoping. `par` is "concurrent let." Option B (returning a record) can be added
later if needed.

**Status:** Confirmed.

### Q4.2: Scope and visibility of `par` bindings

**Decision: Option A — No cross-references.**

All bindings in a `par` block are independent. They can only reference the outer scope. If `e2`
depends on `x`, it cannot be parallelized. Use sequential `let` for dependencies.

This aligns with the plan graph model: each branch is an independent subgraph. Cross-references
would create edges between branches, defeating the purpose of parallel dispatch.

**Status:** Confirmed.

### Q4.3: What can appear inside a `par` block?

**Decision: Option A for v0 — Only query-producing expressions.**

`par { let x = query ...; let y = query ...; }`. The value proposition of `par` is parallel
queries. Pure computation doesn't benefit from parallelism on a single coordinator.

In the plan graph, this means each branch must have a `QueryPlanNode` as its root (possibly with
downstream stream combinators). Branches that don't contain queries are rejected at typecheck
time.

Future: allow arbitrary process expressions (including nested `par`, stream transforms, etc.)
once the distributed executor can dispatch non-query work to remote nodes.

**Status:** Confirmed.

### Q4.4: Executor strategy for `par`

**Decision: Option C — Async composition via ActionListeners.**

The v0 local executor dispatches each branch of a `ParPlanNode` as an independent async
operation. Each branch fires its query via `client.execute()` (returns via `ActionListener`).
The executor collects all listeners and joins (with timeout). On success, all branch results
are bound in the environment and the continuation is evaluated.

This is an **executor concern**, not a language concern. The plan graph describes the parallel
structure; the executor chooses the dispatch strategy. Future executors could use thread pools,
compute Drivers, or network dispatch to remote nodes — the plan graph does not change.

**Status:** Confirmed.

### Q4.5: Channels — implicit or explicit?

**Decision: Option A for v0 — Implicit channels.**

`par` internally creates channels (plan graph edges) for each binding. The user never sees
channel types or operations. Channels are an implementation detail of the plan graph.

In the plan graph, channels are typed edges between nodes. The type is the stream element type.
The executor implements them as in-memory data flow (v0) or cross-node exchanges (future).

Future: expose channel types and `new`/`send`/`recv` operations for advanced orchestration
(fan-out, dynamic routing, producer-consumer). This will require session types for safety
(see [references.md § Session Types](../docs/references.md)). The implicit channel model is
a stepping stone — the underlying plan graph already has typed edges, so adding user-visible
channel syntax is a language-level change, not an architectural one.

**Status:** Confirmed.

### Q4.6: Error semantics for `par`

**Decision: Option A — Fail-fast.**

If one branch of a `par` block fails, the executor cancels all other branches and propagates the
error. The `par` block produces an error. Consistent with v0's "no partial results" policy.

This is an executor-level policy. The plan graph is annotated with the error strategy (fail-fast
for v0). Future executors could support independent failure (each branch succeeds or fails
independently, producing error values for failed branches).

**Status:** Confirmed.

## Tasks (Ordered by Dependency)

### T4.1: Add `Par` to `CoreProcess` IR

Define `Par` as a `CoreProcess` node:

```java
record Par(List<Binding> bindings, CoreExpr body) implements CoreProcess {
    record Binding(String name, CoreProcess expr) {}
}
```

Each binding's expression must be a `CoreProcess` (query or stream pipeline). The body is a
`CoreExpr` that references the bound names.

**Depends on:** Phase 3 (CoreProcess hierarchy exists)
**Blocks:** T4.2, T4.3

### T4.2: Add `Par` to ANTLR grammar

Flesh out the `par` block grammar production if not already present from Phase 1's stub:

```
parExpr : PAR LBRACE parBinding+ RBRACE IN expr ;
parBinding : LET IDENT EQ processExpr SEMI ;
```

**Depends on:** T4.1

### T4.3: Add `Par` typing rule to elaborator

Implement the typing rule from Q4.1:

- All bindings are typed independently (no cross-references — Q4.2)
- Each binding's expression must produce a `Stream` type (Q4.3 restriction)
- The body is typed with all bindings in scope

**Depends on:** T4.1, T4.2

### T4.4: Add `ParPlanNode` to plan graph

When the evaluator encounters a `Par` CoreProcess node:

1. For each binding, recursively plan the binding's expression → produces a plan subgraph
2. Construct a `ParPlanNode` with the list of `(name, planSubgraph)` pairs
3. Evaluate the body with each name bound to `StreamVal(branchPlanNode)`
4. The body evaluation produces the continuation plan (or a terminal value for fold)

**Depends on:** T4.1, Phase 3 (plan graph infrastructure)

### T4.5: Implement plan optimizer passes for `par`

- **Dead-branch elimination**: walk the continuation, collect referenced names, prune
unreferenced branches from the `ParPlanNode`.
- **Push-down into branches**: if the continuation applies stream combinators to a branch
binding, move those combinators into the branch subgraph (enables further push-down into
the QueryNode).

Use ESQL's `RuleExecutor` framework for these optimization passes.

**Depends on:** T4.4

### T4.6: Implement `par` execution in the v0 local executor

When the executor encounters a `ParPlanNode`:

1. For each branch, create an async execution context (ActionListener)
2. Execute each branch's plan subgraph concurrently
3. On all-success: provide branch results to the continuation execution
4. On any-failure: cancel remaining branches, propagate error (Q4.6 fail-fast)
5. Timeout: configurable, fail if exceeded

**Depends on:** T4.4, T4.5

### T4.7: Integration tests

- **Two parallel queries, both used:** Both succeed, both results available in body
- **Two parallel queries, one unused:** Optimizer eliminates unused branch; no wasted I/O
- **One fails:** Error propagates, other is cancelled
- **Performance:** Verify parallel execution is actually concurrent (timing-based test)
- **Scope:** Bindings from `par` are available in the body but not cross-referenced within `par`
- **Push-down:** `par { let x = query ...; } in x |> map .field` — map pushed into query
- **Nested par:** Probably disallowed for v0; test that it's rejected

**Depends on:** T4.6

## Implications for Master Plan

- Q4.1 decision finalizes Section 6.3 (Processes and Channels) — simplified for v0 to "parallel
bindings with implicit channels."
- Q4.5 decision (implicit channels) means explicit channel primitives (`new`, `send`, `recv`)
are deferred to post-v0. The plan graph already has typed edges, so adding explicit channels
is a language-level extension, not an architectural change.
- The `Process` type constructor may not be needed for v0 if channels are implicit and `query`
returns `Stream` directly (the plan graph handles the process structure internally).

## Future Work (Post-v0)

- **Explicit channels**: `new`, `send`, `recv` as language primitives with channel types. These
  map to new `CoreProcess` nodes and corresponding plan graph nodes. Session types (Honda et al.)
  provide the typing discipline. See [references.md](../docs/references.md).
- **QTT multiplicities for channels (D-018)**: channel endpoints are linear (multiplicity 1),
  enforced by the type system. This enables session type protocol safety and deadlock-freedom.
  Streams remain unrestricted (ω). Linear closures captured by plan nodes can be moved instead of
  cloned (zero-copy optimization). See [references.md § Linear Haskell, QTT](../docs/references.md).
- **Producer-consumer patterns**: one query feeds into another's filter via an explicit channel.
  In the plan graph, this is an edge from one branch to another within a `par` block.
- **Join patterns**: a plan node that fires when messages arrive on multiple channels (join
  calculus). Natural extension of `par` for multi-way synchronization.
- **Location-aware dispatch**: plan nodes annotated with data locality hints. The distributed
  executor uses these to co-locate computation with data.
- **Dynamic fan-out**: `par` over a computed list of queries. Requires the plan graph to support
  dynamic branch counts (either at plan construction time or via a runtime expansion step).
- **Merge combinator**: combine streams from `par` branches with interleaving or conflict
  resolution semantics.

## Estimated Scope

Small-medium. The plan graph infrastructure from Phase 3 does the heavy lifting. Phase 4 adds
`ParPlanNode`, the optimizer passes (dead-branch elimination, push-down), and the async
executor logic. The typing rule is simple. Expect ~10–15 files.