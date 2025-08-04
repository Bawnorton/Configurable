package com.bawnorton.configurable.reference;

public interface OnSet<T> {
    void onSet(T value, boolean fromSync);
}
