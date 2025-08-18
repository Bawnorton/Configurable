package com.bawnorton.configurable.mixin;

import com.bawnorton.configurable.networking.Networking;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(
            method = "placeNewPlayer",
            at = @At("TAIL")
    )
    private void sendNewPlayerConfigs(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        Networking.syncConfigs(player);
    }
}
