---
tags: [comparable, theoretical, prior-art, decision]
refs:
  - doc:archive/data-access.pre-threads.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: Ray

Ray: distributed tasks/actors + futures. [[spawn.coordination|spawn]]/[[when-synchronization.coordination|when]]/[[send.coordination|send]] map closely to Ray's task model. Piescript is the ES-native equivalent for data-centric distributed tasks. Ray retains advantages for GPU integration and heterogeneous compute resources.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- analogous-to: [[spawn.coordination]] — piescript's spawn maps directly to Ray's remote task submission
- analogous-to: [[when-synchronization.coordination]] — when ~ ray.get on multiple futures
- analogous-to: [[send.coordination]] — send ~ actor method call
- analogous-to: [[data-locality.distributed]] — Ray also ships compute to data; piescript is the ES-native equivalent
