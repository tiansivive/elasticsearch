# Project Structure

> **Living doc** — update whenever files or packages are added/removed/renamed.
>
> **Last updated**: 2026-03-16 (Phase 2 complete). **Ref**: [Phase 2 completion](303bcf3e-9eef-4719-a47d-24c1ff27a675)

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
    │       │   ├── CorePrimOp.java              # Primitive operation
    │       │   └── CorePrinter.java             # Pretty-printer for Core IR + types
    │       ├── elab/                      # Phase 1b: Elaboration machinery
    │       │   ├── ElaborationContext.java       # Immutable typing context (Γ + binding level)
    │       │   ├── ElaborationState.java        # Mutable global state (metavar supply + zonker)
    │       │   ├── TypeError.java               # Type error sealed interface
    │       │   ├── Unifier.java                 # Robinson unification
    │       │   ├── Elaborator.java              # Bidirectional type checker + desugarer
    │       │   ├── TypeWalker.java             # Type-level traversal (generalize, instantiate, resolveDeep)
    │       │   ├── ElaborationException.java    # Fail-fast elaboration error
    │       │   ├── IndexResolutionPrePass.java  # Async index resolution (Phase 2)
    │       │   ├── ResolvedMapping.java         # Resolved index mapping record (Phase 2)
    │       │   ├── EsqlBodyParser.java          # Extract index patterns from ESQL body (Phase 2)
    │       │   ├── DataTypeMapping.java         # ES DataType → piescript MonoType (Phase 2)
    │       │   └── Polymorphism.java            # Generalization + instantiation helpers (Phase 2)
    │       └── eval/                      # Phase 1c + Phase 2: Evaluation
    │           ├── Value.java                   # Runtime value sealed interface (10 variants)
    │           ├── Evaluator.java               # Tree-walking de Bruijn environment machine
    │           ├── EsqlValueConverter.java      # ESQL response → StreamVal converter
    │           └── EvaluationException.java     # Runtime evaluation error
    ├── test/java/org/elasticsearch/xpack/piescript/
    │   ├── parser/
    │   │   └── PiescriptParserTests.java   # Unit tests for parser
    │   ├── types/
    │   │   └── TypeDataStructureTests.java # Unit tests for type data structures
    │   ├── core/
    │   │   └── CoreExprTests.java          # Unit tests for Core IR
    │   ├── elab/
    │   │   ├── ElaborationContextTests.java # Unit tests for immutable context
    │   │   ├── ElaborationStateTests.java  # Unit tests for mutable state + integrated scenarios
    │   │   ├── UnifierTests.java           # Unit tests for unification
    │   │   └── ElaboratorTests.java        # Unit tests for elaborator (72 tests)
    │   └── eval/
    │       └── EvaluatorTests.java        # Unit tests for evaluator (59 tests)
    └── javaRestTest/java/org/elasticsearch/xpack/piescript/
        └── PiescriptIT.java           # Integration tests (15 test methods)
