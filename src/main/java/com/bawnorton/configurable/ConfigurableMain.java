package com.bawnorton.configurable;

import com.bawnorton.configurable.impl.ConfigLoaderWrapper;
import com.bawnorton.configurable.platform.Platform;
import com.bawnorton.configurable.impl.ConfigurableSettings;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ConfigurableMain {
    public static final String MOD_ID = "configurable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Map<String, ConfigLoaderWrapper> LOADERS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    public static void init() {
        Platform.forEachJar(path -> {
            Path configurable = path.resolve("configurable.json");
            if(Files.exists(configurable)) {
                ConfigurableSettings settings;
                try {
                    settings = GSON.fromJson(Files.newBufferedReader(configurable), ConfigurableSettings.class);
                } catch (IOException e) {
                    LOGGER.error("Could not load configurable settings", e);
                    return;
                }
                String configName = settings.name();

                if(LOADERS.containsKey(configName)) {
                    throw new IllegalStateException("Conflicting config name \"%s\" found in \"%s\"".formatted(configName, configurable));
                }

                String configLoader = settings.fullyQualifiedLoader();
                String config = settings.fullyQualifiedConfig();
                try {
                    Class<?> configLoaderClass = Class.forName(configLoader);
                    Class<?> configClass = Class.forName(config);
                    LOADERS.put(configName, new ConfigLoaderWrapper(configLoaderClass, configClass));
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Could not find config classes for \"%s\"".formatted(configName), e);
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Could not load config \"%s\"".formatted(configName), e);
                }
            }
        });
        LOADERS.forEach((name, loader) -> {
            try {
                Object config = loader.loadConfig();
                loader.saveConfig(config);
            } catch (Throwable t) {
                LOGGER.error("Could not load config \"%s\"".formatted(name), t);
            }
        });
    }
}
