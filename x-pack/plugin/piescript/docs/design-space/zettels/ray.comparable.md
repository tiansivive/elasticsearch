---
tags: [comparable, theoretical]
refs:
  - doc:data-access.md
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Comparable: Ray

Ray: distributed tasks/actors + futures. spawn/when/send map closely to Ray's task model. Piescript is the ES-native equivalent for data-centric distributed tasks. Ray retains advantages for GPU integration and heterogeneous compute resources.

**Depends on**: (none)
**Enables**: (none directly)
**Connections**:
- related: spawn ~ Ray remote task, when ~ ray.get on multiple futures, send ~ actor method call — the mapping is close
- related: [[data-locality.distributed]] — Ray also ships compute to data; piescript is the ES-native equivalent
- related: [[spawn.coordination]] — piescript's spawn maps directly to Ray's remote task submission
