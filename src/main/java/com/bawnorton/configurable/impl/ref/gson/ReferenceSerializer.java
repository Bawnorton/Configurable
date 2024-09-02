package com.bawnorton.configurable.impl.ref.gson;

import com.bawnorton.configurable.impl.ref.Reference;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class ReferenceSerializer implements JsonSerializer<Reference<?>> {
    private final Supplier<Gson> gsonSupplier;

    public ReferenceSerializer(Supplier<Gson> gsonSupplier) {
        this.gsonSupplier = gsonSupplier;
    }

    @Override
    public JsonElement serialize(Reference<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return gsonSupplier.get().toJsonTree(src.get());
    }
}
