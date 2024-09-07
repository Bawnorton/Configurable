package com.bawnorton.configurable.ap.yacl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class YaclElement {
    protected abstract void addNeededImports(Consumer<String> adder);

    public Set<String> getNeededImports() {
        Set<String> neededImports = new HashSet<>();
        addNeededImports(i -> {
            neededImports.add(i);
        });
        return neededImports;
    }

    protected abstract String getSpec(int depth);
}
