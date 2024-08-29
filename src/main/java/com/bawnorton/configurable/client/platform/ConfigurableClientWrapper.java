package com.bawnorton.configurable.client.platform;

//? if fabric {
/*import com.bawnorton.configurable.client.ConfigurableClient;
import net.fabricmc.api.ClientModInitializer;

public final class ConfigurableClientWrapper implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigurableClient.init();
    }
}
*///?} elif neoforge {
import com.bawnorton.configurable.ConfigurableMain;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = ConfigurableMain.MOD_ID, dist = Dist.CLIENT)
public final class ConfigurableClientWrapper {
    public ConfigurableClientWrapper() {
        com.bawnorton.configurable.client.ConfigurableClient.init();
    }
}
//?}
