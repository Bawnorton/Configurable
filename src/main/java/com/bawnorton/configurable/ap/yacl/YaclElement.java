package com.bawnorton.configurable.ap.yacl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class YaclElement {
    private final Set<String> neededImports = new HashSet<>();

    protected abstract void addNeededImports(Consumer<String> adder);

    public Set<String> getNeededImports() {
        addNeededImports(neededImports::add);
        return neededImports;
    }

    protected abstract String getSpec(int depth);
}
