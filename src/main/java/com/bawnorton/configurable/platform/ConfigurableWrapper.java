package com.bawnorton.configurable.platform;

import com.bawnorton.configurable.ConfigurableMain;

//? if fabric {
/*import net.fabricmc.api.ModInitializer;

public final class ConfigurableWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigurableMain.init();
    }
}
*///?} elif neoforge {
import net.neoforged.fml.common.Mod;

@Mod(ConfigurableMain.MOD_ID)
public final class ConfigurableWrapper {
    public ConfigurableWrapper() {
        ConfigurableMain.init();
    }
}
//?}
