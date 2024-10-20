package com.bawnorton.configurable.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

//? if fabric {
import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.load.ConfigurableWrapper;
import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(value = ModMenu.class, remap = false)
public abstract class ModMenuMixin {
    //? if >=1.21 {
    @Shadow @Final private static Map<String, ConfigScreenFactory<?>> configScreenFactories;

    @Shadow @Final private static List<ModMenuApi> apiImplementations;
    //?} else {
    /*@Shadow private static Map<String, ConfigScreenFactory<?>> configScreenFactories;

    @Shadow private static List<Map<String, ConfigScreenFactory<?>>> delayedScreenFactoryProviders;
    *///?}


    @Inject(
            method = "onInitializeClient",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/loader/api/FabricLoader;getAllMods()Ljava/util/Collection;"
            )
    )
    private void populateConfigurableConfigScreenFactories(CallbackInfo ci) {
        Function<ConfigurableWrapper, ModMenuApi> apiGetter = wrapper -> new ModMenuApi() {
            @Override
            public ConfigScreenFactory<?> getModConfigScreenFactory() {
                return parent -> wrapper.createScreen(MinecraftClient.getInstance(), parent);
            }
        };

        ConfigurableMain.getAllWrappers().forEach((name, sourceSetWrappers) -> sourceSetWrappers.values().forEach(wrapper -> {
            if(!wrapper.hasScreenFactory()) return;

            if(configScreenFactories.containsKey(name)) {
                ConfigurableMain.LOGGER.warn("Config screen factory already exists for \"{}\"", name);
                return;
            }

            ModMenuApi api = apiGetter.apply(wrapper);
            configScreenFactories.put(name, api.getModConfigScreenFactory());
            //? if >=1.21 {
            apiImplementations.add(api);
            //?} else {
            /*delayedScreenFactoryProviders.add(api.getProvidedConfigScreenFactories());
            *///?}
        }));
    }
}
//?} else {
/*import com.bawnorton.configurable.client.ConfigurableClient;

@Mixin(ConfigurableClient.class)
public abstract class ModMenuMixin {}
*///?}
