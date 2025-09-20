package com.bawnorton.configurable.platform;

import java.nio.file.Path;

//? if fabric {
/*import net.fabricmc.loader.api.FabricLoader;

public final class Platform {
    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}

*///?} else {
import net.neoforged.fml.loading.FMLPaths;

public final class Platform {
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
//?}