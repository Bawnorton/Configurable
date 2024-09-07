package com.bawnorton.configurable.ap.yacl;

import com.bawnorton.configurable.ap.helper.MappingsHelper;
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
        adder.accept(MappingsHelper.getText());
        if (owner != null) {
            adder.accept(owner);
        }
    }

    @Override
    protected String getSpec(int depth) {
        if (owner == null) {
            //? if yarn {
            return "Text.literal(value.toString())";
            //?} elif mojmap {
            /*return "Component.literal(value.toString())";
            *///?}
        }

        return "%s.%s(value)".formatted(
                owner.substring(owner.lastIndexOf('.') + 1),
                methodName
        );
    }
}
