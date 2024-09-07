package com.bawnorton.configurable.ap.yacl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class YaclListeners extends YaclElement {
    private final List<YaclListener> listeners = new ArrayList<>();

    public void addListener(YaclListener listener) {
        listeners.add(listener);
    }

    public boolean isEmpty() {
        return listeners.isEmpty();
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("java.util.List");
        listeners.forEach(listener -> listener.addNeededImports(adder));
    }

    @Override
    protected String getSpec(int depth) {
        return "List.of(%s)".formatted(
                listeners.stream()
                        .map(listener -> listener.getSpec(depth + 1))
                        .collect(Collectors.joining(", "))
        ).trim();
    }
}
