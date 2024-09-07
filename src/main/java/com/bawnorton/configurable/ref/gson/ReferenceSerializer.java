package com.bawnorton.configurable.ref.gson;

import com.bawnorton.configurable.ref.Reference;
import com.google.gson.*;
import java.lang.reflect.Type;

public class ReferenceSerializer implements JsonSerializer<Reference<?>> {
    @Override
    public JsonElement serialize(Reference<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.get());
    }
}
