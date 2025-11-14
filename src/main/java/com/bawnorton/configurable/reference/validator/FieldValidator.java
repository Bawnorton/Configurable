package com.bawnorton.configurable.reference.validator;

import org.jetbrains.annotations.Nullable;

public interface FieldValidator<T> {
	boolean isValid(@Nullable T value);
}
