package com.bawnorton.configurable.mixin;

import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.networking.Networking;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendCommandTree(Lnet/minecraft/server/network/ServerPlayerEntity;)V"
            )
    )
    private void onPlayerConnect(CallbackInfo ci, @Local(argsOnly = true) ServerPlayerEntity player) {
        ConfigurableMain.getAllWrappers().forEach((name, wrappers) -> wrappers.forEach((sourceSet, wrapper) -> Networking.sendServerConfig(player, name, sourceSet, wrapper.serializeConfig(wrapper.getConfig()))));
    }
}
