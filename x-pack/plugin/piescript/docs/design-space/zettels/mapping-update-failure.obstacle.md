---
tags: [data, write-path, es-internals, obstacle, open, concept]
refs:
  - adr:D-051
  - code:EvalWrite.java
---
# Mapping Update Failure

When `Shard.write` encounters a field not in the index mapping, Elasticsearch returns `MAPPING_UPDATE_REQUIRED` from `IndexShard.applyIndexOperationOnPrimary()`. Currently this surfaces as an `EvaluationException` -- the write fails and the error propagates to the caller.

Two future paths:
1. **Handle dynamic mapping updates**: the write primitive triggers a mapping update request (async cluster state change), waits for acknowledgement, then retries the write. Complex because mapping updates are cluster-wide and may conflict with concurrent writes from other nodes.
2. **Require strict mappings**: piescript writes only succeed against indices with explicit mappings for all fields. The type system could enforce this -- if the index row type `r` is known at elaboration time, the write record type must be a subtype of `r`. Unmapped fields would be a type error, not a runtime error.

Path 2 aligns better with piescript's philosophy of catching errors early via the type system. Path 1 is the ES-conventional approach but introduces async complexity into the write primitive.

**Depends on**: [[shard-write.data]], [[primary-shard-write.data]]
**Enables**: (none directly -- obstacle to resolve)
**Connections**:
- implements: [[shard-write.data]] -- mapping update failure is a failure mode of shard-level writes
- implements: [[primary-shard-write.data]] -- bypassing transport means no dynamic mapping update path
- part-of: [[error-handling.thread]] -- a concrete error case that needs a resolution strategy
- informs: [[row-polymorphism.types]] -- strict-mapping path would use row subtyping to prevent unmapped field writes
- informs: [[create-vs-index.data]] -- CREATE semantics interact with mapping updates differently than INDEX
- tension-with: [[es-conventions-debt.infrastructure]] -- piescript bypasses the standard write path that handles mapping updates
- informs: [[write-context.data]] -- WriteContext abstraction would need to address mapping update handling
