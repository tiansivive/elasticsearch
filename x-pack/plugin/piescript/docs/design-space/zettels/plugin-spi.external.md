---
tags: [external, designed]
refs:
  - vision:external-interaction-model
  - session:80f0b64a-5e21-4b2e-acda-fabde482cc87
---
# Plugin SPI

Any ES plugin registers piescript builtins with type schemes, arities, and Java implementations via `PiescriptExtension` interface. Users get type-safe builtins. Enables Kafka, HTTP, ML inference, custom connectors without changing piescript core.

The `PiescriptExtension` interface exposes three methods: `typeSchemes()` returning a map of qualified names (e.g. `"Kafka.produce"`) to `TypeScheme` objects, `arities()` returning the curried argument count for each function (used by the evaluator to know when a partial application is fully saturated), and `execute(name, args, listener)` for the async Java implementation that receives an `ActionListener` for non-blocking completion.

Type schemes use the same `TypeScheme`/`MonoType`/`RowType` classes as the Prelude — plugin authors define polymorphic types using Rigids (type variables) and kind annotations. For example, a Kafka producer might declare `forall a. (Serializable a) => Topic -> a -> IO ()`, expressed as a `TypeScheme` with a `Rigid` for `a` and a typeclass constraint. The elaborator validates calls against plugin type schemes identically to built-in functions: unification, constraint solving, and arity checking all apply uniformly.

Plugins register via ES's standard `java.util.ServiceLoader` mechanism (a `META-INF/services/org.elasticsearch.xpack.piescript.PiescriptExtension` file listing the implementation class). Discovery happens at node startup; the Prelude is augmented with all discovered extensions before any script is elaborated.

**Depends on**: [[prelude.language]], [[es-plugin.infrastructure]]
**Enables**: (none directly)
**Connections**:
- related: Layer 2 of external interaction model — each integration is typed and sandboxed
- related: [[ffi-painless.external]] — both are external interaction layers; SPI for typed builtins, FFI for raw Java calls
- related: [[module-system.tooling]] — plugin builtins extend the namespace similar to module imports
