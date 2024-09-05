package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclCustomDescriptionText extends YaclElement {
    private final String owner;
    private final String methodName;

    public YaclCustomDescriptionText(String owner, String methodName) {
        this.owner = owner;
        this.methodName = methodName;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("java.util.List");
        adder.accept(owner);
    }

    @Override
    protected String getSpec(int depth) {
        return "%s.%s(value)".formatted(
                owner.substring(owner.lastIndexOf('.') + 1),
                methodName
        );
    }
}
