# Current State

> **Living doc** — update after every implementation session. This is the ground truth for "what
> exists right now."
>
> **Last updated**: 2026-03-11 (session 2)

## Summary

**Phase 0 is complete. Phase 1a (Parser) is complete. Phase 1b (Type System + Elaborator) is
complete.** The piescript plugin has a working ESQL passthrough from Phase 0. Phase 1a is done: the
ANTLR lexer and parser grammars implement the full D1.17 surface syntax, the `PiescriptParser`
entry point produces parse trees, comprehensive parser unit tests cover every syntax form, and a
dev endpoint (`POST /_piescript/dev`) exposes the CST for inspection. Phase 1b is complete:
type data structures (T1b.1), Core IR node types (T1b.2), elaboration state (T1b.3),
unification (T1b.4), the elaborator (T1b.5), elaborator tests (T1b.6), and the dev endpoint
wired to the elaborator (T1b.7) are all implemented. The dev endpoint now returns the CST,
elaborated Core IR, and resolved type for any program.
Phase 1c (Interpreter + Wiring) is next.

## What Works

| Capability | Details |
|-----------|---------|
| REST endpoint | `POST /_piescript/eval` accepts `{"program": "..."}` |
| Dev endpoint | `POST /_piescript/dev` returns CST (`tree`), elaborated Core IR (`core`), and resolved type (`type`). Parse errors return `parse_error`; type errors return `tree` + `type_error`. |
| ESQL passthrough | `query FROM idx \| WHERE x > 1 \| LIMIT 10;` executes as ESQL |
| Request validation | Empty/blank programs rejected with 400 |
| Program structure validation | Missing `query` prefix or `;` suffix rejected with 400 |
| Security | RBAC authorization via `shouldAuthorizeIndexActionNameOnly()`, operator privileges allowlist |
| Integration tests | 6 passing test cases covering happy path, filters, invalid ESQL, empty program, missing prefix, missing field |
| Build | Compiles, passes `check`, `spotlessJavaCheck`, `javaRestTest` |
| ANTLR grammar | Lexer (`PiescriptLexer.g4`) and parser (`PiescriptAntlrParser.g4`) implementing full D1.17 surface syntax |
| Parser entry point | `PiescriptParser.java` — invokes ANTLR, produces parse tree; `parseToTreeString()` for CST inspection |
| Core IR printer | `CorePrinter` in `piescript.core`: pretty-prints Core IR expressions and types for dev/debug output |
| Parser unit tests | `PiescriptParserTests.java` — comprehensive coverage of every syntax form plus error cases |
| Type data structures (Phase 1b) | `Kind`, `MonoType`, `RowType`, `TypeScheme`, `LitVal`, `Op` in `piescript.types` package |
| Core IR (Phase 1b) | `CoreExpr` sealed hierarchy in `piescript.core`: `CoreVar`, `CoreLit`, `CoreLam`, `CoreApp`, `CoreLet`, `CoreRecord`, `CoreProject`, `CoreUpdate`, `CorePrimOp` — extends `Node<CoreExpr>` with `MonoType` on every node |
| Type unit tests (Phase 1b) | `TypeDataStructureTests.java` — construction, equality, sealed hierarchy, factory methods |
| Core IR unit tests (Phase 1b) | `CoreExprTests.java` — construction, accessors, equality, replaceChildren, tree traversal |
| Elaboration context (Phase 1b) | Immutable `ElaborationContext` in `piescript.elab`: typing context (Γ) + binding level, passed by value through recursive descent. `lookup()` returns `Optional`. |
| Elaboration state (Phase 1b) | Mutable `ElaborationState` in `piescript.elab`: metavar supply + zonker with chain resolution. `resolve()` returns `Optional`. |
| Elaboration unit tests (Phase 1b) | `ElaborationContextTests.java` (immutability, de Bruijn indexing, shadowing, scope unwinding) + `ElaborationStateTests.java` (metas, zonker, integrated let-polymorphism workflow) |
| Unification (Phase 1b) | `Unifier` in `piescript.elab`: Robinson unification with occurs check, null-as-bottom (D1.11), closed-row field matching. Returns `Optional<TypeError>`. |
| Type errors (Phase 1b) | `TypeError` sealed interface: `Mismatch`, `InfiniteType`, `FieldMismatch`, `MissingFields` |
| Unification unit tests (Phase 1b) | `UnifierTests.java` — meta solving, transitive chains, occurs check, null-as-bottom, arrow/record/app structural matching, cross-form mismatch |
| Elaborator (Phase 1b) | `Elaborator` in `piescript.elab`: pattern-matching recursive descent over ANTLR CST → Core IR. Bidirectional HM type inference, binding-level generalization, instantiation, primops as typed functions (concrete Integer-only signatures, D-020), desugaring (multi-param lambda, pipe, accessor, update sugar, blocks, top-level bindings). |
| Type walker (Phase 1b) | `TypeWalker` in `piescript.elab`: static type-level traversal utilities — generalization, instantiation, deep resolution, meta collection. Extracted from `Elaborator` for clarity. Public (`resolveDeep` used by `CorePrinter`). |
| Elaboration exception (Phase 1b) | `ElaborationException`: unchecked, fail-fast, wraps source location + optional `TypeError`. |
| Elaborator tests (Phase 1b) | `ElaboratorTests.java` — 68 tests covering: literals (int, long, decimal, string, boolean, null), let-bindings (basic, annotated, nested, shadowing, top-level), lambdas (identity, typed, multi-param), application (direct, inference), let-polymorphism, arithmetic/comparison/boolean operators, unary ops, records (empty, literal, projection, update, field addition), pipe operator, accessor sugar, update sugar, blocks, parentheses, ascription, de Bruijn indexing, and error cases (unbound variable, type mismatch, non-function application, duplicate field, projection on non-record, missing field, unknown type, if/then/else unsupported). |

