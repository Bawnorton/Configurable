package com.bawnorton.configurable.networking;

import com.bawnorton.configurable.ConfigurableMain;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record HandshakePaylod() implements CustomPacketPayload {
    public static final ResourceLocation ID = ConfigurableMain.rl("sync_config_payload");
    public static final CustomPacketPayload.Type<HandshakePaylod> TYPE = new Type<>(ID);
    public static final StreamCodec<ByteBuf, HandshakePaylod> STREAM_CODEC = StreamCodec.unit(new HandshakePaylod());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
