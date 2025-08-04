package com.bawnorton.configurable.platform;

import com.bawnorton.configurable.ConfigurableMain;

//? if fabric {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint
public class ConfigurableEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigurableMain.init();
    }
}
//?} else {
/*import net.neoforged.fml.common.Mod;

@Mod(ConfigurableMain.MOD_ID)
public class ConfigurableEntrypoint {
    public ConfigurableEntrypoint() {
        ConfigurableMain.init();
    }
}
*///?}
