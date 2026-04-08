---
tags: [external, streaming, designed]
refs:
  - vision:external-interaction-model
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# SSE Streaming

GET _piescript/{id}/channels/{name}/stream returns Server-Sent Events. Script sends values to channel; SSE connection drains incrementally. Enables Kibana subscribing to scored batches.

Open design questions center on the mismatch between a script's production rate and a client's consumption rate. Backpressure: when the client consumes slower than the script produces, should the system buffer events (risk: unbounded memory), drop oldest/newest events (risk: data loss), or block the script's channel send until the client catches up (risk: slowing the entire computation)? A bounded buffer with configurable overflow policy (drop-oldest or block) is likely the right default.

Client disconnect handling: the SSE endpoint should send periodic heartbeat comments (`: keepalive\n\n`) and detect TCP-level disconnection. On disconnect, the channel subscription is cleaned up so the script does not block on a dead consumer. Whether the channel itself is closed or merely unsubscribed depends on whether other consumers exist.

Reconnection: SSE natively supports `Last-Event-ID`. If the server assigns monotonic IDs to each event, a reconnecting client can resume from where it left off — but this requires the server to retain a replay buffer, adding memory pressure. Without replay, reconnection restarts from the current position.

Serialization format: each SSE `data:` frame would contain a JSON-encoded value (matching piescript's XContent serialization). For large row sets, chunked XContent or NDJSON could reduce per-event framing overhead.

This zettel depends on multi-value channels because single-value channels complete after one send — SSE needs a stream of values to be useful.

**Depends on**: [[named-channels.lifecycle]], [[multi-value-channels.coordination]]
**Enables**: (none directly)
**Connections**:
- related: [[multi-value-channels.coordination]] — requires multi-value channels for streaming output; incremental results without polling
- related: [[eager-materialization.data]] — SSE streaming is the client-facing complement to solving eager materialization
