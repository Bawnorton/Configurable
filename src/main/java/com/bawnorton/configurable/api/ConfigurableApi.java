package com.bawnorton.configurable.api;

import com.bawnorton.configurable.api.impl.ConfigurableApiImpl;
import net.minecraft.server.level.ServerLevel;

public interface ConfigurableApi {
	static void saveChanges(ServerLevel level, boolean sync) {
		ConfigurableApiImpl.saveChanges(level, sync);
	}

	static void saveChanges() {
		saveChanges(null, false);
	}

	static void loadFromDisk(ServerLevel level, boolean sync) {
		ConfigurableApiImpl.loadFromDisk(level, sync);
	}

	static void loadFromDisk() {
		loadFromDisk(null, false);
	}
}
