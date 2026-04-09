---
tags: [tooling, lifecycle, open, feature, concept]
refs:
  - roadmap:phase-7
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Stored Functions

Persistent function storage in cluster state or system index. Piescript functions need a persistence mechanism for reuse across requests: small definitions in cluster state (like stored scripts), larger [[module-system.tooling|modules]] in a system index. This is the foundation for a module ecosystem and [[content-addressed-code.tooling|content-addressed code]] storage.

**Depends on**: [[module-system.tooling]]
**Enables**: [[content-addressed-code.tooling]]
**Connections**:
- part-of: [[phase-7.roadmap]]
- inspired-by: [[es-plugin.infrastructure]] — follows ES stored scripts pattern (cluster state for small defs, system index for large)
- prerequisite-for: [[scheduled-execution.lifecycle]] — scheduled programs need persistent function storage
