package com.bawnorton.configurable.networking;

//? if >=1.21 {
import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.generated.GeneratedConfig;
import com.bawnorton.configurable.load.ConfigurableWrapper;
import com.bawnorton.configurable.networking.packet.ConfigSyncPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public final class Networking {
    public static void init() {
        PayloadTypeRegistry<RegistryByteBuf> playC2S = PayloadTypeRegistry.playC2S();
        PayloadTypeRegistry<RegistryByteBuf> playS2C = PayloadTypeRegistry.playS2C();

        playC2S.register(ConfigSyncPacket.ID, ConfigSyncPacket.PACKET_CODEC);
        playS2C.register(ConfigSyncPacket.ID, ConfigSyncPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, Networking::handleConfigSync);
    }

    public static void sendServerConfig(ServerPlayerEntity player, String name, String sourceSet, String configString) {
        ServerPlayNetworking.send(player, new ConfigSyncPacket(name, sourceSet, configString));
    }

    private static void handleConfigSync(ConfigSyncPacket packet, ServerPlayNetworking.Context context) {
        ConfigurableWrapper wrapper = ConfigurableMain.getWrappers(packet.name()).get(packet.sourceSet());
        wrapper.deserializeConfig(packet.config());
        GeneratedConfig config = wrapper.getConfig();
        config.update(false);
        wrapper.saveConfig();
        ServerPlayNetworking.send(context.player(), new ConfigSyncPacket(packet.name(), packet.sourceSet(), wrapper.serializeConfig(config)));
    }
}

//?} else {
/*import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.generated.GeneratedConfig;
import com.bawnorton.configurable.load.ConfigurableWrapper;
import com.bawnorton.configurable.networking.packet.ConfigSyncPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public final class Networking {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.TYPE, Networking::handleConfigSync);
    }


    public static void sendServerConfig(ServerPlayerEntity player, String name, String sourceSet, String configString) {
        ServerPlayNetworking.send(player, new ConfigSyncPacket(name, sourceSet, configString));
    }

    private static void handleConfigSync(ConfigSyncPacket packet, ServerPlayerEntity player, PacketSender packetSender) {
        ConfigurableWrapper wrapper = ConfigurableMain.getWrappers(packet.name()).get(packet.sourceSet());
        wrapper.deserializeConfig(packet.config());
        GeneratedConfig config = wrapper.getConfig();
        config.update(false);
        wrapper.saveConfig();
        packetSender.sendPacket(new ConfigSyncPacket(packet.name(), packet.sourceSet(), wrapper.serializeConfig(config)));
    }
}
*///?}