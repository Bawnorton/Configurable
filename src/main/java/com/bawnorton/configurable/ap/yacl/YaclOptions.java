package com.bawnorton.configurable.ap.yacl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class YaclOptions extends YaclElement {
    private final List<YaclOption> options = new ArrayList<>();

    public void addOption(YaclOption option) {
        options.add(option);
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("java.util.List");
        options.forEach(option -> option.addNeededImports(adder));
    }

    @Override
    protected String getSpec(int depth) {
        return "List.of(%s)".formatted(
                options.stream()
                        .map(yaclOption -> yaclOption.getSpec(depth + 1))
                        .collect(Collectors.joining(", "))
        ).trim();
    }
}
