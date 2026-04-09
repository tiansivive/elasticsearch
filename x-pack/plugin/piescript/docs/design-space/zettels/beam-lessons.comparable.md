---
tags: [comparable, beam, prior-art, reference, scheduling, mem-management, fault-tolerance]
refs:
  - doc:references.md
---
# BEAM/Erlang Runtime Lessons

BEAM demonstrates that a functional language runtime CAN be a production-grade distributed system. Key lessons for piescript:

- Preemptive scheduling with reduction counting (fairness without cooperation)
- Per-process GC with independent memory lifecycles (no global pauses)
- [[hot-code-loading.beam]] for live upgrades (module versioning with lazy migration)
- [[otp-supervision.coordination]] trees for fault tolerance (let-it-crash with structured recovery)

These are not features to copy but existence proofs that the design space piescript occupies is viable at production scale.

**Depends on**: (none)
**Enables**: [[hot-code-loading.beam]], [[preemptive-scheduling.beam]], [[per-process-gc.beam]]
**Connections**:
- subsumes: [[otp-supervision.coordination]] — OTP supervision is one facet of the broader BEAM lessons
- analogous-to: [[join-calculus.coordination]] — BEAM's message-passing concurrency model is a pragmatic realization of similar ideas to the join calculus
