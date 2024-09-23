package com.bawnorton.configurable.client.networking;

//? if >=1.21 {
import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.generated.GeneratedConfig;
import com.bawnorton.configurable.load.ConfigurableWrapper;
import com.bawnorton.configurable.networking.packet.ConfigSyncPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientNetworking {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, ClientNetworking::handleConfigSync);
    }

    private static void handleConfigSync(ConfigSyncPacket packet, ClientPlayNetworking.Context context) {
        ConfigurableWrapper wrapper = ConfigurableMain.getWrappers(packet.name()).get(packet.sourceSet());
        wrapper.deserializeConfig(packet.config());
        GeneratedConfig config = wrapper.getConfig();
        config.update(true);
        wrapper.saveConfig();
        wrapper.refreshConfigScreen();
    }

    public static void sendClientConfig(String name, String sourceSet, String configString) {
        ClientPlayNetworking.send(new ConfigSyncPacket(name, sourceSet, configString));
    }
}
//?} else {
/*import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.generated.GeneratedConfig;
import com.bawnorton.configurable.load.ConfigurableWrapper;
import com.bawnorton.configurable.networking.packet.ConfigSyncPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ClientPlayerEntity;

public final class ClientNetworking {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.TYPE, ClientNetworking::handleConfigSync);
    }

    private static void handleConfigSync(ConfigSyncPacket packet, ClientPlayerEntity player, PacketSender sender) {
        ConfigurableWrapper wrapper = ConfigurableMain.getWrappers(packet.name()).get(packet.sourceSet());
        wrapper.deserializeConfig(packet.config());
        GeneratedConfig config = wrapper.getConfig();
        config.update(true);
        wrapper.saveConfig();
        wrapper.refreshConfigScreen();
    }

    public static void sendClientConfig(String name, String sourceSet, String configString) {
        ClientPlayNetworking.send(new ConfigSyncPacket(name, sourceSet, configString));
    }
}
*///?}
