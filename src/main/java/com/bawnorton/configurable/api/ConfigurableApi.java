package com.bawnorton.configurable.api;

import java.util.Map;

public interface ConfigurableApi {
    Map<Class<?>, Object> getTypeAdapters();

    //? if neoforge
    /*String getModId();*/
}
