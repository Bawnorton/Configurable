package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public abstract class YaclDescriptionText extends YaclElement {
    protected final String owner;
    protected final String methodName;

    protected YaclDescriptionText(String owner, String methodName) {
        this.owner = owner;
        this.methodName = methodName;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("java.util.List");
        adder.accept(owner);
    }

    public interface Factory<T extends YaclDescriptionText> {
        T create(String owner, String methodName);
    }
}
