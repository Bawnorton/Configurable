package com.bawnorton.configurable.client.platform;

import com.bawnorton.configurable.client.ConfigurableClient;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;

public final class ConfigurableClientWrapper implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigurableClient.init();
    }
}
//?} elif neoforge {
/*import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.platform.Platform;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import java.util.function.Supplier;

@Mod(value = ConfigurableMain.MOD_ID, dist = Dist.CLIENT)
public final class ConfigurableClientWrapper {
    public ConfigurableClientWrapper() {
        ConfigurableClient.init();
        ConfigurableMain.getWrappers().forEach((name, wrapper) -> {
            ModContainer container = Platform.getContainer(name);
            if(container == null) {
                ConfigurableMain.LOGGER.error("Could not attach screen factory to \"{}\"", name);
                return;
            }
            container.getCustomExtension(IConfigScreenFactory.class).ifPresentOrElse(
                    factory -> ConfigurableMain.LOGGER.warn("Config screen factory already exists for \"{}\"", name),
                    () -> container.registerExtensionPoint(
                            IConfigScreenFactory.class,
                            (Supplier<IConfigScreenFactory>) () -> (c, parent) -> wrapper.createScreen(MinecraftClient.getInstance(), parent)
                    )
            );
        });
    }
}
*///?}
