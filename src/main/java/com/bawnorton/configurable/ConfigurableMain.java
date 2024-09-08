package com.bawnorton.configurable;

import com.bawnorton.configurable.load.ConfigurableApiImplLoader;
import com.bawnorton.configurable.load.ConfigurableSettings;
import com.bawnorton.configurable.load.ConfigurableWrapper;
import com.bawnorton.configurable.ref.gson.ItemTypeAdapter;
import com.bawnorton.configurable.platform.Platform;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ConfigurableMain {
    public static final String MOD_ID = "configurable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Map<String, ConfigurableWrapper> WRAPPERS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private static final Map<String, Map<Class<?>, Object>> typeAdapters = new HashMap<>();

    public static void init() {
        ConfigurableApiImplLoader.load();

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

                if(WRAPPERS.containsKey(configName)) {
                    throw new IllegalStateException("Conflicting config name \"%s\" found in \"%s\"".formatted(configName, configurable));
                }

                registerDefaultTypeAdapters(configName);

                try {
                    ConfigurableWrapper wrapper = new ConfigurableWrapper(ConfigurableApiImplLoader.getImpl(configName));
                    addToWrapped(settings::fullyQualifiedLoader, wrapper::setLoader, configName);
                    if(settings.hasScreenFactory() && !Platform.isServer()) {
                        addToWrapped(settings::fullyQualifiedScreenFactory, wrapper::setScreenFactory, configName);
                    }
                    WRAPPERS.put(configName, wrapper);
                } catch (IllegalStateException e) {
                    LOGGER.error("Could not create configurable wrapper for \"%s\"".formatted(configName), e);
                }
            }
        });
        WRAPPERS.forEach((configName, wrapper) -> {
            wrapper.loadConfig();
            wrapper.saveConfig();
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> void addToWrapped(Supplier<String> nameGetter, Consumer<Class<T>> applicator, String configName) {
        try {
            applicator.accept((Class<T>) Class.forName(nameGetter.get()));
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not find config class for \"%s\"".formatted(configName), e);
        }
    }

    private static void registerDefaultTypeAdapters(String configName) {
        registerTypeAdapater(configName, Item.class, new ItemTypeAdapter());
    }

    public static <T> void registerTypeAdapater(String configName, Class<T> type, Object typeAdapter) {
        typeAdapters.computeIfAbsent(configName, k -> new HashMap<>()).put(type, typeAdapter);
    }

    public static Map<Class<?>, Object> getTypeAdapters(String configName) {
        return typeAdapters.getOrDefault(configName, Map.of());
    }

    public static Map<String, ConfigurableWrapper> getWrappers() {
        return WRAPPERS;
    }

    public static ConfigurableWrapper getWrapper(String configName) {
        return WRAPPERS.get(configName);
    }
}
