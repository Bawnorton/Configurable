package com.bawnorton.configurable;

import com.bawnorton.configurable.load.ConfigurableApiImplLoader;
import com.bawnorton.configurable.load.ConfigurableSettings;
import com.bawnorton.configurable.load.ConfigurableWrapper;
import com.bawnorton.configurable.networking.Networking;
import com.bawnorton.configurable.platform.Platform;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ConfigurableMain {
    public static final String MOD_ID = "configurable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Map<String, Map<String, ConfigurableWrapper>> WRAPPERS = new HashMap<>();

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static void init() {
        ConfigurableApiImplLoader.load();
        Networking.init();

        List<ConfigurableSettings> settingsList = new ArrayList<>();
        Platform.forEachJar(path -> {
            //? if neoforge {
            /*List<String> sourceSetSettings = new ArrayList<>();
            try (ZipFile zipFile = new ZipFile(path.toFile())) {
                zipFile.stream()
                        .filter(entry -> entry.getName().startsWith("configurable/") && !entry.isDirectory())
                        .forEach(entry -> sourceSetSettings.add(entry.getName()));
                for (String sourceSetSetting : sourceSetSettings) {
                    ConfigurableSettings settings;
                    try {
                        ZipEntry entry = zipFile.getEntry(sourceSetSetting);
                        if (entry == null) {
                            LOGGER.warn("Could not find configurable setting {} in {}", sourceSetSetting, path);
                            continue;
                        }
                        try (InputStream is = zipFile.getInputStream(entry)) {
                            settings = GSON.fromJson(new InputStreamReader(is), ConfigurableSettings.class);
                        }
                    } catch (IOException e) {
                        LOGGER.error("Could not load configurable settings", e);
                        return;
                    }
                    settingsList.add(settings);
                }
            } catch (IOException ignore) {}
            *///?} else {
            Path configurable = path.resolve("configurable");
            if (!(Files.exists(configurable) && Files.isDirectory(configurable))) return;

            List<Path> sourceSetSettings = new ArrayList<>();
            try (Stream<Path> stream = Files.list(configurable)) {
                stream.filter(Files::isRegularFile).forEach(sourceSetSettings::add);
            } catch (IOException e) {
                ConfigurableMain.LOGGER.error("Could not find configurable settings", e);
            }

            for(Path sourceSetSetting : sourceSetSettings) {
                ConfigurableSettings settings;
                try {
                    settings = GSON.fromJson(Files.newBufferedReader(sourceSetSetting), ConfigurableSettings.class);
                } catch (IOException e) {
                    LOGGER.error("Could not load configurable settings", e);
                    return;
                }
                settingsList.add(settings);
            }
            //?}
        });

        for (ConfigurableSettings settings : settingsList) {
            String configName = settings.name();
            String sourceSet = settings.sourceSet();

            if(WRAPPERS.containsKey(configName)) {
                Map<String, ConfigurableWrapper> wrappers = WRAPPERS.get(configName);
                if(wrappers.containsKey(sourceSet)) {
                    throw new IllegalStateException("Conflicting config name \"%s\" for source set \"%s\"".formatted(configName, sourceSet));
                }
            }

            try {
                ConfigurableWrapper wrapper = new ConfigurableWrapper(ConfigurableApiImplLoader.getImpl(configName));
                if(wrapper.isClientOnly() && Platform.isServer()) return;

                WRAPPERS.computeIfAbsent(configName, k -> new HashMap<>()).put(sourceSet, wrapper);
                addToWrapped(settings::fullyQualifiedLoader, wrapper::setLoader, configName);
                if(settings.hasScreenFactory() && !Platform.isServer()) {
                    addToWrapped(settings::fullyQualifiedScreenFactory, wrapper::setScreenFactory, configName);
                }
            } catch (IllegalStateException e) {
                LOGGER.error("Could not create configurable wrapper for \"%s\"".formatted(configName), e);
            }
        }
        WRAPPERS.values().forEach(sourceSetWrappers -> sourceSetWrappers.values().forEach(wrapper -> {
            wrapper.loadConfig();
            wrapper.saveConfig();
        }));
    }

    @SuppressWarnings("unchecked")
    private static <T> void addToWrapped(Supplier<String> nameGetter, Consumer<Class<T>> applicator, String configName) {
        try {
            applicator.accept((Class<T>) Class.forName(nameGetter.get()));
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            LOGGER.error("Could not find config class for \"%s\"".formatted(configName), e);
        }
    }

    public static Map<Class<?>, Object> getTypeAdapters(String configName, String sourceSet) {
        return WRAPPERS.get(configName).get(sourceSet).getTypeAdapters();
    }

    public static FieldNamingStrategy getFieldNamingStrategy(String configName, String sourceSet) {
        return WRAPPERS.get(configName).get(sourceSet).getFieldNamingStrategy();
    }

    public static Map<String, Map<String, ConfigurableWrapper>> getAllWrappers() {
        return WRAPPERS;
    }

    public static Map<String, ConfigurableWrapper> getWrappers(String configName) {
        return WRAPPERS.get(configName);
    }
}
