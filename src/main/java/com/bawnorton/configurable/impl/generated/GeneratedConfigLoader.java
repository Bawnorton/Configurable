package com.bawnorton.configurable.impl.generated;

public interface GeneratedConfigLoader<T extends GeneratedConfig> {
    void saveConfig(T config);
    T loadConfig();
}
