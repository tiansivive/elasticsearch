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
│       ├── RestPiescriptAction.java    # REST handler (eval)
│       ├── RestPiescriptDevAction.java # REST handler (dev — CST inspection)
│       ├── TransportPiescriptAction.java   # Transport handler (ESQL bridge)
    │       ├── parser/                     # Phase 1a: lexer/parser
    │       │   ├── PiescriptParser.java         # Parser entry point (CST → parse tree)
    │       │   ├── PiescriptParsingException.java  # Parse error wrapper
    │       │   ├── PiescriptLexer.java          # (generated from PiescriptLexer.g4)
    │       │   ├── PiescriptAntlrParser.java    # (generated from PiescriptAntlrParser.g4)
    │       │   └── ...Visitor/Listener classes  # (generated ANTLR infrastructure)
    │       ├── types/                     # Phase 1b: type system data structures
    │       │   ├── Kind.java                    # Meta kind enum (TYPE, ROW)
    │       │   ├── MonoType.java                # Monomorphic types (sealed interface)
    │       │   ├── RowType.java                 # Row type (fields + optional row variable)
    │       │   ├── TypeScheme.java              # Polymorphic type scheme (∀-quantified)
    │       │   ├── LitVal.java                  # Literal values for Core IR
    │       │   └── Op.java                      # Primitive operator enum
    │       ├── core/                      # Phase 1b: Core IR (typed, elaborated)
    │       │   ├── CoreExpr.java                # Abstract sealed base (extends Node)
    │       │   ├── CoreField.java               # Helper record (label + value pair)
    │       │   ├── CoreVar.java                 # Variable (de Bruijn index)
    │       │   ├── CoreLit.java                 # Literal value
    │       │   ├── CoreLam.java                 # Lambda abstraction
    │       │   ├── CoreApp.java                 # Function application
    │       │   ├── CoreLet.java                 # Let-binding
    │       │   ├── CoreRecord.java              # Record literal
    │       │   ├── CoreProject.java             # Field projection
    │       │   ├── CoreUpdate.java              # Record update
    │       │   └── CorePrimOp.java              # Primitive operation
    │       └── elab/                      # Phase 1b: Elaboration machinery
    │           ├── ElaborationContext.java       # Immutable typing context (Γ + binding level)
    │           ├── ElaborationState.java        # Mutable global state (metavar supply + zonker)
    │           ├── TypeError.java               # Type error sealed interface
    │           └── Unifier.java                 # Robinson unification
    ├── test/java/org/elasticsearch/xpack/piescript/
    │   ├── parser/
    │   │   └── PiescriptParserTests.java   # Unit tests for parser
    │   ├── types/
    │   │   └── TypeDataStructureTests.java # Unit tests for type data structures
    │   ├── core/
    │   │   └── CoreExprTests.java          # Unit tests for Core IR
    │   └── elab/
    │       ├── ElaborationContextTests.java # Unit tests for immutable context
    │       ├── ElaborationStateTests.java  # Unit tests for mutable state + integrated scenarios
    │       └── UnifierTests.java           # Unit tests for unification
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
| `RestPiescriptDevAction.java` | Development endpoint. Registers `POST /_piescript/dev`, parses a program and returns the LISP-style CST produced by ANTLR for parser inspection. |
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

### Source (`src/main`) — Types (Phase 1b)

| File | Purpose |
|------|---------|
| `types/Kind.java` | Enum distinguishing type-level (`TYPE`) from row-level (`ROW`) metavariables. |
| `types/MonoType.java` | Sealed interface for monomorphic types: `TCon` (type constructor), `Arrow` (function), `RecordType`, `AppType` (type application), `Meta` (unsolved metavariable). |
| `types/RowType.java` | Record representing row structure: labeled fields (`Map<String, MonoType>`) plus optional row variable tail for row polymorphism. |
| `types/TypeScheme.java` | Polymorphic type scheme `∀{α₁..αₙ}.body`. Quantified set contains meta IDs. Monomorphic types use empty quantified set. |
| `types/LitVal.java` | Sealed interface for literal values carried by Core IR `Lit` nodes. Variants aligned with ES DataType: `IntegerLit`, `LongLit`, `DoubleLit`, `KeywordLit` (BytesRef), `BooleanLit`, `NullLit`. |
| `types/Op.java` | Enum for primitive operators used in Core IR `PrimOp` nodes: arithmetic, comparison, boolean, and unary operators. |

### Source (`src/main`) — Core IR (Phase 1b)

