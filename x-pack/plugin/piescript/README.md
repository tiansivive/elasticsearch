# Piescript — Phase 0: Plugin Scaffold

Typed functional scripting language for Elasticsearch. Phase 0 is a minimal plugin scaffold that passes ESQL queries through to the ESQL engine via a new REST endpoint.

## Documentation

Detailed project documentation lives in the [`docs/`](docs/) folder:

| Doc | Purpose |
|-----|---------|
| [`docs/AGENTS.md`](docs/AGENTS.md) | Agent entry point — start here for full project context |
| [`docs/vision.md`](docs/vision.md) | Long-term goals and design philosophy |
| [`docs/roadmap.md`](docs/roadmap.md) | Phased development plan with status markers |
| [`docs/current-state.md`](docs/current-state.md) | What's implemented now, limitations, next steps |
| [`docs/architecture.md`](docs/architecture.md) | System design, components, data flow |
| [`docs/project-structure.md`](docs/project-structure.md) | File layout and module responsibilities |
| [`docs/decisions.md`](docs/decisions.md) | Architectural decisions and rationale |
| [`docs/references.md`](docs/references.md) | π-calculus papers, textbooks, and theory |

## Quick Reference

### Run Automated Tests

```bash
./gradlew :x-pack:plugin:piescript:javaRestTest
```

### Compile Only

```bash
./gradlew :x-pack:plugin:piescript:compileJava
```

This only compiles piescript and any stale upstream dependencies. You never need to build all of Elasticsearch explicitly — Gradle handles incremental builds automatically.

### Precommit Checks (formatting, forbidden APIs, etc.)

```bash
./gradlew :x-pack:plugin:piescript:check
```

This runs all precommit checks + tests for just the piescript module. To only check or fix formatting:

```bash
./gradlew :x-pack:plugin:piescript:spotlessJavaCheck
# To auto-fix:
./gradlew :x-pack:plugin:piescript:spotlessApply
```

### Manual Testing

Start a local single-node cluster with trial license (includes piescript):

```bash
./gradlew run -Drun.license_type=trial
```

This starts Elasticsearch on `localhost:9200` with security enabled. Default superuser credentials: `elastic-admin` / `elastic-password`.

**1. Create a test index:**

```bash
curl -u elastic-admin:elastic-password -X POST "localhost:9200/test-index/_bulk?refresh=true" \
  -H "Content-Type: application/json" \
  -d '
{"index":{}}
{"message":"hello","status":200}
{"index":{}}
{"message":"world","status":500}
{"index":{}}
{"message":"error","status":503}
'
```

**2. Run a piescript program (ESQL passthrough):**

```bash
curl -u elastic-admin:elastic-password -X POST "localhost:9200/_piescript/eval" \
  -H "Content-Type: application/json" \
  -d '{"program": "query FROM test-index | WHERE status >= 500 | LIMIT 10;"}'
```

Expected response (columnar JSON, same format as `POST /_query`):

```json
{
  "columns": [
    {"name": "message", "type": "text"},
    {"name": "message.keyword", "type": "keyword"},
    {"name": "status", "type": "long"}
  ],
  "values": [
    ["world", "world", 500],
    ["error", "error", 503]
  ]
}
```

**3. Error cases:**

```bash
# Missing query prefix
curl -u elastic-admin:elastic-password -X POST "localhost:9200/_piescript/eval" \
  -H "Content-Type: application/json" \
  -d '{"program": "FROM test-index;"}'
# -> 400: "program must start with 'query'"

# Empty program
curl -u elastic-admin:elastic-password -X POST "localhost:9200/_piescript/eval" \
  -H "Content-Type: application/json" \
  -d '{"program": ""}'
# -> 400: "[program] is required"
```

**Without security** (add to the `run` command):

```bash
./gradlew run -Dtests.es.xpack.security.enabled=false -Drun.license_type=trial
```

Then omit `-u elastic-admin:elastic-password` from curl commands.

## Phase 0 Limitations

- No real language parsing — only strips `query ... ;` wrapper and passes ESQL verbatim
- No semicolons allowed inside the ESQL query string (e.g., string literals containing `;`)
- No async query support
- Response is ESQL's native format (no piescript-specific response wrapper)
- No feature flag or license gating

See [docs/current-state.md](docs/current-state.md) for the full list of limitations and known shortcuts.
