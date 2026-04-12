---
tags: [language, evaluation, channels, open, question, needs-design, later]
refs: []
---
# Top-Level Channel Return Type

If a piescript program's final expression evaluates to `Channel t`, the response is opaque and useless to the caller -- a channel is a coordination handle, not a meaningful result. The transport layer would serialize a `ChannelVal(nodeId, channelId)` which has no meaning outside the cluster.

Three options have been identified, none yet chosen:

1. **Elaborator warning** -- emit a warning during elaboration when the program's return type is `Channel t`. Does not prevent the problem, just flags it.
2. **Transport auto-unwrap** -- the transport layer detects `Channel t` as the result type and automatically waits on the channel before serializing the response. This is implicit and potentially surprising (what if the channel never fires?).
3. **Type-level rejection** -- reject programs whose return type is `Channel t` at the type level. Requires distinguishing "top-level return position" from other positions, which complicates the type system.

The question interacts with response type evolution (how the transport serializes results) and the evaluator (should it auto-wait?). Currently undecided.

**Depends on**: [[channels.infrastructure]], [[spawn.coordination]]
**Enables**: (none directly)
**Connections**:
- informs: [[spawn.coordination]] -- spawn returns a channel; if the program is just `spawn expr`, the result is a channel
- informs: [[response-type-evolution.infrastructure]] -- the transport layer must handle or reject channel results
- tension-with: [[evaluator.language]] -- should the evaluator auto-wait on channel results at the top level?
- related: [[when-synchronization.coordination]] -- `when` is the user-level mechanism for waiting on channels; the question is whether to require it at top level
- related: [[fire-and-forget.coordination]] -- if spawn is fire-and-forget, a bare `spawn expr` at top level returns immediately with an opaque handle
- tension-with: [[purity.language]] -- auto-unwrap would be an implicit effect at the program boundary
