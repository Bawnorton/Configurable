package com.bawnorton.configurable.reference.validator;

public interface MessageProvider<T> {
    String getMessage(T value);
}
