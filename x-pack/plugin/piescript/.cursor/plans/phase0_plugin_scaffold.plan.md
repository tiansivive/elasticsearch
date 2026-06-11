---
name: "Phase 0: Piescript Plugin Scaffold"
overview: "Plugin scaffold + ESQL query passthrough. Vertical slice: curl a piescript program, get ESQL results back."
todos:
  - id: t0.1
    content: "Create Gradle module: x-pack/plugin/piescript/build.gradle"
    status: completed
  - id: t0.2
    content: "Create plugin class: PiescriptPlugin"
    status: completed
  - id: t0.3
    content: "Create request + action type: PiescriptRequest, PiescriptAction"
    status: completed
  - id: t0.4
    content: "Create REST handler: RestPiescriptAction"
    status: completed
  - id: t0.5
    content: "Create transport action: TransportPiescriptAction"
    status: completed
  - id: t0.6
    content: "Security: RBACEngine + operator privileges for piescript action"
    status: completed
  - id: t0.7
    content: "Integration test: PiescriptIT (javaRestTest)"
    status: completed
isProject: false
---

# Phase 0: Plugin Scaffold + Query Passthrough

**Parent:** `scripting_language_design_9286506e.plan.md`
**Status:** Ready to implement
**Dependencies:** None
**Open design questions:** None

## Goal

Prove end-to-end plumbing: a client sends a program string via REST, the plugin extracts the ESQL query, delegates to ESQL via the node client, and returns a columnar result. No parsing, no typechecking, no Core IR, no interpreter.

## Vertical Slice

```bash
curl -X POST "localhost:9200/_piescript/eval" \
  -H "Content-Type: application/json" \
  -d '{ "program": "query FROM logs-* | WHERE status >= 500;" }'
```

Returns the same columnar JSON as `POST /_query` would for the equivalent ESQL.

## Decisions (Resolved)

### D0.1: Plugin name — `piescript`


| Artifact                    | Value                               |
| --------------------------- | ----------------------------------- |
| Gradle module path          | `x-pack/plugin/piescript/`          |
| Java package                | `org.elasticsearch.xpack.piescript` |
| Plugin ID (`esplugin.name`) | `x-pack-piescript`                  |
| REST path                   | `/_piescript/eval`                  |
| Action type string          | `"indices:data/read/piescript"`     |


### D0.2: REST API shape

```
POST /_piescript/eval
{
  "program": "query FROM logs-* | WHERE status >= 500;"
}
```

Response format: identical to ESQL's `/_query` response (columnar JSON with `columns` and `values` arrays). This is achieved by using `EsqlQueryResponse` directly as the action's response type (see Shortcuts section).

For Phase 0, the request body has a single field: `program`. Future phases add `params`, `timeout`, etc.

### D0.3: Query extraction — string prefix strip

Require the program to start with `query` and end with `;`. Strip `query` prefix (with whitespace) and trailing `;`, pass the middle verbatim to ESQL. Throwaway code replaced by the real parser in Phase 1.

**Known limitation:** ESQL strings containing semicolons (e.g., string literals like `"a;b"`) will break extraction. Acceptable for Phase 0.

### D0.4: Response type — reuse `EsqlQueryResponse` directly

No `PiescriptResponse` wrapper. The action type is `ActionType<EsqlQueryResponse>`. The transport action receives `EsqlQueryResponse` from ESQL and passes it through to the REST layer unchanged.

**Rationale:** `EsqlQueryResponse` implements `ChunkedToXContentObject` and `RefCounted` (via `TransportMessage`). Creating a wrapper would require re-implementing chunked serialization and ref-count lifecycle management — complexity with zero benefit for scaffolding. The built-in `RestRefCountedChunkedToXContentListener` handles it automatically.

**Trade-off:** Couples the action's type signature to ESQL. Acceptable for Phase 0; decoupled when the language returns its own result types in later phases.

### D0.5: Request base class — `ActionRequest`

`PiescriptRequest extends ActionRequest`. Not `LegacyActionRequest`, which is `@Deprecated` (Javadoc: *"Use ActionRequest with a specific ActionResponse type"*). Newer plugins (otel-data, prometheus, newer ESQL actions) all use `ActionRequest` directly.

### D0.6: Security — same model as ESQL

