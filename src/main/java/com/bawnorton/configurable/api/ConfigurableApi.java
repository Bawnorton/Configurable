package com.bawnorton.configurable.api;

import com.bawnorton.configurable.generated.GeneratedConfig;
import java.util.Map;

public interface ConfigurableApi {
    Map<Class<?>, Object> getTypeAdapters();

    GeneratedConfig beforeSave(GeneratedConfig config);

    GeneratedConfig afterLoad(GeneratedConfig config);

    default boolean serverEnforces() {
        return true;
    }

    //? if neoforge
    /*String getConfigName();*/
}
