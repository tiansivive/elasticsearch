# Current State

> **Living doc** — update after every implementation session. This is the ground truth for "what
> exists right now."
>
> **Last updated**: 2026-03-10

## Summary

**Phase 0 is complete. Phase 1a (Parser) is in progress.** The piescript plugin has a working ESQL
passthrough from Phase 0. Phase 1a has begun: the ANTLR lexer and parser grammars are written,
the `PiescriptParser` entry point exists, and parser unit tests are in place. Type checking,
elaboration, and evaluation do not exist yet.

## What Works

| Capability | Details |
|-----------|---------|
| REST endpoint | `POST /_piescript/eval` accepts `{"program": "..."}` |
| ESQL passthrough | `query FROM idx \| WHERE x > 1 \| LIMIT 10;` executes as ESQL |
| Request validation | Empty/blank programs rejected with 400 |
| Program structure validation | Missing `query` prefix or `;` suffix rejected with 400 |
| Security | RBAC authorization via `shouldAuthorizeIndexActionNameOnly()`, operator privileges allowlist |
| Integration tests | 6 passing test cases covering happy path, filters, invalid ESQL, empty program, missing prefix, missing field |
| Build | Compiles, passes `check`, `spotlessJavaCheck`, `javaRestTest` |
| ANTLR grammar (Phase 1a) | Lexer (`PiescriptLexer.g4`) and parser (`PiescriptAntlrParser.g4`) grammars implementing D1.17 surface syntax |
| Parser entry point (Phase 1a) | `PiescriptParser.java` — invokes ANTLR, produces parse tree |
| Parser unit tests (Phase 1a) | `PiescriptParserTests.java` — tests each syntax form |

## What Does Not Exist Yet

| Capability | Target Phase | Notes |
|-----------|-------------|-------|
| Language parser | 1a | ANTLR grammar and parser exist; not yet wired into transport action |
| Type system | 1b | No type inference, no types at all |
| Core IR | 1c | No intermediate representation |
| Evaluator | 1c | No interpreter |
| Pattern matching | 1d | No match expressions |
| Piescript-specific response format | 1c+ | Returns raw `EsqlQueryResponse` |
| Async query support | 3 | Only synchronous `syncEsqlQueryRequest` |
| Feature flag / license gating | TBD | No gating mechanism |
| Unit tests | 1a+ | Parser unit tests exist; elaborator/evaluator unit tests will come in 1b/1c |

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

4. **Flat package structure.** All classes are in `org.elasticsearch.xpack.piescript`. Phase 1 will
   introduce sub-packages (`lang`, `types`, `core`, `eval`).

5. **No backwards compatibility versioning.** `PiescriptRequest` does not use `TransportVersion`
   checks because the protocol is trivial (single string). Phase 1's richer request format will
   need versioned serialization.

## Immediate Next Steps

Phase 1a (Parser) is in progress. The next work is completing it and moving to **Phase 1b: Type
System + Elaborator**. Before starting 1b, review:

- [roadmap.md](roadmap.md) for the Phase 1a task list
- [decisions.md](decisions.md) for type system and grammar decisions already made
- [Phase 1 discussion](3cd2a822-792c-4179-a00e-0ba98b875f52) for detailed design notes on grammar,
  typing rules, and node infrastructure