`PiescriptRequest implements CompositeIndicesRequest` (marker interface, no methods). This tells the security authorization engine to authorize by action name only, deferring per-index authorization to the internal ESQL sub-request. Requires a one-line addition to `RBACEngine.shouldAuthorizeIndexActionNameOnly()` and one-line addition to the operator privileges allowlist.

The `read` privilege covers `indices:data/read/piescript` via the existing `indices:data/read/`* wildcard in `IndexPrivilege.READ_AUTOMATON`. No privilege registration needed.

### D0.7: No feature flag, no license, no cluster capabilities

Simplicity for Phase 0. Not merging soon; all can be added later if needed.

## Tasks

### T0.1: Create Gradle module

**File:** `x-pack/plugin/piescript/build.gradle`

```groovy
apply plugin: 'elasticsearch.internal-es-plugin'
apply plugin: 'elasticsearch.internal-java-rest-test'

esplugin {
    name = 'x-pack-piescript'
    description = 'Typed functional scripting language for Elasticsearch'
    classname = 'org.elasticsearch.xpack.piescript.PiescriptPlugin'
    extendedPlugins = ['x-pack-esql']
}

base {
    archivesName = 'x-pack-piescript'
}

dependencies {
    compileOnly project(path: xpackModule('core'))
    compileOnly project(path: xpackModule('esql'))
    compileOnly project(xpackModule('esql-core'))
    javaRestTestImplementation(testArtifact(project(xpackModule('core'))))
}
```

**Gradle plugins applied:**

- `elasticsearch.internal-es-plugin` — core plugin build infrastructure: `esplugin {}` DSL, plugin descriptor generation, JAR hell checks, Java compilation, cluster features metadata.
- `elasticsearch.internal-java-rest-test` — adds the `javaRestTest` source set and task for integration tests.

**Why `extendedPlugins = ['x-pack-esql']` alone:** The plugin classloader chain resolves transitively. `x-pack-esql` extends `x-pack-esql-core` which extends `x-pack-core`. Classes from all three are visible at runtime. This is the same pattern used by ESQL datasource plugins (e.g., `esql-datasource-csv`). Verified at build time with `./gradlew :x-pack:plugin:piescript:compileJava`.

**Why `compileOnly`:** Standard convention for x-pack plugin dependencies. The classes are available at runtime through the plugin classloader hierarchy established by `extendedPlugins`. `compileOnly` prevents them from being bundled into the plugin's own JAR.

**Auto-discovery:** `settings.gradle` line 163 calls `addSubProjects('', new File(rootProject.projectDir, 'x-pack'))` which recursively discovers `build.gradle` files. Since `x-pack/` and `x-pack/plugin/` both have existing `build.gradle` files, the new `x-pack/plugin/piescript/build.gradle` is found automatically. No `settings.gradle` edit needed.

**Distribution bundling:** `distribution/build.gradle` (lines 229-244) iterates all direct children of `:x-pack:plugin` and bundles them via `distro.copyModule()`. The new plugin is automatically included in the default distribution.

### T0.2: Create plugin class

**File:** `x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/PiescriptPlugin.java`

`PiescriptPlugin extends Plugin implements ActionPlugin`

Registers:

- One `ActionHandler`: `PiescriptAction.INSTANCE` -> `TransportPiescriptAction.class`
- One `RestHandler`: `RestPiescriptAction`

No `createComponents()` override needed for Phase 0 — the transport action uses the Guice-injected `Client` for delegation.

**Reference:** [EsqlPlugin.java](x-pack/plugin/esql/src/main/java/org/elasticsearch/xpack/esql/plugin/EsqlPlugin.java) — `getActions()` at line 336, `getRestHandlers()` at line 364.

**Depends on:** T0.1

### T0.3: Create request and action type

**Files:**

- `x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/PiescriptAction.java`
- `x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/PiescriptRequest.java`

`**PiescriptAction`** — singleton action type:

```java
public class PiescriptAction extends ActionType<EsqlQueryResponse> {
    public static final PiescriptAction INSTANCE = new PiescriptAction();
    public static final String NAME = "indices:data/read/piescript";
    private PiescriptAction() { super(NAME); }
}
```

Response type is `EsqlQueryResponse` directly (see D0.4).

`**PiescriptRequest**` — transport request:

- Extends `ActionRequest` (see D0.5)
- Implements `CompositeIndicesRequest` (marker, see D0.6)
- Single field: `String program`
- `validate()`: checks that `program` is non-null and non-empty
- Transport serialization: `writeTo(StreamOutput)` writes `program`; `PiescriptRequest(StreamInput)` reads it. No `TransportVersion` gating needed since there are no prior versions.

