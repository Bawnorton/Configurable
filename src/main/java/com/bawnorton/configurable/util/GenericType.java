package com.bawnorton.configurable.util;

public record GenericType(Class<?> type, GenericType... genericTypes) {
    public boolean isRaw() {
        return genericTypes == null || genericTypes.length == 0;
    }
}
