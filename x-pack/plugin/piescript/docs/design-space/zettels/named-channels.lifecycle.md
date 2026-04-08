---
tags: [lifecycle, external, designed]
refs:
  - vision:external-interaction-model
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Named Channels

Scripts expose named channels on specific nodes. External systems POST values to them. Token is the capability. Enables interactive data exploration and pipeline orchestration.

The token is a capability — possession grants access to all the script's channels. Token-based access avoids coupling to ES's RBAC model for script-level permissions: any bearer of the token can read from or write to channels, regardless of ES user identity. This keeps the security model simple and composable (tokens can be forwarded to downstream systems without granting ES privileges).

Open questions around tokens: generation strategy (cryptographically random vs derived from script ID + secret — random is safer, derived is reproducible), distribution (returned in the submit response body, or via a separate `GET _piescript/{id}/token` endpoint), revocation and expiration (TTL-based expiry, explicit revoke call, or automatic invalidation when the script terminates), and scoping (one token per script granting access to all channels, or fine-grained per-channel tokens for least-privilege access).

Internally, named channels map to entries in the ChannelRegistry keyed by user-chosen names instead of system-generated UUIDs. The REST layer resolves `_piescript/{id}/channels/{name}` to the corresponding registry entry; the channel itself is the same `Channel a` infrastructure used for inter-actor communication.

**Depends on**: [[actor-model.lifecycle]]
**Enables**: [[sse-streaming.external]]
**Connections**:
- related: [[channels.infrastructure]] — Layer 1 detail; internally everything is channels, REST is just an HTTP skin
- related: [[channel-registry.infrastructure]] — named channels map to registry entries keyed by user-chosen names instead of UUIDs
- related: [[security-namespace.infrastructure]] — token-based capability access is orthogonal to ES's RBAC security namespace
