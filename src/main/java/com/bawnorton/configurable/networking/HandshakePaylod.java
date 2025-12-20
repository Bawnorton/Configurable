package com.bawnorton.configurable.networking;

import com.bawnorton.configurable.ConfigurableMain;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record HandshakePaylod(Set<String> configs) implements CustomPacketPayload {
	public static final Identifier ID = ConfigurableMain.rl("handshake");
	public static final CustomPacketPayload.Type<HandshakePaylod> TYPE = new Type<>(ID);
	public static final StreamCodec<ByteBuf, HandshakePaylod> STREAM_CODEC = ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8)
			.map(HandshakePaylod::new, payload -> new HashSet<>(payload.configs()));

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
