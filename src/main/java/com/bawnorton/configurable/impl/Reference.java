package com.bawnorton.configurable.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class Reference<T> {
    private final String name;
    private final VarHandle varHandle;

    public Reference(Class<?> clazz, String name, Class<T> type) {
        this.name = name;
        try {
            MethodHandles.Lookup publicLookup = MethodHandles.lookup();
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, publicLookup);
            varHandle = privateLookup.findStaticVarHandle(clazz, name, type);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) varHandle.get();
    }

    public void set(Object value) throws ClassCastException {
        varHandle.set(value);
    }

    public Class<?> getType() {
        return varHandle.varType();
    }

    public String getName() {
        return name;
    }
}
