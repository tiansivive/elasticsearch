# Project Structure

> **Living doc** — update whenever files or packages are added/removed/renamed.

## Directory Layout

```
x-pack/plugin/piescript/
├── README.md                       # Build/test/manual-testing quick reference
├── build.gradle                    # Plugin build configuration
├── docs/                           # Project documentation (you are here)
│   ├── AGENTS.md                   # Agent entry point — read this first
│   ├── architecture.md             # System design and data flow
│   ├── project-structure.md        # This file
│   ├── vision.md                   # Long-term goals and design philosophy
│   ├── roadmap.md                  # Phased plan with status markers
│   ├── current-state.md            # What's implemented now, limitations
│   ├── decisions.md                # Key architectural decisions (ADR-lite)
│   └── references.md               # π-calculus papers, textbooks, theory
└── src/
    ├── main/
    │   ├── antlr/
    │   │   ├── PiescriptLexer.g4           # ANTLR lexer grammar (Phase 1a)
    │   │   └── PiescriptAntlrParser.g4     # ANTLR parser grammar (Phase 1a)
    │   └── java/org/elasticsearch/xpack/piescript/
    │       ├── PiescriptAction.java        # ActionType definition
    │       ├── PiescriptPlugin.java        # Plugin entry point
    │       ├── PiescriptRequest.java       # Request object (program carrier)
    │       ├── RestPiescriptAction.java    # REST handler
    │       ├── TransportPiescriptAction.java   # Transport handler (ESQL bridge)
    │       └── parser/                     # Phase 1a: lexer/parser
    │           ├── PiescriptParser.java         # Parser entry point (CST → parse tree)
    │           ├── PiescriptParsingException.java  # Parse error wrapper
    │           ├── PiescriptLexer.java          # (generated from PiescriptLexer.g4)
    │           ├── PiescriptAntlrParser.java    # (generated from PiescriptAntlrParser.g4)
    │           └── ...Visitor/Listener classes  # (generated ANTLR infrastructure)
    ├── test/java/org/elasticsearch/xpack/piescript/
    │   └── parser/
    │       └── PiescriptParserTests.java   # Unit tests for parser
    └── javaRestTest/java/org/elasticsearch/xpack/piescript/
        └── PiescriptIT.java           # Integration tests (6 test methods)
```

## File Responsibilities

### Build

| File | Purpose |
|------|---------|
| `build.gradle` | Declares the plugin as `elasticsearch.internal-es-plugin`, sets `extendedPlugins = ['x-pack-esql']`, adds compile-time dependencies on `x-pack-core`, `x-pack-esql`, and `x-pack-esql-core`. Configures `javaRestTest` to use the default distribution. |

### Source (`src/main`) — Plugin Core

| File | Purpose |
|------|---------|
| `PiescriptAction.java` | Defines the `ActionType` singleton (`indices:data/read/piescript`) with response type `EsqlQueryResponse`. This is the handle used to dispatch and route the action through the transport layer. |
| `PiescriptPlugin.java` | Plugin registration. Implements `ActionPlugin` to register the action handler (`PiescriptAction → TransportPiescriptAction`) and the REST handler (`RestPiescriptAction`). |
| `PiescriptRequest.java` | Immutable request object carrying the `program` string. Implements `CompositeIndicesRequest` for security delegation. Validates that `program` is non-blank. Serializable for transport. |
| `RestPiescriptAction.java` | HTTP entry point. Registers `POST /_piescript/eval`, parses the JSON body to extract `program`, and dispatches a `PiescriptRequest` to the transport layer. |
| `TransportPiescriptAction.java` | Core logic. Validates the `query ... ;` wrapper, extracts the ESQL query string, and delegates to `EsqlQueryAction` via the node client. Runs on `DIRECT_EXECUTOR_SERVICE`. |

### Source (`src/main`) — Parser (Phase 1a)

| File | Purpose |
|------|---------|
| `src/main/antlr/PiescriptLexer.g4` | ANTLR lexer grammar. Defines tokens for the piescript surface syntax per D1.17. |
| `src/main/antlr/PiescriptAntlrParser.g4` | ANTLR parser grammar. Defines the full expression grammar (precedence tower, lambdas, let-bindings, records, etc.) per D1.17. |
| `parser/PiescriptParser.java` | Parser entry point. Invokes the generated ANTLR parser and converts the CST to a usable parse tree. |
| `parser/PiescriptParsingException.java` | Parse error wrapper with source location. |
| `parser/PiescriptLexer.java` | Generated from `PiescriptLexer.g4` by ANTLR. |
| `parser/PiescriptAntlrParser.java` | Generated from `PiescriptAntlrParser.g4` by ANTLR. |
| `parser/*Visitor*.java`, `*Listener*.java` | Generated ANTLR infrastructure (visitor/listener base classes). |

### Tests (`src/test`) — Unit Tests

| File | Purpose |
|------|---------|
| `parser/PiescriptParserTests.java` | Unit tests for the parser. Tests each syntax form and error reporting. |

### Tests (`src/javaRestTest`) — Integration Tests

| File | Purpose |
|------|---------|
| `PiescriptIT.java` | Java REST integration test suite. Spins up a single-node cluster with trial license and security disabled. Tests: basic passthrough, filtered queries, invalid ESQL, empty program, missing `query` prefix, missing `program` field. |

## Packages

The root package is `org.elasticsearch.xpack.piescript`. Sub-packages are introduced per phase:

| Package | Phase | Status | Purpose |
|---------|-------|--------|---------|
| `piescript` | 0 | Exists | Plugin core: action, request, REST handler, transport action |
| `piescript.parser` | 1a | Exists | Lexer, parser, ANTLR-generated classes, parse errors |
| `piescript.types` | 1b | Planned | Type system (MonoType, PolyType, TypeScheme, Kind) |
| `piescript.core` | 1b | Planned | Core IR (elaborated, typed representation) |
| `piescript.eval` | 1c | Planned | Tree-walking evaluator, runtime values, closures |

## External Touchpoints

Files outside the piescript directory that reference piescript:

| File | What it does |
|------|-------------|
| `x-pack/plugin/security/src/main/java/org/elasticsearch/xpack/security/authz/RBACEngine.java` | Registers `indices:data/read/piescript` in `shouldAuthorizeIndexActionNameOnly()` for security authorization. |
| `x-pack/plugin/security/qa/operator-privileges-tests/.../Constants.java` | Adds piescript action to the operator privileges allowlist. |
