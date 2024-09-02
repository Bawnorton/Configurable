package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclValueFormatter extends YaclElement {
    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("net.minecraft.text.Text");
    }

    @Override
    protected String getSpec(int depth) {
        return "Text.literal(value.toString())";
    }
}
