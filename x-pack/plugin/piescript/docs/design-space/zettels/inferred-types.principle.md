---
tags: [meta, principle, types, inference, decision]
refs:
  - doc:vision.md
---
# Types Are Inferred, Not Annotated

The programmer writes expressions; the compiler figures out the types via
Hindley-Milner inference. No type annotations are required (but may be
supported for documentation in the future). This keeps the surface syntax
lightweight while providing strong safety guarantees.

**Depends on**: [[hindley-milner.types]]
**Enables**: (none directly)
**Connections**:
- part-of: [[design-principles.hub]]
- implements: [[hindley-milner.types]] — HM inference is the mechanism
- related: [[type-annotations.types]] — optional annotations for documentation, not required
- related: [[elaboration-architecture.types]] — the elaborator is where inference happens
