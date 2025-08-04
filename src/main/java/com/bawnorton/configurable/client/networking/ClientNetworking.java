package com.bawnorton.configurable.client.networking;

import com.bawnorton.configurable.ConfigurableLoader;
import com.bawnorton.configurable.networking.SyncConfigPayload;
import com.bawnorton.configurable.service.ConfigLoader;

//? if fabric {
/*import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworking {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SyncConfigPayload.TYPE, ClientNetworking::handleSyncConfigPayload);
    }

    private static void handleSyncConfigPayload(SyncConfigPayload payload, ClientPlayNetworking.Context context) {
        ConfigLoader configLoader = ConfigurableLoader.getConfigLoader(payload.name());
        payload.applyToConfigLoader(configLoader);
    }
}
*///?} else {
public class ClientNetworking {
    public static void init() {
    }
}
//?}
