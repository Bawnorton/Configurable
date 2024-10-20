package com.bawnorton.configurable.platform;

import java.nio.file.Path;
import java.util.function.Consumer;

//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import java.util.List;

public final class Platform {
    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static void forEachJar(Consumer<Path> consumer) {
        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            List<Path> rootPaths = mod.getRootPaths();
            for (Path path : rootPaths) {
                consumer.accept(path);
            }
        });
    }

    public static boolean isServer() {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER);
    }
}

//?} elif neoforge {
/*import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.ModList;

public final class Platform {
    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    public static boolean isModLoaded(String modId) {
        ModList modList = ModList.get();
        if(modList != null) {
            return modList.isLoaded(modId);
        }
        LoadingModList loadingModList = LoadingModList.get();
        if(loadingModList != null) {
            return loadingModList.getModFileById(modId) != null;
        }
        return false;
    }

    public static void forEachJar(Consumer<Path> consumer) {
        ModList modList = ModList.get();
        modList.forEachModFile(modFile -> consumer.accept(modFile.getFilePath()));
    }

    public static ModContainer getContainer(String name) {
        ModList modList = ModList.get();
        if(modList != null) {
            return modList.getModContainerById(name).orElse(null);
        }
        return null;
    }

    public static boolean isServer() {
        return FMLEnvironment.dist.isDedicatedServer();
    }
}
*///?}