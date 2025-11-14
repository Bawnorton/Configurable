package com.bawnorton.configurable;

import com.bawnorton.configurable.io.SaveLoader;
import com.bawnorton.configurable.networking.Networking;
import com.bawnorton.configurable.networking.SyncConfigPayload;
import com.bawnorton.configurable.platform.Platform;
import com.bawnorton.configurable.service.ConfigLoader;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class ConfigurableLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger("configurable-loader");
	private static final ServiceLoader<ConfigLoader> CONFIG_LOADERS = ServiceLoader.load(ConfigLoader.class);

	private static final Map<String, SaveLoader> saveLoaders = new HashMap<>();
	private static final Map<String, ConfigLoader> configLoaders = new HashMap<>();

	private static boolean initialized = false;

	public static void init() {
		if (initialized) return;

		initialized = true;
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
		SaveLoader saveLoader = new SaveLoader(configDir.resolve("%s.%s".formatted(instance.getName(), instance.getFileType()
				.getExtension())), instance.getFileType());
		instance.load(saveLoader);
		instance.save(saveLoader);
		saveLoaders.put(instance.getName(), saveLoader);
	}

	public static boolean isConfigLoaderPresent(String name) {
		return configLoaders.containsKey(name);
	}

	public static ConfigLoader getConfigLoader(String name) {
		ConfigLoader loader = configLoaders.get(name);
		if (loader == null) {
			throw new IllegalArgumentException("No ConfigLoader found for config: %s".formatted(name));
		}
		return loader;
	}

	public static Collection<ConfigLoader> getConfigLoaders() {
		return configLoaders.values();
	}

	public static void saveChanges(ConfigLoader configLoader, ServerLevel level, boolean sync) {
		configLoader.save(getSaveLoader(configLoader.getName()));
		if (sync) {
			sync(configLoader, level);
		}
	}

	public static void loadFromDisk(ConfigLoader configLoader, ServerLevel level, boolean sync) {
		configLoader.load(getSaveLoader(configLoader.getName()));
		if (sync) {
			sync(configLoader, level);
		}
	}

	private static void sync(ConfigLoader configLoader, ServerLevel level) {
		if (level != null) {
			level.players()
					.forEach(player -> Networking.send(player, new SyncConfigPayload(configLoader.getName(), configLoader.getFields())));
		} else {
			LOGGER.warn("Sync requested without a valid world. Changes will not be synced.");
		}
	}
}
