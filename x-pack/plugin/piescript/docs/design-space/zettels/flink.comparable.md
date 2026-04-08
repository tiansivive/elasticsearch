---
tags: [comparable, theoretical]
refs:
  - doc:data-access.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: Apache Flink

Apache Flink: stateful stream processing with exactly-once guarantees, persistent state, complex windowing. Piescript's channels enable streaming patterns but Flink retains advantages for persistent state, exactly-once, and windowing. Piescript is not competing — it addresses the "about ES data but ESQL can't express the computation" workloads.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- contrasts-with: [[exchange-streaming.infrastructure]] — Exchange provides backpressure streaming but not Flink's guarantees
- related: [[multi-value-channels.coordination]] — if implemented would move closer but still wouldn't match exactly-once
