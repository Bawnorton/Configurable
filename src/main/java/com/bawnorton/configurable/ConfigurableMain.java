package com.bawnorton.configurable;

import com.bawnorton.configurable.io.SaveLoader;
import com.bawnorton.configurable.networking.Networking;
import com.bawnorton.configurable.networking.SyncConfigPayload;
import com.bawnorton.configurable.platform.Platform;
import com.bawnorton.configurable.service.ConfigLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class ConfigurableMain {
    public static final String MOD_ID = "configurable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final ServiceLoader<ConfigLoader> CONFIG_LOADERS = ServiceLoader.load(ConfigLoader.class);

    private static final Map<String, SaveLoader> saveLoaders = new HashMap<>();
    private static final Map<String, ConfigLoader> configLoaders = new HashMap<>();

    public static void init() {
        Networking.init();

        LOGGER.info("Loading Generated Config Loaders...");
        CONFIG_LOADERS.forEach(loader -> {
            registerConfigLoader(Platform.getConfigDir(), loader);
            LOGGER.info("Loaded '{}'", loader.getName());
        });
    }

    public static SaveLoader getSaveLoader(String name) {
        SaveLoader loader = saveLoaders.get(name);
        if (loader == null) {
            throw new IllegalArgumentException("No SaveLoader found for config: %s".formatted(name));
        }
        return loader;
    }

    public static void registerConfigLoader(Path configDir, ConfigLoader instance) {
        if (configLoaders.containsKey(instance.getName())) {
            throw new IllegalArgumentException("Config with name '%s' already exists".formatted(instance.getName()));
        }
        configLoaders.put(instance.getName(), instance);
        SaveLoader saveLoader = new SaveLoader(configDir.resolve("%s.%s".formatted(instance.getName(), instance.getFileType())), instance.getFileType());
        instance.load(saveLoader);
        instance.save(saveLoader);
        saveLoaders.put(instance.getName(), saveLoader);
    }

    public static ConfigLoader getConfigLoader(String name) {
        ConfigLoader loader = configLoaders.get(name);
        if (loader == null) {
            throw new IllegalArgumentException("No ConfigLoader found for config: %s".formatted(name));
        }
        return loader;
    }

    public static void saveChanges(ConfigLoader configLoader, ServerLevel level, boolean sync) {
        configLoader.save(getSaveLoader(configLoader.getName()));
        if (sync) {
            if (level != null) {
                level.players().forEach(player -> Networking.send(player, new SyncConfigPayload(configLoader.getName(), configLoader.getFields())));
            } else {
                LOGGER.warn("Sync requested without a valid world. Changes will not be synced.");
            }
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
