# Current State

> **Living doc** — update after every implementation session. This is the ground truth for "what
> exists right now."
>
> **Last updated**: 2026-03-10

## Summary

**Phase 0 is complete. Phase 1a (Parser) is complete. Phase 1b (Type System + Elaborator) is in
progress.** The piescript plugin has a working ESQL passthrough from Phase 0. Phase 1a is done: the
ANTLR lexer and parser grammars implement the full D1.17 surface syntax, the `PiescriptParser`
entry point produces parse trees, comprehensive parser unit tests cover every syntax form, and a
dev endpoint (`POST /_piescript/dev`) exposes the CST for inspection. Phase 1b has begun: type data structures (T1b.1) and Core IR node types (T1b.2) are implemented.
Elaboration state, unification, and the elaborator itself do not exist yet.

## What Works

| Capability | Details |
|-----------|---------|
| REST endpoint | `POST /_piescript/eval` accepts `{"program": "..."}` |
| Dev endpoint | `POST /_piescript/dev` returns LISP-style CST for parser inspection |
| ESQL passthrough | `query FROM idx \| WHERE x > 1 \| LIMIT 10;` executes as ESQL |
| Request validation | Empty/blank programs rejected with 400 |
| Program structure validation | Missing `query` prefix or `;` suffix rejected with 400 |
| Security | RBAC authorization via `shouldAuthorizeIndexActionNameOnly()`, operator privileges allowlist |
| Integration tests | 6 passing test cases covering happy path, filters, invalid ESQL, empty program, missing prefix, missing field |
| Build | Compiles, passes `check`, `spotlessJavaCheck`, `javaRestTest` |
| ANTLR grammar | Lexer (`PiescriptLexer.g4`) and parser (`PiescriptAntlrParser.g4`) implementing full D1.17 surface syntax |
| Parser entry point | `PiescriptParser.java` — invokes ANTLR, produces parse tree; `parseToTreeString()` for CST inspection |
| Parser unit tests | `PiescriptParserTests.java` — comprehensive coverage of every syntax form plus error cases |
| Type data structures (Phase 1b) | `Kind`, `MonoType`, `RowType`, `TypeScheme`, `LitVal`, `Op` in `piescript.types` package |
| Core IR (Phase 1b) | `CoreExpr` sealed hierarchy in `piescript.core`: `CoreVar`, `CoreLit`, `CoreLam`, `CoreApp`, `CoreLet`, `CoreRecord`, `CoreProject`, `CoreUpdate`, `CorePrimOp` — extends `Node<CoreExpr>` with `MonoType` on every node |
| Type unit tests (Phase 1b) | `TypeDataStructureTests.java` — construction, equality, sealed hierarchy, factory methods |
| Core IR unit tests (Phase 1b) | `CoreExprTests.java` — construction, accessors, equality, replaceChildren, tree traversal |

## What Does Not Exist Yet

| Capability | Target Phase | Notes |
|-----------|-------------|-------|
| Elaboration state | 1b | No context, metavar supply, binding level, zonker |
| Unification | 1b | No Robinson unification, no occurs check |
| Elaborator | 1b | No CST → Core IR pass, no type inference, no desugaring |
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
   `parser/` sub-package). Phase 1b will introduce sub-packages (`types`, `core`), and Phase 1c
   adds `eval`.

5. **No backwards compatibility versioning.** `PiescriptRequest` does not use `TransportVersion`
   checks because the protocol is trivial (single string). Phase 1's richer request format will
   need versioned serialization.

## Immediate Next Steps

Phase 1b is in progress. T1b.1 (type data structures) and T1b.2 (Core IR) are complete. The next
task is **T1b.3: Implement elaboration state** — context, metavar supply, binding level, zonker.
After that, T1b.4 (unification) and T1b.5 (the elaborator itself). Review:

- [roadmap.md](roadmap.md) for the full Phase 1 task breakdown
- [decisions.md](decisions.md) for type system and grammar decisions already made
- [Phase 1 plan](../../.cursor/plans/phase1_expression_language.plan.md) for detailed task
  descriptions, typing rules, Java type definitions, and design rationale
- [Phase 1 discussion](3cd2a822-792c-4179-a00e-0ba98b875f52) for design notes on grammar,
  typing rules, and node infrastructure
