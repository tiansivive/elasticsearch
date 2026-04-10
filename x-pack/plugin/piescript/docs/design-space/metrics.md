# Design Space Metrics

Derived metrics for understanding the design space. None of these are stored on
individual items — they're computed from the graph structure.

## Priority (dependency count)

An item's priority is proportional to how many other items depend on it.
Items with many incoming `Depends on` links are bottlenecks — they block
the most downstream work.

```bash
# Find highest-priority items (most depended-on)
grep -roh '\[\[.*\]\]' docs/design-space/zettels/ | sort | uniq -c | sort -rn | head -20
```

## Hub score (master nodes)

Zettels with high combined incoming + outgoing link counts are hubs — connection
points that tie many concepts together. High hub score is emergent, not assigned.

```bash
# Find hub zettels (most total connections)
cd docs/design-space/zettels
for f in *.md; do
  name="${f%.md}"
  incoming=$(grep -roh "\[\[$name\]\]" *.md | wc -l)
  outgoing=$(grep -oh '\[\[.*\]\]' "$f" | wc -l)
  total=$((incoming + outgoing))
  echo "$total $incoming↓ $outgoing↑ $name"
done | sort -rn | head -20
```

## Staleness

Items tagged `open` or `designed` that have no recent `session:` refs may be
forgotten. Cross-reference with recent session IDs to find stale items.

## Cluster density

Items with many mutual connections form natural "work packages" — groups
that should be tackled together. In Obsidian, these appear as dense clusters
in the graph view.

Example: `error-handling`, `pattern-matching`, `adts`, and `result-types`
form a tight cluster — implementing any one without the others has limited value.

## Maturity distribution

The ratio of `implemented` to `open` items within a concern tag shows which
areas are mature vs underexplored.

```bash
# Count items per concern × maturity
for tag in language types esql data infrastructure lifecycle external; do
  echo "=== $tag ==="
  grep -rl "$tag" docs/design-space/zettels/ | while read f; do
    grep "maturity" "$f" | head -1
  done | sort | uniq -c
done
```

## Blocking depth

The longest dependency chain to a non-implemented item. Deep chains signal
architectural risk — a change of mind at the root cascades through everything.

## ES-internals code-ref hints

Zettels tagged `es-internals` whose `refs` lack a repo-relative `code:` path under `x-pack/`,
`server/`, or `libs/` (piescript-only `code:` counts as a gap for *upstream* navigation):

```bash
./scripts/catalog.py --es-code-gaps
```

This is a **hint list**, not a quality score — some items intentionally cite only `resource:` or short piescript `code:` names.

## Orphan items

Items with no `Depends on` and no `Enables` links. These are either:
- **Foundational** — they stand alone (e.g., theoretical references)
- **Disconnected** — they should be linked but aren't (review and fix)
