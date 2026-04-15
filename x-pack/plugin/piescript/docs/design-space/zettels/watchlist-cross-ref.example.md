---
tags: [example, esql, data-processing, security, concurrency, motivation]
refs:
  - session:a55b26b4-ec4e-4101-bd8a-444709bcd8ba
  - session:a4c44992-3966-4627-a399-19f52f7da836
  - thread:language-expressiveness
---
# Watchlist Cross-Reference — Real-World Example

A real use case from active development on **Elastic Security's Watchlists** feature
(Entity Analytics). Watchlists sync privileged users from integration sources (Okta, AD)
into an internal index. Validating correctness requires cross-referencing the watchlist
index against the source indices — which users are in the sources but missing from the
watchlist? Which are in the watchlist but no longer in any source?

## The problem today (ESQL only)

Requires **7 separate ESQL queries** run manually, with results compared by eyeballing
entity IDs across query outputs. From a real Cursor session (a55b26b4):

1. Query the watchlist index for all synced entities
2. Query Okta integration for privileged users (role-based filter)
3. Query AD integration for privileged users (boolean field)
4. Query watchlist again filtered by source
5. Count comparison (watchlist vs Okta vs AD)
6–7. Separate count queries per source

The cross-referencing ("compare entity.name values against query 2") is done manually.
No programmatic join, no single result, no automation.

## With piescript — one program, concurrent, type-checked

```piescript
use ".entity_analytics.watchlists.default" as watchlist;
use "logs-entityanalytics_okta.user-default" as okta;
use "logs-entityanalytics_ad.user-default" as ad;

-- Run all 3 queries concurrently
let wl_ch = spawn query ESQL.from watchlist
  |> ESQL.keep (fn r -> { id: r.entity.id, name: r.entity.name });

let okta_ch = spawn query ESQL.from okta
  |> ESQL.where (fn r -> r.user.roles == "Super Administrator"
                      || r.user.roles == "Organization Administrator"
                      || r.user.roles == "Group Administrator"
                      || r.user.roles == "Application Administrator")
  |> ESQL.statsBy
       (fn r -> { latest: ESQL.max r.@timestamp })
       (fn r -> { name: r.user.name, id: r.user.id });

let ad_ch = spawn query ESQL.from ad
  |> ESQL.where (fn r -> r.entityanalytics_ad.user.privileged_group_member == true)
  |> ESQL.statsBy
       (fn r -> { latest: ESQL.max r.@timestamp })
       (fn r -> { name: r.user.name, id: r.user.id });

-- Synchronize and cross-reference
when (wl_ch wl) & (okta_ch okta_users) & (ad_ch ad_users) ->
  let source_ids = List.map (fn r -> r.id) okta_users
                ++ List.map (fn r -> r.id) ad_users;
  let wl_ids = List.map (fn r -> r.id) wl;

  let in_source_not_in_wl = List.filter (fn id ->
    not (List.any (fn wid -> wid == id) wl_ids)
  ) source_ids;

  let in_wl_not_in_source = List.filter (fn id ->
    not (List.any (fn sid -> sid == id) source_ids)
  ) wl_ids;

  {
    watchlist_count: List.length wl,
    okta_count: List.length okta_users,
    ad_count: List.length ad_users,
    in_source_not_in_watchlist: in_source_not_in_wl,
    in_watchlist_not_in_source: in_wl_not_in_source
  }
```

## What piescript provides here

| Concern | ESQL only | Piescript |
|---------|-----------|-----------|
| Queries | 7 separate, run manually | 3 queries in one program |
| Concurrency | Sequential (user runs one at a time) | `spawn` runs all 3 in parallel, `when` synchronizes |
| Cross-referencing | Manual eyeballing of IDs | Programmatic set difference via `List.filter` |
| Result | 7 separate result tables | One structured record with counts + mismatches |
| Type safety | None across queries | All 3 indices type-checked from field caps |
| Reusability | Copy-paste queries each time | Save as a program, run on demand |

## Feature context

**Watchlists** is an Entity Analytics feature in Elastic Security (Kibana), currently in
development. It syncs privileged users from external identity providers (Okta, Active
Directory) into an internal Elasticsearch index for risk scoring and alerting. The sync
process filters users by role/privilege and maintains a mapping between source entities
and watchlist entries.

This validation query is needed during development and testing to verify the sync logic
is correct — exactly the kind of ad-hoc cross-index analysis that piescript is built for.

## Piescript features demonstrated

- **Multi-index queries** via `use` declarations with typed `Index r` values
- **ESQL compilation** via `ESQL.from`/`where`/`statsBy`/`keep` combinators
- **Concurrent execution** via `spawn` (3 queries in parallel) + `when` (synchronize)
- **Cross-index joins** via list operations after materialization
- **Structured output** — single record with all results
- **Type safety** — field access type-checked against real index mappings

## Notes

- `++` (list concat) is not yet implemented — tagged `next` on the language-expressiveness
  thread. Would use `List.concat` or similar builtin in the interim.
- The role filter for Okta uses `==` per role; a future `List.any` over a role list would
  be cleaner. Relates to [[string-concat.language]] and list membership operations.
- For larger datasets, this pattern could use Exchange streaming (Block G) instead of
  full materialization.

**Depends on**: (none — this is an example, not a feature)
**Enables**: (none directly)
**Connections**:
- example-of: [[extraction-cliff.external]] — this IS the extraction cliff: 7 manual queries today, 1 program with piescript
- example-of: [[esql-compilation.esql]] — ESQL combinators compile piescript to ESQL queries
- example-of: [[spawn.coordination]] — concurrent query execution
- example-of: [[when-synchronization.coordination]] — multi-query synchronization
- example-of: [[value-proposition.principle]] — composable type-safe pipelines replacing manual multi-query workflows
- motivates: [[string-concat.language]] — `++` needed for combining lists from different sources
- motivates: [[target-users.principle]] — security analysts cross-referencing indices is a core use case
