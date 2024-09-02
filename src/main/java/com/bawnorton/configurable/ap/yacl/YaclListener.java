package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclListener extends YaclElement {
    private final String owner;
    private final String methodName;

    public YaclListener(String owner, String methodName) {
        this.owner = owner;
        this.methodName = methodName;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept(owner);
    }

    @Override
    protected String getSpec(int depth) {
        return "%s::%s".formatted(
                owner.substring(owner.lastIndexOf(".") + 1),
                methodName
        );
    }
}
