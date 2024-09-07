package com.bawnorton.configurable.generated;

public interface GeneratedConfigLoader<T extends GeneratedConfig> {
    void saveConfig(T config);
    T loadConfig();
}
