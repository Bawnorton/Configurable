package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclValueFormatter extends YaclElement {
    private final String owner;
    private final String methodName;

    public YaclValueFormatter(String owner, String methodName) {
        this.owner = owner;
        this.methodName = methodName;
    }

    public YaclValueFormatter() {
        this.owner = null;
        this.methodName = null;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        //? if yarn {
        adder.accept("net.minecraft.text.Text");
        //?} elif mojmap {
        /*adder.accept("net.minecraft.network.chat.Component");
         *///?}
        if (owner != null) {
            adder.accept(owner);
        }
    }

    @Override
    protected String getSpec(int depth) {
        if (owner == null) {
            return "Text.literal(value.toString())";
        }

        return "%s.%s(value)".formatted(
                owner.substring(owner.lastIndexOf('.') + 1),
                methodName
        );
    }
}
