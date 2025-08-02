package com.bawnorton.configurable.reference.validator;

public interface FieldValidator<T> {
    boolean isValid(T value);
}
