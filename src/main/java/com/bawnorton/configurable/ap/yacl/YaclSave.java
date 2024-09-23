package com.bawnorton.configurable.ap.yacl;

import com.bawnorton.configurable.ap.helper.MappingsHelper;
import java.util.function.Consumer;

public class YaclSave extends YaclElement {
    private final String configName;

    public YaclSave(String configName) {
        this.configName = configName;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept(MappingsHelper.getMinecraftClient());
        adder.accept("com.bawnorton.configurable.client.networking.ClientNetworking");
        adder.accept("com.bawnorton.configurable.ConfigurableMain");
        adder.accept("com.bawnorton.configurable.load.ConfigurableWrapper");
    }

    @Override
    protected String getSpec(int depth) {
        return """
                () -> {
            %1$s    ConfigurableMain.getWrappers("%2$s").forEach((sourceSet, wrapper) -> {
            %1$s        MinecraftClient client = MinecraftClient.getInstance();
            %1$s        if(wrapper.serverEnforces() && client.world != null && !client.isConnectedToLocalServer()) {
            %1$s            ClientNetworking.sendClientConfig("%2$s", sourceSet, wrapper.serializeConfig(wrapper.getConfig()));
            %1$s        } else {
            %1$s            wrapper.saveConfig();
            %1$s        }
            %1$s    });
            %1$s}
            """.formatted("\t".repeat(depth), configName).trim();
    }
}
