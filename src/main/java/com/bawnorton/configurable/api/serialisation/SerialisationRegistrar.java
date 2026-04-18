package com.bawnorton.configurable.api.serialisation;

import com.bawnorton.configurable.io.typed.TypedReader;
import com.bawnorton.configurable.io.typed.TypedWriter;
import org.jetbrains.annotations.Nullable;

public interface SerialisationRegistrar {
	<T> void registerType(Class<T> boxedType, @Nullable Class<T> primitiveType, TypedReader<T> reader, TypedWriter<T> writer);

	default <T> void registerType(Class<T> type, TypedReader<T> reader, TypedWriter<T> writer) {
		registerType(type, null, reader, writer);
	}

	void registerResolver(TypedHandlerResolver resolver);
}

