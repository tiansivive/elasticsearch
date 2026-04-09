---
tags: [types, polymorphism, theoretical]
refs:
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Impredicative Types

Impredicative type instantiation (polymorphic values in polymorphic positions).
- In a predicative system, type variables can only be instantiated with monotypes
- Impredicativity allows instantiating a type variable with a polymorphic type, e.g., `id @(forall a. a -> a)`
- GHC's Quick Look approach offers a practical compromise for limited impredicativity

**Depends on**: [[higher-rank.types]], [[forall-type.types]]
**Enables**: (none)
**Connections**:
- overlaps: [[higher-rank.types]] — GHC's Quick Look approach
- related: [[bidir-checking.types]] — impredicative instantiation typically requires annotations or checking mode
