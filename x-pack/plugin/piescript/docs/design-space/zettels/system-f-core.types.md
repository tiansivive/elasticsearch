---
tags: [types, language, implemented, polymorphism, ir, decision, concept]
refs:
  - adr:D-005
  - adr:D-035
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:CoreTypeAbs.java
  - code:CoreTypeApp.java
---
# System F Core

The [[core-ir.language|Core IR]] is System F with explicit `CoreTypeAbs` (Lambda) and `CoreTypeApp` (@) nodes. The surface language is [[hindley-milner.types|HM]] (implicit); the [[elaboration-architecture.types|elaborator]] infers type abstractions at generalization sites and type applications at instantiation sites. Standard compilation model: HM surface in, System F core out.

**Depends on**: [[hindley-milner.types]], [[core-ir.language]]
**Enables**: [[deferred-constraints.types]]
**Connections**:
- part-of: [[phase-1.roadmap]]
- enables: [[deferred-constraints.types]] — deferred constraint solving decouples constraint generation from solving
- enables: future specialization and monomorphization via `CoreTypeAbs`/`CoreTypeApp`
- uses: [[forall-type.types]] — Forall variant in MonoType needed for CoreTypeAbs to express its own type
- prerequisite-for: [[parametricity.types]] — System F's universal quantification is the basis for parametricity guarantees
- prerequisite-for: [[higher-rank.types]] — System F provides the foundation for higher-rank polymorphism
