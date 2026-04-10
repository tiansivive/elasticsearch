---
tags: [paper-trail, comparable, design-pattern]
refs:
  - session:b0063333-71f1-4ce6-8d31-a5d0d93b9b50
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Elm Architecture Session

Brief comparison of piescript's philosophy with the Elm Architecture — Model/Update/View pattern, pure functions, effects as data. While different domains, the shared insight: effects described as data (free monad / Cmd in Elm), pure core with effectful shell.

**Connections**:
- related: [[free-monad.types]] — both use effects-as-data pattern
- related: [[purity.language]] — shared philosophy of pure core + effectful boundary
