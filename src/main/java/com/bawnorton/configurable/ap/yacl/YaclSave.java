package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclSave extends YaclElement {
    private final String configName;

    public YaclSave(String configName) {
        this.configName = configName;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("com.bawnorton.configurable.ConfigurableMain");
        adder.accept("com.bawnorton.configurable.load.ConfigurableWrapper");
    }

    @Override
    protected String getSpec(int depth) {
        return "() -> ConfigurableMain.getWrappers(\"%s\").values().forEach(ConfigurableWrapper::saveConfig)".formatted(configName);
    }
}