**Reference:** [EsqlQueryAction.java](x-pack/plugin/esql/src/main/java/org/elasticsearch/xpack/esql/action/EsqlQueryAction.java) for action type pattern.

**Depends on:** T0.1

### T0.4: Create REST handler

**File:** `x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/RestPiescriptAction.java`

`RestPiescriptAction extends BaseRestHandler`

- Route: `POST /_piescript/eval`
- Handler name: `"piescript_eval"`
- `prepareRequest()`: parse JSON body with `request.contentOrSourceParamParser()`, extract the `program` string field, construct a `PiescriptRequest`, return a `RestChannelConsumer` that calls `client.execute(PiescriptAction.INSTANCE, request, listener)`
- Response listener: `new RestRefCountedChunkedToXContentListener<>(channel)` — works because `EsqlQueryResponse` is both `ChunkedToXContentObject` and `RefCounted` (inherited from `TransportMessage`). This listener handles chunked JSON serialization and ref-count cleanup automatically. No custom listener needed.

**Reference:** [RestEsqlQueryAction.java](x-pack/plugin/esql/src/main/java/org/elasticsearch/xpack/esql/action/RestEsqlQueryAction.java) — route at line 43, `prepareRequest()` at line 52.

**Depends on:** T0.3

### T0.5: Create transport action

**File:** `x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/TransportPiescriptAction.java`

`TransportPiescriptAction extends HandledTransportAction<PiescriptRequest, EsqlQueryResponse>`

**Constructor (Guice-injected):**

- `TransportService transportService`
- `ActionFilters actionFilters`
- `Client client` — the node client for delegating to ESQL

Passes to super: `PiescriptAction.NAME`, `transportService`, `actionFilters`, `PiescriptRequest::new` (the `Writeable.Reader`), `EsExecutors.DIRECT_EXECUTOR_SERVICE` (runs on transport thread — appropriate for a thin delegation action that does no compute work itself).

`**doExecute()` logic:**

1. Extract `program` from `PiescriptRequest`
2. Call `extractEsqlQuery(program)` — strip `query` prefix and `;` suffix to get raw ESQL string
3. Construct `EsqlQueryRequest` via `EsqlQueryRequest.syncEsqlQueryRequest(esqlQuery)` — this factory method is `public static` and accessible from outside the ESQL package
4. Call `client.execute(EsqlQueryAction.INSTANCE, esqlRequest, listener)` — fully async, ESQL handles thread dispatch internally (forks to `SEARCH` then `esql_worker` pools)

**Why `DIRECT_EXECUTOR_SERVICE`:** The transport action does minimal work — string extraction and delegation. The actual query execution happens inside ESQL on its own thread pools. This is the standard pattern for delegation-only transport actions (see `EsqlResolveFieldsAction`, `TransportInferenceActionProxy`).

**Why `client.execute()` over `PlanExecutor`:** Standard ES cross-plugin delegation pattern. Avoids coupling to ESQL internals. The node client handles transport dispatch, security re-authorization, and task management transparently. ESQL's transport action handles forking to the right thread pool, compute execution, and async task management.

**Reference:** [TransportEsqlQueryAction.java](x-pack/plugin/esql/src/main/java/org/elasticsearch/xpack/esql/plugin/TransportEsqlQueryAction.java) — constructor at line 116, super call at line 136.

**Depends on:** T0.3

### T0.6: Security wiring

**File 1:** `x-pack/plugin/security/src/main/java/org/elasticsearch/xpack/security/authz/RBACEngine.java`

Add one line to the `switch` in `shouldAuthorizeIndexActionNameOnly()` (after line 306):

```java
case "indices:data/read/esql/compute":
case "indices:data/read/piescript":   // <-- add
```

This tells the security engine: "this action is composite — authorize by action name only, defer per-index authorization to the sub-requests." Without this line, security would throw `IllegalStateException` because `PiescriptRequest implements CompositeIndicesRequest` but the action string isn't in the known composite action list.

**File 2:** `x-pack/plugin/security/qa/operator-privileges-tests/src/javaRestTest/java/org/elasticsearch/xpack/security/operator/Constants.java`

Add one line to the sorted operator-privileges action allowlist (between `esql/search_shards` and `explain`, around line 597):