## What Does Not Exist Yet

| Capability | Target Phase | Notes |
|-----------|-------------|-------|
| Evaluator | 1c | No tree-walking interpreter |
| Transport pipeline wiring | 1c | Parser not yet wired into transport action; still uses Phase 0 string-stripping |
| Piescript-specific response format | 1c+ | Returns raw `EsqlQueryResponse` |
| Pattern matching | 1d | No match expressions |
| Async query support | 3 | Only synchronous `syncEsqlQueryRequest` |
| Feature flag / license gating | TBD | No gating mechanism |

## Known Limitations and Shortcuts

These are intentional simplifications from Phase 0 that will need attention:

1. **No semicolon handling in queries.** `extractEsqlQuery()` splits on the last `;`. If the ESQL
   query itself contains a semicolon (e.g., in a string literal), it will break. This is fine for
   Phase 0 because the `query ... ;` wrapper is temporary syntax.

2. **No `PiescriptResponse` wrapper.** The transport action returns `EsqlQueryResponse` directly.
   When piescript evaluates non-query expressions (e.g., `let x = 1 + 2 in x`), a custom response
   type will be needed.

3. **`DIRECT_EXECUTOR_SERVICE`.** The transport action runs on the calling thread. This is correct
   because ESQL manages its own executor, but when piescript adds its own computation (type checking,
   evaluation), a dedicated thread pool may be needed.

4. **Flat package structure.** All classes are in `org.elasticsearch.xpack.piescript` (except
   `parser/`, `types/`, `core/`, and `elab/` sub-packages). Phase 1c will add `eval`.

5. **No backwards compatibility versioning.** `PiescriptRequest` does not use `TransportVersion`
   checks because the protocol is trivial (single string). Phase 1's richer request format will
   need versioned serialization.

## Immediate Next Steps

Phase 1b is complete (T1b.1–T1b.7 all done). The next phase is **Phase 1c: Interpreter + Wiring** —
runtime value types, tree-walking evaluator, transport action integration, and the vertical slice
working end-to-end via REST.

**Deferred elaborator tests** (tracked in T1c.5): occurs check (`fn x -> x x`), cross-type
arithmetic rejection (`"hello" + 1`, `3.14 + 1`), lambda applied to wrong type, update sugar
applied to record.

Review:

- [roadmap.md](roadmap.md) for the full Phase 1 task breakdown
- [decisions.md](decisions.md) for type system and grammar decisions already made
- [Phase 1 plan](../../.cursor/plans/phase1_expression_language.plan.md) for detailed task
  descriptions, typing rules, Java type definitions, and design rationale
- [Phase 1 discussion](3cd2a822-792c-4179-a00e-0ba98b875f52) for design notes on grammar,
  typing rules, and node infrastructure
