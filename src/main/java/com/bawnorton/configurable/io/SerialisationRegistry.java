package com.bawnorton.configurable.io;

import com.bawnorton.configurable.api.serialisation.SerialisationRegistrar;
import com.bawnorton.configurable.api.serialisation.TypedHandlerResolver;
import com.bawnorton.configurable.io.typed.TypedHandler;
import com.bawnorton.configurable.io.typed.TypedHandlerCollection;
import com.bawnorton.configurable.io.typed.TypedReader;
import com.bawnorton.configurable.io.typed.TypedWriter;
import com.bawnorton.configurable.util.GenericType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class SerialisationRegistry implements SerialisationRegistrar {
	private final TypedHandlerCollection typedHandlers = new TypedHandlerCollection();
	private final List<TypedHandlerResolver> resolvers = new ArrayList<>();
	private boolean frozen = false;

	@Override
	public synchronized <T> void registerType(Class<T> boxedType, @Nullable Class<T> primitiveType, TypedReader<T> reader, TypedWriter<T> writer) {
		throwIfFrozen();
		typedHandlers.register(boxedType, primitiveType, reader, writer);
	}

	@Override
	public synchronized void registerResolver(TypedHandlerResolver resolver) {
		throwIfFrozen();
		resolvers.add(resolver);
	}

	public synchronized TypedHandler<?> resolve(GenericType genericType) {
		for (int i = resolvers.size() - 1; i >= 0; i--) {
			TypedHandler<?> resolved = resolvers.get(i).resolve(genericType);
			if (resolved != null) {
				resolved.attachExpectedType(genericType);
				return resolved;
			}
		}

		TypedHandler<?> typedHandler = typedHandlers.getHandlerFor(genericType);
		typedHandler.attachExpectedType(genericType);
		return typedHandler;
	}

	public synchronized void freeze() {
		frozen = true;
	}

	private void throwIfFrozen() {
		if (frozen) {
			throw new IllegalStateException("The serialisation registry has already been bootstrapped and can no longer be modified");
		}
	}
}

