---
tags: [beam, scheduling, open, exploration, reference]
refs: []
---
# Hot Code Loading

BEAM's ability to upgrade running code without stopping processes.
- Modules are versioned; a process running old code continues executing the old version until it makes a fully-qualified call (Module:Function), which routes to the new version
- At most two versions coexist
- This enables zero-downtime upgrades -- an existence proof that live code replacement is tractable in a concurrent functional runtime

**Depends on**: (none)
**Enables**: (none)
**Connections**:
- part-of: [[beam-lessons.comparable]] — one of the four key BEAM runtime lessons
- analogous-to: [[stored-functions.tooling]] — piescript's stored functions would face the same versioning questions if they support live updates
