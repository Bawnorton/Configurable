package com.bawnorton.configurable.ap.yacl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class YaclOptionGroups extends YaclElement {
    private final List<YaclOptionGroup> optionGroups = new ArrayList<>();

    public void addOptionGroup(YaclOptionGroup optionGroup) {
        optionGroups.add(optionGroup);
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("java.util.List");
        optionGroups.forEach(optionGroup -> optionGroup.addNeededImports(adder));
    }

    @Override
    protected String getSpec(int depth) {
        return "List.of(%s)".formatted(
                optionGroups.stream()
                        .map(yaclOptionGroup -> yaclOptionGroup.getSpec(depth + 1))
                        .collect(Collectors.joining(", "))
        ).trim();
    }
}
