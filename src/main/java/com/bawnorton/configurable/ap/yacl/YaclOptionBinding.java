package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclOptionBinding extends YaclElement {
    private final String externalRef;

    public YaclOptionBinding(String extenalRef) {
        this.externalRef = extenalRef;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("dev.isxander.yacl3.api.Binding");
    }

    @Override
    protected String getSpec(int depth) {
        return "Binding.generic(%1$s.get(), %1$s::get, %1$s::set)".formatted(externalRef);
    }
}
