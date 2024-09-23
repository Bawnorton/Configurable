package com.bawnorton.configurable.networking.packet;

//? if >=1.21 {
import com.bawnorton.configurable.ConfigurableMain;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ConfigSyncPacket(String name, String sourceSet, String config) implements CustomPayload {
    public static final Id<ConfigSyncPacket> ID = new Id<>(ConfigurableMain.id(ConfigSyncPacket.class.getCanonicalName().toLowerCase()));
    public static final PacketCodec<ByteBuf, ConfigSyncPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            ConfigSyncPacket::name,
            PacketCodecs.STRING,
            ConfigSyncPacket::sourceSet,
            PacketCodecs.STRING,
            ConfigSyncPacket::config,
            ConfigSyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
//?} else {
/*import com.bawnorton.configurable.ConfigurableMain;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record ConfigSyncPacket(String name, String sourceSet, String config) implements FabricPacket {
    public static final PacketType<ConfigSyncPacket> TYPE = PacketType.create(
            ConfigurableMain.id(ConfigSyncPacket.class.getCanonicalName().toLowerCase()),
            ConfigSyncPacket::new
    );

    private ConfigSyncPacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readString(), buf.readString());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(name);
        buf.writeString(sourceSet);
        buf.writeString(config);
    }

    @Override
    public PacketType<ConfigSyncPacket> getType() {
        return TYPE;
    }
}
*///?}
