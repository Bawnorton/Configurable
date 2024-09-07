package com.bawnorton.configurable.api;

import com.bawnorton.configurable.generated.GeneratedConfig;
import java.util.Map;

public interface ConfigurableApi {
    Map<Class<?>, Object> getTypeAdapters();

    GeneratedConfig beforeSave(GeneratedConfig config);

    GeneratedConfig afterLoad(GeneratedConfig config);

    //? if neoforge
    /*String getModId();*/
}
