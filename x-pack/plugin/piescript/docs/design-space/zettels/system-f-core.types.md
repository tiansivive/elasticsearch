---
tags: [types, language, implemented, polymorphism, ir]
refs:
  - adr:D-005
  - adr:D-035
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# System F Core

The Core IR is System F with explicit `CoreTypeAbs` (Lambda) and `CoreTypeApp` (@) nodes. The surface language is HM (implicit); the elaborator infers type abstractions at generalization sites and type applications at instantiation sites. Standard compilation model: HM surface in, System F core out.

**Depends on**: [[hindley-milner.types]], [[core-ir.language]]
**Enables**: [[deferred-constraints.types]]
**Connections**:
- related: [[deferred-constraints.types]] — deferred constraint solving decouples constraint generation from solving
- related: `CoreTypeAbs`/`CoreTypeApp` enable future specialization and monomorphization
- related: [[forall-type.types]] — Forall variant in MonoType needed for CoreTypeAbs to express its own type
- related: [[parametricity.types]] — System F's universal quantification is the basis for parametricity guarantees
- related: [[higher-rank.types]] — System F provides the foundation for higher-rank polymorphism
