package com.bawnorton.configurable.ap.yacl;

import com.bawnorton.configurable.ap.helper.MappingsHelper;
import java.util.function.Consumer;

public abstract class YaclTextElement extends YaclElement {
    protected final String configName;

    protected YaclTextElement(String configName) {
        this.configName = configName;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept(MappingsHelper.getText());
    }

    @Override
    protected String getSpec(int depth) {
        //? if yarn {
        return "Text.translatable(\"configurable.%s.yacl.%s\")".formatted(configName, getKey());
        //?} elif mojmap {
        /*return "Component.translatable(\"configurable.%s.yacl.%s\")".formatted(configName, getKey());
        *///?}
    }

    protected abstract String getKey();
}
