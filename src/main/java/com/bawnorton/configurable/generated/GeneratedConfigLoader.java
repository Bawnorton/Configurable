package com.bawnorton.configurable.generated;

import java.util.function.UnaryOperator;

public interface GeneratedConfigLoader<T extends GeneratedConfig> {
    void saveConfig(T config);

    T loadConfig(UnaryOperator<String> datafixer);

    String serializeConfig(T config);

    T deserializeConfig(String serializedConfig);
}
