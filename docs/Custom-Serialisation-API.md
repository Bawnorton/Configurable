# Custom Serialisation API

This is you can register custom type serialisation in Configurable for:
- JSON5 config files
- TOML config files
- network `ByteBuf` sync

Registration **must** happen before serialisation is first used. Once bootstrapped, the registry is frozen and later registrations throw an `IllegalStateException`.

Because configs can load very early, **ServiceLoader/Fabric Entrypoint registration is the recommended approach**. 
Programmatic registration is possible but you must ensure it happens before any config loading, for example by registering in a mixin config.

## TypedReader/Writer

The API is based around `TypedReader` and `TypedWriter` instances, which are registered for a specific type. 
These readers/writers can be contextless or context-aware where the context is the expected type of the value being 
read/written. For example a `TypedReader` can be context-aware to handle generics of the element being read or for
polymorphic handling of supertypes.

## ServiceLoader / Fabric Entrypoint Bootstrap 

Create a bootstrap class:

```java
package com.example.mod.config;

import com.bawnorton.configurable.api.serialisation.SerialisationBootstrap;
import com.bawnorton.configurable.api.serialisation.SerialisationRegistrar;
import com.bawnorton.configurable.io.typed.TypedReader;
import com.bawnorton.configurable.io.typed.TypedWriter;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.codec.ByteBufCodecs;

public final class ExampleSerialisationBootstrap implements SerialisationBootstrap {
    @Override
    public void bootstrap(SerialisationRegistrar registrar) {
        registrar.registerType(
                MyType.class,
                TypedReader.<MyType>create()
                        .json(TypedReader.JsonReader.contextless(element -> new MyType(element.getAsString())))
                        .toml(TypedReader.TomlReader.contextless((config, path) -> new MyType(config.get(path))))
                        .object(TypedReader.ObjectReader.contextless(item -> {
                            if (item instanceof MyType typed) return typed;
                            return new MyType(item.toString());
                        }))
                        .byteBuf(TypedReader.ByteBufReader.contextless(buf -> new MyType(ByteBufCodecs.STRING_UTF8.decode(buf)))),
                TypedWriter.<MyType>create()
                        .json(TypedWriter.JsonWriter.contextless(value -> new JsonPrimitive(value.value())))
                        .toml(TypedWriter.TomlWriter.contextless(MyType::value))
                        .byteBuf(TypedWriter.ByteBufWriter.contextless((buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.value())))
        );
    }

    @Override
    public int priority() {
        return 0; // Lower priority bootstraps run first, to override existing registrations return a higher priority value.
    }
}
```


Then add the ServiceLoader file:

`META-INF/services/com.bawnorton.configurable.api.serialisation.SerialisationBootstrap`

with contents:

```text
com.example.mod.config.ExampleSerialisationBootstrap
```

or on Fabric add the entrypoint to `fabric.mod.json`:

```json
{
  "entrypoints": {
    "configurable-serialisation": [
      "com.example.mod.config.ExampleSerialisationBootstrap"
    ]
  }
}
```

## Programmatic Registration 

If you control init ordering and register very early, for example in a mixin config, then you can register programmatically:

```java
SerialisationApi.registerType(
        MyType.class,
        TypedReader.<MyType>create()
                .json(TypedReader.JsonReader.contextless(element -> new MyType(element.getAsString())))
                .toml(TypedReader.TomlReader.contextless((config, path) -> new MyType(config.get(path))))
                .object(TypedReader.ObjectReader.contextless(item -> new MyType(item.toString())))
                .byteBuf(TypedReader.ByteBufReader.contextless(buf -> new MyType(ByteBufCodecs.STRING_UTF8.decode(buf)))),
        TypedWriter.<MyType>create()
                .json(TypedWriter.JsonWriter.contextless(value -> new JsonPrimitive(value.value())))
                .toml(TypedWriter.TomlWriter.contextless(MyType::value))
                .byteBuf(TypedWriter.ByteBufWriter.contextless((buf, value) -> ByteBufCodecs.STRING_UTF8.encode(buf, value.value())))
);
```

## Resolver-Based Registration

If you need to handle more complex types you can register a resolver, this is handy for collection based types:
```java
SerialisationApi.registerResolver(genericType -> {
    if (genericType.type() == MyType.class) {
        // Return a TypedHandler<?> if you need dynamic behavior.
    }
    return null;
});
```

## JSON/TOML Writer Requirements

For save support, you must provide:
- `TypedWriter.json(...)` -> returns a `JsonElement`
- `TypedWriter.toml(...)` -> returns a TOML-safe object (`String`, `Number`, `Boolean`, `List`, etc.)

If a writer is missing, save operations for that format throw `UnsupportedOperationException`.

## ByteBuf Notes

`TypedHandler` wraps ByteBuf values with internal null markers, so your `byteBuf` reader/writer only handles non-null payload values.
