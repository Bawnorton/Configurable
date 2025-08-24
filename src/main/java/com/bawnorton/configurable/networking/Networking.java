package com.bawnorton.configurable.networking;

//? if fabric {
import com.bawnorton.configurable.ConfigurableLoader;
import com.bawnorton.configurable.service.ConfigLoader;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class Networking {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(SyncConfigPayload.TYPE, SyncConfigPayload.STREAM_CODEC);
    }

    public static void syncConfigs(ServerPlayer player) {
        for(ConfigLoader loader : ConfigurableLoader.getConfigLoaders()) {
            SyncConfigPayload payload = new SyncConfigPayload(loader.getName(), loader.getFields());
            send(player, payload);
        }
    }

    public static <T extends CustomPacketPayload> void send(ServerPlayer player, T payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
//?} else {

/*import com.bawnorton.configurable.ConfigurableLoader;
import com.bawnorton.configurable.ConfigurableMain;
import com.bawnorton.configurable.service.ConfigLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ConfigurableMain.MOD_ID)
public class Networking {
    public static void init() {}

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(SyncConfigPayload.TYPE, SyncConfigPayload.STREAM_CODEC, Networking::handleSyncConfigPayload);
    }

    public static void syncConfigs(ServerPlayer player) {
        for(ConfigLoader loader : ConfigurableLoader.getConfigLoaders()) {
            SyncConfigPayload payload = new SyncConfigPayload(loader.getName(), loader.getFields());
            send(player, payload);
        }
    }

    public static <T extends CustomPacketPayload> void send(ServerPlayer player, T payload) {
        if(player.connection.hasChannel(payload)) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

    private static void handleSyncConfigPayload(SyncConfigPayload payload, IPayloadContext context) {
        ConfigLoader configLoader = ConfigurableLoader.getConfigLoader(payload.name());
        payload.applyToConfigLoader(configLoader);
    }
}

*///?}