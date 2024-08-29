package com.bawnorton.configurable.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class ConfigLoaderWrapper {
    private final MethodHandle loadConfig;
    private final MethodHandle saveConfig;

    public ConfigLoaderWrapper(Class<?> configLoaderClass, Class<?> configClass) {
        MethodHandles.Lookup lookup = MethodHandles.lookup().in(configLoaderClass);
        MethodType returnConfig = MethodType.methodType(configClass);
        MethodType acceptConfig = MethodType.methodType(void.class, configClass);
        try {
            loadConfig = lookup.findStatic(configLoaderClass, "loadConfig", returnConfig);
            saveConfig = lookup.findStatic(configLoaderClass, "saveConfig", acceptConfig);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public Object loadConfig() throws Throwable {
        return loadConfig.invoke();
    }

    public void saveConfig(Object config) throws Throwable {
        saveConfig.invoke(config);
    }
}
