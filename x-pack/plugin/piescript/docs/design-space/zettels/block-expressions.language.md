---
tags: [syntax, language, implemented]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Block Expressions

Sequenced expressions with top-level bindings — piescript's equivalent of do-notation. A block is a series of let-bindings and expressions where each binding is in scope for everything that follows. Top-level program bindings desugar to nested `CoreLet` nodes. Enables writing multi-step programs without deeply nested let/in chains.

**Depends on**: [[core-ir.language]]
**Enables**: (none directly)
**Connections**:
- related: [[pipe-operator.language]] — pipes compose left-to-right; blocks compose top-to-bottom
