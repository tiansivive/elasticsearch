---
tags: [lifecycle, external, designed, feature, concept, needs-design, someday]
refs:
  - vision:external-interaction-model
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:distributed-coordination
  - thread:external-interaction
---
# Named Channels

Scripts expose named [[channels.infrastructure]] on specific nodes. External systems POST values to them. Token is the capability (see [[token-capability-security.security]]). Enables interactive data exploration and pipeline orchestration.

- Token-based access avoids coupling to ES's RBAC model for script-level permissions
- Any bearer of the token can read from or write to channels, regardless of ES user identity
- Tokens can be forwarded to downstream systems without granting ES privileges

Open questions around tokens:

- **Generation** -- cryptographically random vs derived from script ID + secret
- **Distribution** -- returned in submit response body, or via separate `GET _piescript/{id}/token` endpoint
- **Revocation** -- TTL-based expiry, explicit revoke call, or automatic invalidation on termination
- **Scoping** -- one token per script vs fine-grained per-channel tokens

Internally, named channels map to entries in [[channel-registry.infrastructure]] keyed by user-chosen names instead of system-generated UUIDs. The REST layer resolves `_piescript/{id}/channels/{name}` to the corresponding registry entry; the channel itself is the same `Channel a` infrastructure used for inter-actor communication.

**Depends on**: [[actor-model.lifecycle]]
**Enables**: [[sse-streaming.external]]
**Connections**:
- part-of: [[external-interaction-model.roadmap]]
- related: [[channels.infrastructure]] — Layer 1 detail; internally everything is channels, REST is just an HTTP skin
- related: [[channel-registry.infrastructure]] — named channels map to registry entries keyed by user-chosen names instead of UUIDs
- contrasts-with: [[security-namespace.infrastructure]] — token-based capability access is orthogonal to ES's RBAC security namespace
- related: [[token-capability-security.security]] — token is the external capability for channel access
