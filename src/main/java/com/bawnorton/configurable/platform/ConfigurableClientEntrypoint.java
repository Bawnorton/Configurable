package com.bawnorton.configurable.platform;

import com.bawnorton.configurable.client.ConfigurableClient;

//? if fabric {
/*import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint
public class ConfigurableClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigurableClient.init();
    }
}
*///?} else {
import com.bawnorton.configurable.ConfigurableMain;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = ConfigurableMain.MOD_ID, dist = Dist.CLIENT)
public class ConfigurableClientEntrypoint {
    public ConfigurableClientEntrypoint() {
        ConfigurableClient.init();
    }
}
//?}