| File | Purpose |
|------|---------|
| `core/CoreExpr.java` | Abstract sealed base class extending `Node<CoreExpr>`. Provides default `writeTo`/`getWriteableName` (throws — Core IR is not serialized). All 9 concrete node types are permitted subclasses. |
| `core/CoreField.java` | Helper record pairing a label with a value expression. Convenience for constructing and inspecting `CoreRecord` and `CoreUpdate` nodes. |
| `core/CoreVar.java` | Variable reference by de Bruijn index. Leaf node (no children). |
| `core/CoreLit.java` | Literal value (`LitVal`). Leaf node (no children). |
| `core/CoreLam.java` | Lambda abstraction. One child (body). Carries optional debug name and elaborated parameter type. |
| `core/CoreApp.java` | Function application. Two children (fn, arg). |
| `core/CoreLet.java` | Let-binding. Two children (rhs, body). Carries optional debug name and elaborated bind type. |
| `core/CoreRecord.java` | Record literal. N children (field values), parallel to a `List<String>` of labels. |
| `core/CoreProject.java` | Field projection (`expr.label`). One child (expr). |
| `core/CoreUpdate.java` | Record update (`{ expr \| field = val }`). 1+N children (base expr + update values). |
| `core/CorePrimOp.java` | Primitive operation. N children (operands). Carries `Op` enum. |

### Source (`src/main`) — Elaboration (Phase 1b)

| File | Purpose |
|------|---------|
| `elab/ElaborationContext.java` | Immutable typing context passed by value through recursive descent. Holds the de Bruijn-indexed list of named type schemes and the binding level. Returns new instances on `bind()`, `enterBindingLevel()`, `exitBindingLevel()` — the call stack handles scope unwinding. `lookup()` returns `Optional<LookupResult>`. |
| `elab/ElaborationState.java` | Mutable global state shared across the elaboration pass. Holds only the metavariable supply (monotonic counter) and the zonker (meta ID → solution map with chain resolution). `freshType(bindingLevel)` and `freshRow(bindingLevel)` take the binding level from the caller's context. `resolve()` returns `Optional<Object>`. |
| `elab/TypeError.java` | Sealed interface for type errors returned by unification. Variants: `Mismatch` (structural incompatibility), `InfiniteType` (occurs check), `FieldMismatch` (wraps inner error with label), `MissingFields` (field set asymmetry). Not an exception — used as `Optional<TypeError>`. |
| `elab/Unifier.java` | Static Robinson unification over `MonoType`. Resolves through the zonker, handles `Meta` solving (with occurs check), null-as-bottom (D1.11), and structural matching for `TCon`, `Arrow`, `RecordType` (closed rows), `AppType`. Uses flat `if`-chain early exits + single `switch` expression with `when` guards. Returns `Optional<TypeError>` (empty = success). |

### Tests (`src/test`) — Unit Tests

| File | Purpose |
|------|---------|
| `parser/PiescriptParserTests.java` | Unit tests for the parser. Tests each syntax form and error reporting. |
| `types/TypeDataStructureTests.java` | Unit tests for type data structures. Tests construction, equality, sealed hierarchy exhaustiveness, and factory methods. |
| `core/CoreExprTests.java` | Unit tests for Core IR nodes. Tests construction, accessors, equality, replaceChildren, tree traversal, and NamedWriteable guard. |
| `elab/ElaborationContextTests.java` | Unit tests for the immutable context. Tests bind/lookup, de Bruijn indexing, shadowing, immutability guarantees (bind doesn't mutate original), binding level operations, scope unwinding via call stack. |
| `elab/ElaborationStateTests.java` | Unit tests for the mutable state. Tests fresh meta allocation with explicit binding levels, zonker solve/resolve/chain resolution, `resolveType`, and an integrated let-polymorphism workflow exercising both context and state together. |
| `elab/UnifierTests.java` | Unit tests for unification. Covers: identical types, meta solving (left/right/meta-meta/transitive/conflict), occurs check (direct/nested), null-as-bottom (with TCon/Arrow/Meta), arrow matching (success/param mismatch/result mismatch/with metas), record matching (success/missing/extra/field type mismatch/with metas/empty), AppType, and cross-form mismatches. |

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
| `piescript.types` | 1b | Exists | Type system (Kind, MonoType, RowType, TypeScheme, LitVal, Op) |
| `piescript.core` | 1b | Exists | Core IR (CoreExpr sealed hierarchy extending Node, CoreField helper) |
| `piescript.elab` | 1b | Exists | Elaboration machinery (immutable context, mutable state; future: unification, elaborator) |
| `piescript.eval` | 1c | Planned | Tree-walking evaluator, runtime values, closures |

## External Touchpoints

Files outside the piescript directory that reference piescript:

| File | What it does |
|------|-------------|
| `x-pack/plugin/security/src/main/java/org/elasticsearch/xpack/security/authz/RBACEngine.java` | Registers `indices:data/read/piescript` in `shouldAuthorizeIndexActionNameOnly()` for security authorization. |
| `x-pack/plugin/security/qa/operator-privileges-tests/.../Constants.java` | Adds piescript action to the operator privileges allowlist. |
