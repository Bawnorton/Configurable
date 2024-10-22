package com.bawnorton.configurable.api;

import com.bawnorton.configurable.generated.GeneratedConfig;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import java.util.Map;

public interface ConfigurableApi {
    ConfigurableApi DEFAULT = new ConfigurableApi() {
    };

    default Map<Class<?>, Object> getTypeAdapters() {
        return Map.of();
    }

    default GeneratedConfig beforeSave(GeneratedConfig config) {
        return config;
    }

    default String beforeLoad(String config) {
        return config;
    }

    default GeneratedConfig afterLoad(GeneratedConfig config) {
        return config;
    }

    default boolean serverEnforces() {
        return true;
    }

    default FieldNamingStrategy defaultFieldNamingStrategy() {
        return FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
    }

    //? if !fabric {
    /**
     * The name of the config this API impl belongs to, this is necessary for non-fabric implementations
     * <br>
     * <b>THIS DOES NOT SET THE NAME OF THE CONFIG</b>
     */
    default String getConfigName() {
        throw new UnsupportedOperationException();
    }
    //?}
}
