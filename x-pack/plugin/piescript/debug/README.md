# Running Elasticsearch for Development

## Single node (security enabled, Kibana-compatible)

```bash
./gradlew run -Drun.license_type=trial
```

Default user: `test_user` / `x-pack-test-password` (superuser via `_es_test_root` role).

### Setting up Kibana

Once the cluster is up, set the `kibana_system` password:

```bash
curl -u test_user:x-pack-test-password -X POST 'localhost:9200/_security/user/kibana_system/_password' \
  -H 'Content-Type: application/json' -d '{"password": "kibana"}'
```

Then in Kibana's `kibana.yml`:

```yaml
elasticsearch.hosts: ["http://localhost:9200"]
elasticsearch.username: "kibana_system"
elasticsearch.password: "kibana"
```

Or use a service token instead:

```bash
curl -u test_user:x-pack-test-password -X POST 'localhost:9200/_security/service/elastic/kibana/credential/token/dev'
```

And in `kibana.yml`:

```yaml
elasticsearch.hosts: ["http://localhost:9200"]
elasticsearch.serviceAccountToken: "<token_value>"
```

Login to Kibana as `test_user` / `x-pack-test-password`.

## Single node (security disabled)

```bash
./gradlew run -Drun.license_type=trial -Dtests.es.xpack.security.enabled=false
```

No auth required. Used by `test-eval.sh`.

## Multi-node (3 nodes, security disabled)

Requires a `multinode.gradle` init script:

```bash
./gradlew run -Drun.license_type=trial -Dtests.es.xpack.security.enabled=false \
  -I ../scripts/es-dev-config/multinode.gradle
```

Used by `setup-test-index-multinode.sh` and `test-multinode.sh`.

## Custom settings

Any `-Dtests.es.<setting>` system property is passed through as an ES setting
(the `tests.es.` prefix is stripped). Examples:

```bash
-Dtests.es.xpack.security.enabled=false
-Dtests.es.cluster.routing.allocation.disk.threshold_enabled=false
```
