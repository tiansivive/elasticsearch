---
tags: [infrastructure, security, es-internals, implemented, decision, transport-layer]
refs:
  - adr:D-055
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - code:x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/PiescriptAction.java
  - code:x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/PiescriptSendAction.java
  - code:x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/PiescriptPlugin.java
  - code:x-pack/plugin/piescript/src/main/java/org/elasticsearch/xpack/piescript/RestPiescriptAction.java
  - code:x-pack/plugin/security/src/main/java/org/elasticsearch/xpack/security/authz/RBACEngine.java
  - code:x-pack/plugin/security/src/main/java/org/elasticsearch/xpack/security/authz/AuthorizationService.java
  - code:x-pack/plugin/security/src/main/java/org/elasticsearch/xpack/security/operator/OperatorPrivileges.java
  - code:x-pack/plugin/security/qa/operator-privileges-tests/src/javaRestTest/java/org/elasticsearch/xpack/security/operator/Constants.java
---
# Security Namespace

- Main action: `cluster:compute/piescript` (cluster-level auth) — declared on **`PiescriptAction`**, registered in **`PiescriptPlugin#getActions`**, exposed over REST via **`RestPiescriptAction`** (refs).
- Send action: `internal:compute/piescript/send` — **`PiescriptSendAction`**, same registration path; see [[transport-layer.es]] for transport handlers.
- **RBAC**: authorization runs through **`AuthorizationService`** and the **`RBACEngine`** (see `refs`); D-055 removed the brittle `indices:data/read/piescript` allowlist story.
- **Operator privileges**: when enabled, operator-only actions are enforced via **`OperatorPrivileges`**; the QA **`Constants`** list includes `"cluster:compute/piescript"` as an operator-allowlisted action name (see `refs` — integration-test catalog of operator action strings).
- [[esql-compilation.esql|ESQL]] queries handle their own index authorization.

**Depends on**: [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- documents: [[transport-layer.es]] — cluster vs internal transport actions (`TransportPiescriptAction` vs `TransportPiescriptSendAction`)
- part-of: [[phase-0.roadmap]]
- solves: previous namespace `indices:data/read/piescript` caused assertion failures in `RBACEngine` with security enabled
- complements: [[auth-checks.elaboration]] — compile-time auth checks complement the runtime namespace
- constrains: [[transport-send.infrastructure]] — send action uses the internal namespace
- constrains: [[eval-endpoint.infrastructure]] — eval endpoint uses the cluster:compute namespace
