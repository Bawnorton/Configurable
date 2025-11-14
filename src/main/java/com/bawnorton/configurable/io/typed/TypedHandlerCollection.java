package com.bawnorton.configurable.io.typed;

import com.bawnorton.configurable.util.GenericType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TypedHandlerCollection {
	private final Map<Class<?>, TypedHandler<?>> handlers = new HashMap<>();

	public <T> void register(Class<T> boxedType, @Nullable Class<T> primitiveType, TypedReader<T> reader, TypedWriter<T> writer) {
		handlers.put(boxedType, new TypedHandler<>(reader, writer));
		if (primitiveType != null) {
			handlers.put(primitiveType, new TypedHandler<>(reader, writer));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> TypedHandler<T> getHandlerFor(GenericType genericType) {
		TypedHandler<T> handler = (TypedHandler<T>) handlers.get(genericType.type());
		if (handler == null) {
			throw new IllegalArgumentException("No TypedHandler registered for type: " + genericType.type().getName());
		}
		return handler;
	}
}
