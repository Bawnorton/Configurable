package com.bawnorton.configurable.reference.validator;

import org.jetbrains.annotations.Nullable;

public interface MessageProvider<T> {
	String getMessage(@Nullable T value);
}
