---
tags: [types, tech-debt]
refs:
  - adr:D-032
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Resolve Deep

`TypeWalker.resolveDeep` still used by `CorePrinter` for display. D-032 calls for removing it (violates no-substitution principle). D-035 eliminated its use in `generalize`/`instantiate`. Fix: environment-based Rigid resolution in downstream passes.

**Depends on**: [[zonker.types]]
**Enables**: (none directly)
**Connections**:
- related: [[core-printer.tooling]] — long-standing deviation from D-032; low priority but accumulates coupling
- related: [[force-threading.types]] — force subsumes resolveDeep; resolveDeep is tech debt
