package com.bawnorton.configurable.networking;

//? if fabric {

import com.bawnorton.configurable.ConfigurableLoader;
import com.bawnorton.configurable.service.ConfigLoader;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Networking {
	private static final Map<UUID, Set<String>> clientConfigs = new HashMap<>();

	public static void init() {
		//? if <= 1.21.11 {
		 PayloadTypeRegistry.playS2C().register(SyncConfigPayload.TYPE, SyncConfigPayload.STREAM_CODEC);
		//?} else
		/*PayloadTypeRegistry.clientboundPlay().register(SyncConfigPayload.TYPE, SyncConfigPayload.STREAM_CODEC);*/
		//? if <= 1.21.11 {
		PayloadTypeRegistry.playC2S().register(HandshakePaylod.TYPE, HandshakePaylod.STREAM_CODEC);
        //?} else
        /*PayloadTypeRegistry.serverboundPlay().register(HandshakePaylod.TYPE, HandshakePaylod.STREAM_CODEC);*/

		ServerPlayNetworking.registerGlobalReceiver(HandshakePaylod.TYPE, Networking::handleHandshake);

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> clientConfigs.remove(getPlayerId(handler.getPlayer())));
	}

	private static void handleHandshake(HandshakePaylod handshakePaylod, ServerPlayNetworking.Context context) {
		clientConfigs.put(getPlayerId(context.player()), handshakePaylod.configs());
		syncConfigs(context.player());
	}

	public static void syncConfigs(ServerPlayer player) {
		Set<String> configs = clientConfigs.getOrDefault(getPlayerId(player), Set.of());
		if (configs.isEmpty()) return;

		for (ConfigLoader loader : ConfigurableLoader.getConfigLoaders()) {
			if (configs.contains(loader.getName())) {
				send(player, new SyncConfigPayload(loader.getName(), loader.getFields()));
			}
		}
	}

	private static UUID getPlayerId(ServerPlayer player) {
		//? if >=1.21.10 {
		return player.getGameProfile().id();
		//?} else {
		/*return player.getGameProfile().getId();
		 *///?}
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
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = ConfigurableMain.MOD_ID)
public class Networking {
    private static final Map<UUID, Set<String>> clientConfigs = new HashMap<>();

    public static void init() {}

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(SyncConfigPayload.TYPE, SyncConfigPayload.STREAM_CODEC, Networking::handleSyncConfigPayload);
        registrar.playToServer(HandshakePaylod.TYPE, HandshakePaylod.STREAM_CODEC, Networking::handleHandshake);
    }

    @SubscribeEvent
    public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        syncConfigs((ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event) {
        clientConfigs.remove(getPlayerId(event.getEntity()));
    }

    private static void handleHandshake(HandshakePaylod handshakePaylod, IPayloadContext context) {
        clientConfigs.put(getPlayerId(context.player()), handshakePaylod.configs());
        syncConfigs((ServerPlayer) context.player());
    }

    private static void handleSyncConfigPayload(SyncConfigPayload payload, IPayloadContext context) {
        ConfigLoader configLoader = ConfigurableLoader.getConfigLoader(payload.name());
        payload.applyToConfigLoader(configLoader);
    }

    public static void syncConfigs(ServerPlayer player) {
        Set<String> configs = clientConfigs.getOrDefault(getPlayerId(player), Set.of());
        if(configs.isEmpty()) return;

        for(ConfigLoader loader : ConfigurableLoader.getConfigLoaders()) {
            if (configs.contains(loader.getName())) {
                send(player, new SyncConfigPayload(loader.getName(), loader.getFields()));
            }
        }
    }

		private static UUID getPlayerId(Player player) {
				//? if >=1.21.10 {
				return player.getGameProfile().id();
				//?} else {
				/^return player.getGameProfile().getId();
				^///?}
		}

    public static <T extends CustomPacketPayload> void send(ServerPlayer player, T payload) {
        if(player.connection.hasChannel(payload)) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }
}

*///?}