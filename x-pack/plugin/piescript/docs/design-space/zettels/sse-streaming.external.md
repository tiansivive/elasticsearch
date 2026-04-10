---
tags: [external, streaming, designed, feature, concept, needs-design, someday]
refs:
  - vision:external-interaction-model
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
  - thread:external-interaction
---
# SSE Streaming

`GET _piescript/{id}/channels/{name}/stream` returns Server-Sent Events. Script sends values to [[channels.infrastructure|channel]]; SSE connection drains incrementally. Enables Kibana subscribing to scored batches.

- **Backpressure**: bounded buffer with configurable overflow policy (drop-oldest or block) is likely the right default.
- **Client disconnect handling**: periodic heartbeat comments (`: keepalive\n\n`), TCP-level disconnect detection, channel subscription cleanup.
- **Reconnection**: SSE natively supports `Last-Event-ID`; if the server assigns monotonic IDs, a reconnecting client can resume — but this requires the server to retain a replay buffer, adding memory pressure. Without replay, reconnection restarts from the current position.

- **Serialization**: each SSE `data:` frame contains JSON-encoded value (matching piescript's XContent [[serialization.infrastructure|serialization]]).

This zettel depends on [[multi-value-channels.coordination|multi-value channels]] because single-value channels complete after one [[send.coordination|send]] -- SSE needs a stream of values to be useful.

**Depends on**: [[named-channels.lifecycle]], [[multi-value-channels.coordination]]
**Enables**: (none directly)
**Connections**:
- part-of: [[external-interaction-model.roadmap]]
- related: [[multi-value-channels.coordination]] — requires multi-value channels for streaming output; incremental results without polling
- complements: [[eager-materialization.data]] — SSE streaming is the client-facing complement to solving eager materialization
