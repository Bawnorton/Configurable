package com.bawnorton.configurable.api.serialisation;

import com.bawnorton.configurable.io.typed.TypedHandler;
import com.bawnorton.configurable.util.GenericType;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TypedHandlerResolver {
	@Nullable TypedHandler<?> resolve(GenericType genericType);
}