```

## File Responsibilities

### Build

| File | Purpose |
|------|---------|
| `build.gradle` | Declares the plugin as `elasticsearch.internal-es-plugin`, sets `extendedPlugins = ['x-pack-esql']`, adds compile-time dependencies on `x-pack-core`, `x-pack-esql`, and `x-pack-esql-core`. Configures `javaRestTest` to use the default distribution. |

### Source (`src/main`) — Plugin Core

| File | Purpose |
|------|---------|
| `PiescriptAction.java` | Defines the `ActionType` singleton (`indices:data/read/piescript`) with response type `PiescriptResponse`. This is the handle used to dispatch and route the action through the transport layer. |
| `PiescriptResponse.java` | Response wrapper: holds a `Value` + type string (expression result), including `StreamVal` serialized as JSON arrays. Implements `ChunkedToXContentObject` and `Releasable`. Serializable for transport (D-023). |
| `PiescriptPlugin.java` | Plugin registration. Implements `ActionPlugin` to register the action handler (`PiescriptAction → TransportPiescriptAction`) and the REST handler (`RestPiescriptAction`). |
| `PiescriptRequest.java` | Immutable request object carrying the `program` string. Implements `CompositeIndicesRequest` for security delegation. Validates that `program` is non-blank. Serializable for transport. |
| `RestPiescriptAction.java` | HTTP entry point. Registers `POST /_piescript/eval`, parses the JSON body to extract `program`, and dispatches a `PiescriptRequest` to the transport layer. |
| `RestPiescriptDevAction.java` | Development endpoint. Registers `POST /_piescript/dev`, runs the full parse → elaborate → evaluate pipeline and returns `tree` (CST), `core` (pretty-printed Core IR), `type` (resolved type), and `eval` (evaluated result). Parse errors return `parse_error`; type errors return `tree` + `type_error`; eval errors return `eval_error`. |
| `TransportPiescriptAction.java` | Core logic. Unified pipeline: parse → index resolution pre-pass → elaborate → evaluate. Runs on `ThreadPool.Names.GENERIC` (D-004 revision). Resolve callbacks fork to GENERIC before evaluation to avoid blocking coordination threads. |

### Source (`src/main`) — Parser (Phase 1a)

| File | Purpose |
|------|---------|
| `src/main/antlr/PiescriptLexer.g4` | ANTLR lexer grammar. Defines tokens for the piescript surface syntax per D1.17. **Phase 1d splits `IDENTIFIER` into `UPPER_IDENT` / `LOWER_IDENT` (D-033).** |
| `src/main/antlr/PiescriptAntlrParser.g4` | ANTLR parser grammar. Defines the full expression grammar (precedence tower, lambdas, let-bindings, records, etc.) per D1.17. **Phase 1d adds `ident` helper rule, splits type `TypeCon` into `TypeCon` (UPPER_IDENT) and `TypeVar` (LOWER_IDENT).** |
| `parser/PiescriptParser.java` | Parser entry point. Invokes the generated ANTLR parser and converts the CST to a usable parse tree. |
| `parser/PiescriptParsingException.java` | Parse error wrapper with source location. |
| `parser/PiescriptLexer.java` | Generated from `PiescriptLexer.g4` by ANTLR. |
| `parser/PiescriptAntlrParser.java` | Generated from `PiescriptAntlrParser.g4` by ANTLR. |
| `parser/*Visitor*.java`, `*Listener*.java` | Generated ANTLR infrastructure (visitor/listener base classes). |

### Source (`src/main`) — Types (Phase 1b)

| File | Purpose |
|------|---------|
| `types/Kind.java` | Enum distinguishing type-level (`TYPE`) from row-level (`ROW`) metavariables. |
| `types/MonoType.java` | Sealed interface for monomorphic types: `TCon` (type constructor), `Arrow` (function), `RecordType`, `AppType` (type application), `Meta` (unsolved metavariable). **Phase 1d adds `Rigid(int id, Kind kind)` for bound/skolemized type variables (D-031).** |
| `types/RowType.java` | Record representing row structure: labeled fields (`Map<String, MonoType>`) plus optional row variable tail for row polymorphism. |
| `types/TypeScheme.java` | Polymorphic type scheme `∀{α₁..αₙ}.body`. Quantified set contains meta IDs. Monomorphic types use empty quantified set. **Phase 1d changes `quantified` from `Set<Integer>` to `Map<Integer, Kind>` for kind-aware instantiation; Rigids replace Metas in the quantified set.** |
| `types/LitVal.java` | Sealed interface for literal values carried by Core IR `Lit` nodes. Variants aligned with ES DataType: `IntegerLit`, `LongLit`, `DoubleLit`, `KeywordLit` (BytesRef), `BooleanLit`, `NullLit`. |
| `types/Op.java` | Enum for primitive operators used in Core IR `PrimOp` nodes: arithmetic, comparison, boolean, and unary operators. |

### Source (`src/main`) — Core IR (Phase 1b)

| File | Purpose |
|------|---------|
| `core/CoreExpr.java` | Abstract sealed base class extending `Node<CoreExpr>`. Provides default `writeTo`/`getWriteableName` (throws — Core IR is not serialized). All concrete node types are permitted subclasses. |
| `core/CoreField.java` | Helper record pairing a label with a value expression. Convenience for constructing and inspecting `CoreRecord` and `CoreUpdate` nodes. |
| `core/CoreVar.java` | Variable reference by de Bruijn index. Leaf node (no children). |
| `core/CoreFree.java` | Free variable reference (module-level). Carries name and type, no de Bruijn index. Emitted for built-in functions resolved from the module map. |
| `core/CoreLit.java` | Literal value (`LitVal`). Leaf node (no children). |
| `core/CoreLam.java` | Lambda abstraction. One child (body). Carries optional debug name and elaborated parameter type. |
| `core/CoreApp.java` | Function application. Two children (fn, arg). |
| `core/CoreLet.java` | Let-binding. Two children (rhs, body). Carries optional debug name and elaborated bind type. |
| `core/CoreRecord.java` | Record literal. N children (field values), parallel to a `List<String>` of labels. |
| `core/CoreProject.java` | Field projection (`expr.label`). One child (expr). |
| `core/CoreUpdate.java` | Record update (`{ expr \| field = val }`). 1+N children (base expr + update values). |
| `core/CorePrimOp.java` | Primitive operation. N children (operands). Carries `Op` enum. |
| `core/CorePrinter.java` | Pretty-printer for Core IR expressions and types. Produces S-expression-like output with resolved types (via `TypeWalker.resolveDeep`). Used by the dev endpoint. |

### Source (`src/main`) — Elaboration (Phase 1b)

| File | Purpose |
|------|---------|
| `elab/ElaborationContext.java` | Immutable typing context passed by value through recursive descent. Holds the de Bruijn-indexed list of named type schemes, a module-level free variable map (`Map<String, TypeScheme>`), and the binding level. `lookup()` checks local bindings (returns de Bruijn index); `lookupModule()` checks module bindings (returns type scheme only). Local variables shadow module-level names. `withModule()` factory creates a context with pre-populated module bindings. |
| `elab/Prelude.java` | Built-in function definitions: type schemes and arities for `map`, `filter`, `reduce`. Exports `MODULE` (the module map) and `ARITY` (name → argument count). Wired into the elaboration context at program start. |
| `elab/ElaborationState.java` | Mutable global state shared across the elaboration pass. Holds only the metavariable supply (monotonic counter) and the zonker (meta ID → solution map with chain resolution). `freshType(bindingLevel)` and `freshRow(bindingLevel)` take the binding level from the caller's context. `resolve()` returns `Optional<Object>`. **Phase 1d renames `resolveType` → `zonk` returning `Optional<MonoType>` (D-032), adds `resolveRow(RowType)` for flattening.** |
| `elab/TypeError.java` | Sealed interface for type errors returned by unification. Variants: `Mismatch` (structural incompatibility), `InfiniteType` (occurs check), `FieldMismatch` (wraps inner error with label), `MissingFields` (field set asymmetry). Not an exception — used as `Optional<TypeError>`. |
| `elab/Unifier.java` | Static Robinson unification over `MonoType`. Resolves through the zonker, handles `Meta` solving (with occurs check), null-as-bottom (D1.11), and structural matching for `TCon`, `Arrow`, `RecordType` (closed rows), `AppType`. Uses flat `if`-chain early exits + single `switch` expression with `when` guards. Returns `Optional<TypeError>` (empty = success). **Phase 1d rewrites `unifyRows` to Leijen-style open-row decomposition (D-030) and adds `Rigid` handling (D-031).** |
| `elab/Elaborator.java` | Bidirectional type checker and desugarer. Pattern-matching recursive descent over ANTLR parse tree → Core IR. Single `elaborate` switch dispatches on all CST node types. Handles: let (with generalization), lambda (multi-param desugaring), application, primops (concrete Integer-only typed functions, D-020), records, projection (closed-row direct lookup), update, accessor/update-sugar (lambda desugaring), blocks, ascription, literals, pipe (flipped app), top-level bindings. Phase 1 limitations: no if/then/else, no open rows, no numeric widening. **Phase 1d changes `resolveTypeAnnotation` to return `TypeScheme` (D-034), adds checking rule for universal types, updates accessor/update/projection to use open rows with unification (supersedes D-021).** |
| `elab/TypeWalker.java` | Static type-level traversal utilities. Generalization (collect unsolved metas at binding level → quantify), instantiation (replace quantified metas with fresh ones), deep resolution (fully resolve all metas in a type), and type walking (substitution). Extracted from `Elaborator` for clarity. Public (`resolveDeep` used by `CorePrinter`). **Phase 1d removes `resolveDeep` (D-032), changes `collectMetas` to return `Map<Integer, Kind>`, and makes `instantiate` kind-aware.** |
| `elab/ElaborationException.java` | Unchecked exception for fail-fast elaboration errors (D1.14). Carries line/column and optional `TypeError`. Avoids calling `Source` methods to sidestep the `WarningSourceLocation` compile dependency. |
| `elab/IndexResolutionPrePass.java` | Phase 2 index resolution pre-pass. Walks the CST to collect `QueryExpr` nodes, extracts index patterns via `EsqlBodyParser`, and asynchronously resolves field caps via `IndexResolver`. Produces `Map<String, ResolvedMapping>` for the elaborator. |
| `elab/ResolvedMapping.java` | Record holding a resolved index pattern's mapping (`EsIndex`) and partially unmapped fields. Consumed by the elaborator to type query expressions. |
| `elab/EsqlBodyParser.java` | Parses an ESQL body string to extract the index pattern (the `FROM` target). Used by `IndexResolutionPrePass` during query collection. |
| `elab/DataTypeMapping.java` | Maps ES `DataType` values (from field caps) to piescript `MonoType`. Used when building the row type for a resolved query expression. |
| `elab/Polymorphism.java` | Extracted generalization and instantiation logic. `generalize` collects unsolved metas and quantifies them with `CoreTypeAbs` wrappers. `instantiateAndWrap` replaces quantified rigids with fresh metas and wraps with `CoreTypeApp`. |

### Source (`src/main`) — Evaluation (Phase 1c)

| File | Purpose |
|------|---------|
| `eval/Value.java` | Sealed interface for runtime values. 10 variants: `IntegerVal`, `LongVal`, `DoubleVal`, `KeywordVal(String)` (D-026), `BooleanVal`, `NullVal`, `RecordVal(Map<String, Value>)`, `StreamVal(List<Value>)`, `ClosureVal(CoreExpr body, Value[] env)`, `BuiltinVal(name, arity, partialArgs)`. `StreamVal` is the eagerly materialized stream (Phase 2). `BuiltinVal` supports curried partial application for built-in functions. |
| `eval/Evaluator.java` | Tree-walking de Bruijn environment machine. Takes optional `Client` for query execution. Evaluates all `CoreExpr` variants. `CoreQuery` fires `EsqlQueryAction` synchronously and converts to `StreamVal` via `EsqlValueConverter`. `CoreFree` produces `BuiltinVal`; `CoreApp` dispatches to closures or built-in partial application. Built-ins `map`/`filter`/`reduce` operate over `StreamVal` via `applyFunction`. `CoreLit` converts `BytesRef` to `String` at the boundary. `CorePrimOp` dispatches arithmetic (integer-only, D-020), comparison, and boolean operations. |
| `eval/EsqlValueConverter.java` | Converts `EsqlQueryResponse` to `StreamVal`. Each row becomes a `RecordVal` (column names as field keys). Cell conversion uses `instanceof` dispatch (`Integer`, `Long`, `Double`, `String`, `Boolean`, `null`, multi-value first-element). |
| `eval/EvaluationException.java` | Unchecked runtime error for user-observable evaluation failures (null in arithmetic, division by zero). |

### Tests (`src/test`) — Unit Tests

| File | Purpose |
|------|---------|
| `parser/PiescriptParserTests.java` | Unit tests for the parser. Tests each syntax form and error reporting. |
| `types/TypeDataStructureTests.java` | Unit tests for type data structures. Tests construction, equality, sealed hierarchy exhaustiveness, and factory methods. |
| `core/CoreExprTests.java` | Unit tests for Core IR nodes. Tests construction, accessors, equality, replaceChildren, tree traversal, and NamedWriteable guard. |
| `elab/ElaborationContextTests.java` | Unit tests for the immutable context. Tests bind/lookup, de Bruijn indexing, shadowing, immutability guarantees (bind doesn't mutate original), binding level operations, scope unwinding via call stack. |
| `elab/ElaborationStateTests.java` | Unit tests for the mutable state. Tests fresh meta allocation with explicit binding levels, zonker solve/resolve/chain resolution, `resolveType`, and an integrated let-polymorphism workflow exercising both context and state together. |
| `elab/UnifierTests.java` | Unit tests for unification. Covers: identical types, meta solving (left/right/meta-meta/transitive/conflict), occurs check (direct/nested), null-as-bottom (with TCon/Arrow/Meta), arrow matching (success/param mismatch/result mismatch/with metas), record matching (success/missing/extra/field type mismatch/with metas/empty), AppType, and cross-form mismatches. |
| `elab/ElaboratorTests.java` | Unit tests for the elaborator (72 tests). Covers: literals (int, long, decimal, string, escapes, boolean, null), let-bindings (basic, annotated, nested, shadowing, top-level, multiple), lambdas (identity, typed, multi-param), application (direct, type inference), let-polymorphism, all arithmetic/comparison/boolean operators, unary ops (negation, not), records (empty, literal, projection, update, field addition), pipe operator, accessor sugar, update sugar, blocks (let stmts, expr stmts, multi), parentheses, type ascription, de Bruijn indices, error cases (unbound variable, type mismatch, non-function application, duplicate field, projection on non-record, missing field, annotation mismatch, unknown type, if/then/else unsupported, update on non-record), and deferred tests (occurs check, cross-type arithmetic, lambda type mismatch). |
| `eval/EvaluatorTests.java` | Unit tests for the evaluator (59 tests). Covers: literals (int, long, double, string, boolean, null), arithmetic (+, -, *, /, %), comparisons (<, >, <=, >=, ==, !=), boolean ops (&&, \|\|, !), unary negation, let-bindings (basic, expression, nested, shadowing, top-level), lambdas (identity, increment, multi-param, returns closure), let-polymorphism, records (empty, literal, projection, update, add field), pipe operator, blocks, closures (curried, capture), accessor sugar, nested record projection, error cases (division by zero, modulo by zero, null in arithmetic), complex expressions, and stream built-ins (`map` projection/transform, `filter` predicate/keep-all/remove-all, `reduce` sum/empty, empty stream, query-without-client). |

### Tests (`src/javaRestTest`) — Integration Tests

| File | Purpose |
|------|---------|
| `PiescriptIT.java` | Java REST integration test suite (15 tests). Spins up a single-node cluster with trial license, security disabled, ML disabled. Tests: query type-checking (dev endpoint), eager evaluation (stream results, map projection, filter predicate, reduce sum), expression evaluation (arithmetic, records, lambdas, booleans), error handling (empty program, missing field, type error, parse error, malformed input). |

## Packages

The root package is `org.elasticsearch.xpack.piescript`. Sub-packages are introduced per phase:

| Package | Phase | Status | Purpose |
|---------|-------|--------|---------|
| `piescript` | 0 | Exists | Plugin core: action, request, REST handler, transport action |
| `piescript.parser` | 1a | Exists | Lexer, parser, ANTLR-generated classes, parse errors |
| `piescript.types` | 1b | Exists | Type system (Kind, MonoType, RowType, TypeScheme, LitVal, Op) |
| `piescript.core` | 1b | Exists | Core IR (CoreExpr sealed hierarchy extending Node, CoreField helper) |
| `piescript.elab` | 1b | Exists | Elaboration machinery (immutable context, mutable state; future: unification, elaborator) |
| `piescript.eval` | 1c | Exists | Tree-walking evaluator, runtime values, closures |

## External Touchpoints

Files outside the piescript directory that reference piescript:

| File | What it does |
|------|-------------|
| `x-pack/plugin/security/src/main/java/org/elasticsearch/xpack/security/authz/RBACEngine.java` | Registers `indices:data/read/piescript` in `shouldAuthorizeIndexActionNameOnly()` for security authorization. |
| `x-pack/plugin/security/qa/operator-privileges-tests/.../Constants.java` | Adds piescript action to the operator privileges allowlist. |
