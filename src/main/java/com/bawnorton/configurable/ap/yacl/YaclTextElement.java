package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public abstract class YaclTextElement extends YaclElement {
    protected final String configName;

    protected YaclTextElement(String configName) {
        this.configName = configName;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        //? if yarn {
        adder.accept("net.minecraft.text.Text");
        //?} elif mojmap {
        /*adder.accept("net.minecraft.network.chat.Component");
         *///?}
    }

    @Override
    protected String getSpec(int depth) {
        return "Text.translatable(\"configurable.%s.yacl.%s\")".formatted(configName, getKey());
    }

    protected abstract String getKey();
}
