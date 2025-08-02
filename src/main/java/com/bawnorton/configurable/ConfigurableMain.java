package com.bawnorton.configurable;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.io.SaveLoader;
import com.bawnorton.configurable.platform.Platform;
import com.bawnorton.configurable.service.ConfigLoader;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class ConfigurableMain {
    public static final String MOD_ID = "configurable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final ServiceLoader<ConfigLoader> CONFIG_LOADERS = ServiceLoader.load(ConfigLoader.class);

    private static Map<String, SaveLoader> saveLoaders;
    private static SaveLoader currentSaveLoader;

    public static void init() {
        saveLoaders = new HashMap<>();
        CONFIG_LOADERS.forEach(loader -> {
            LOGGER.info("Loading '{}'", loader.getName());
            FileType fileType = loader.getFileType();
            currentSaveLoader = new SaveLoader(Platform.getConfigDir().resolve("%s.%s".formatted(loader.getName(), fileType)), fileType);
            loader.load();
            currentSaveLoader.load();
            loader.save();
            currentSaveLoader.save();
            saveLoaders.put(loader.getName(), currentSaveLoader);
        });
        currentSaveLoader = null;
    }

    public static SaveLoader getCurrentSaveLoader() {
        if (currentSaveLoader == null) {
            throw new IllegalStateException("Configs are not being loading");
        }
        return currentSaveLoader;
    }

    @TestOnly
    public static void setCurrentSaveLoader(SaveLoader loader) {
        currentSaveLoader = loader;
    }

    public static SaveLoader getSaveLoader(String name) {
        if (saveLoaders == null) {
            throw new IllegalStateException("Configurable has not been initialized yet");
        }
        SaveLoader loader = saveLoaders.get(name);
        if (loader == null) {
            throw new IllegalArgumentException("No SaveLoader found for name: " + name);
        }
        return loader;
    }
}
