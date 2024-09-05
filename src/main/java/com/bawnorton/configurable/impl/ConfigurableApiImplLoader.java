package com.bawnorton.configurable.impl;

import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.api.ConfigurableApi;
import java.util.Map;

//? if fabric {
import net.fabricmc.loader.api.FabricLoader;
//?} elif neoforge {
/*import java.util.ServiceLoader;
*///?}

public final class ConfigurableApiImplLoader {
    //? if neoforge
    /*private static final ServiceLoader<ConfigurableApi> serviceLoader = ServiceLoader.load(ConfigurableApi.class);*/

    public static void load() {
        //? if fabric {
        FabricLoader.getInstance().getEntrypointContainers("configurable", ConfigurableApi.class).forEach(container -> {
            String id = container.getProvider().getMetadata().getId();
            try {
                applyImpl(id, container.getEntrypoint());
            } catch (Throwable e) {
                ConfigurableMain.LOGGER.error("Mod {} provides a broken ConfigurableApi implemenation", id, e);
            }
        });
        //?} elif neoforge {
        /*serviceLoader.forEach(apiImpl -> applyImpl(apiImpl.getModId(), apiImpl));
        *///?}
    }

    private static void applyImpl(String id, ConfigurableApi apiImpl) {
        Map<Class<?>, Object> adapters = apiImpl.getTypeAdapters();
        adapters.forEach((type, adapter) -> ConfigurableMain.registerTypeAdapater(id, type, adapter));
    }
}