```java
"indices:data/read/esql/search_shards",
"indices:data/read/piescript",        // <-- add
"indices:data/read/explain",
```

**Depends on:** T0.3 (action type string must be finalized)

### T0.7: Integration test

**File:** `x-pack/plugin/piescript/src/javaRestTest/java/org/elasticsearch/xpack/piescript/PiescriptIT.java`

`PiescriptIT extends ESRestTestCase`

**Cluster setup:** `ElasticsearchCluster.local()` with `DistributionType.DEFAULT` (which includes piescript since it's bundled under `x-pack/plugin/`). Settings: `xpack.security.enabled=false`, `xpack.license.self_generated.type=trial`.

**Gradle config:** The `javaRestTest` task needs `usesDefaultDistribution("piescript requires default distribution")`.

**Test cases:**

1. **Positive:** Ingest documents into a test index. Send `POST /_piescript/eval { "program": "query FROM test-index;" }`. Assert response contains expected `columns` and `values` arrays.
2. **Negative (invalid ESQL):** Send a syntactically invalid ESQL query. Assert a 400-level error with a meaningful message (not a 500).
3. **Negative (empty program):** Send `{ "program": "" }`. Assert validation error.
4. **Negative (malformed program):** Send `{ "program": "not a query" }` (no `query` prefix). Assert a clean error.
5. **Negative (missing body):** Send empty request. Assert validation error.

Uses the low-level REST client (`client().performRequest(...)`) — no REST API spec needed.

**Reference:** ESQL QA structure under `x-pack/plugin/esql/qa/server/single-node/`. Cluster setup pattern from [Clusters.java](x-pack/plugin/esql/qa/server/single-node/src/javaRestTest/java/org/elasticsearch/xpack/esql/qa/single_node/Clusters.java).

**Depends on:** T0.5, T0.6

## File Inventory


| #   | File                                            | Type          | Purpose                       |
| --- | ----------------------------------------------- | ------------- | ----------------------------- |
| 1   | `x-pack/plugin/piescript/build.gradle`          | New           | Gradle module definition      |
| 2   | `.../piescript/PiescriptPlugin.java`            | New           | Plugin entry point            |
| 3   | `.../piescript/PiescriptAction.java`            | New           | Action type constant          |
| 4   | `.../piescript/PiescriptRequest.java`           | New           | Transport request             |
| 5   | `.../piescript/RestPiescriptAction.java`        | New           | REST handler                  |
| 6   | `.../piescript/TransportPiescriptAction.java`   | New           | Transport action (delegation) |
| 7   | `.../security/authz/RBACEngine.java`            | Edit (1 line) | Security composite action     |
| 8   | `.../security/qa/.../Constants.java`            | Edit (1 line) | Operator privileges allowlist |
| 9   | `.../piescript/PiescriptIT.java` (javaRestTest) | New           | Integration test              |


## Testing Strategy

- **Positive:** Valid ESQL passthrough returns correct columnar results
- **Negative:** Invalid ESQL produces a meaningful 400 error (not a 500)
- **Negative:** Empty program produces a validation error
- **Negative:** Program without `query` prefix produces a clean extraction error
- **Negative:** Missing request body produces a validation error
- **Boundary:** Large result sets work (pagination deferred but shouldn't crash)

## Known Shortcuts (Phase 0)


| Shortcut                                                                         | Impact                                    | Resolved in                              |
| -------------------------------------------------------------------------------- | ----------------------------------------- | ---------------------------------------- |
| No `PiescriptResponse` wrapper — action typed as `ActionType<EsqlQueryResponse>` | Couples action signature to ESQL          | Phase 1+ when language returns own types |
| No semicolon handling in query extraction                                        | `"a;b"` in ESQL strings breaks extraction | Phase 1 (real parser)                    |
| Synchronous only — no async query support                                        | Large queries may time out                | Future phase                             |
| No feature flag or license gating                                                | Endpoint available to all users           | Before merge                             |
| No cluster capability registration                                               | Clients can't discover the endpoint       | Before merge                             |


## Implications for Master Plan

None. Phase 0 is pure plumbing and introduces no language design decisions. The throwaway query extraction code is replaced by the real parser in Phase 1. The `EsqlQueryResponse` shortcut is replaced when the language produces its own result types.

## Estimated Scope

Small. 7 new files (6 Java + 1 Gradle), 2 one-line edits to existing files, ~200-250 lines of Java + ~20 lines of Gradle.