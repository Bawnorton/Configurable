package com.bawnorton.configurable.client.networking;

//? if fabric {

import com.bawnorton.configurable.ConfigurableLoader;
import com.bawnorton.configurable.networking.HandshakePaylod;
import com.bawnorton.configurable.networking.SyncConfigPayload;
import com.bawnorton.configurable.service.ConfigLoader;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.stream.Collectors;

public class ClientNetworking {
	public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(SyncConfigPayload.TYPE, ClientNetworking::handleSyncConfigPayload);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> send(
				new HandshakePaylod(
						ConfigurableLoader.getConfigLoaders()
								.stream()
								.map(ConfigLoader::getName)
								.collect(Collectors.toSet())
				)
		));
	}

	private static void handleSyncConfigPayload(SyncConfigPayload payload, ClientPlayNetworking.Context context) {
		ConfigLoader configLoader = ConfigurableLoader.getConfigLoader(payload.name());
		payload.applyToConfigLoader(configLoader);
	}

	public static <T extends CustomPacketPayload> void send(T payload) {
		ClientPlayNetworking.send(payload);
	}
}
//?} else {
/*public class ClientNetworking {
    public static void init() {
    }
}
*///?}
