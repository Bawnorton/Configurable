package com.bawnorton.configurable.util;

public record GenericHolder(Class<?> type, Class<?>... genericTypes) {
    public boolean isParameterized() {
        return genericTypes != null && genericTypes.length > 0;
    }
}
