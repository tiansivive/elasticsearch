---
tags: [data, runtime, implemented, lucene]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Boolean Block Fix

`Shard.stream` uses `BooleanBlock.Builder` for boolean fields instead of `LongBlock.Builder`. Lucene stores booleans as longs; the fix reads the long 0/1 and writes it as `boolean` into a `BooleanBlock`. `Page.toList` dispatches on `BooleanBlock` → `BooleanVal`. Initially overcomplicated by threading column types through all value types — the fix was simply to use the right Block type. Lesson: explore the API surface (Block types available in compute engine) before adding abstraction layers.

**Depends on**: [[shard-stream.data]]
**Enables**: (none directly)
**Connections**:
- related: debugging lesson — evaluator trusts the type checker (D-025); block type IS the type at the columnar level, no runtime type metadata needed
- related: [[esql-value-converter.esql]] — both deal with mapping between ES columnar types and piescript value types
