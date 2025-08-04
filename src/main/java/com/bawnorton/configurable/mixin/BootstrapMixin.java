package com.bawnorton.configurable.mixin;

import com.bawnorton.configurable.ConfigurableLoader;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment
@Mixin(Bootstrap.class)
public abstract class BootstrapMixin {
    @Inject(
            method = "bootStrap",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/time/Instant;now()Ljava/time/Instant;"
            )
    )
    private static void asEarlyAsFeasibleForReferencingMCClasses(CallbackInfo ci) {
        ConfigurableLoader.init();
    }
}
