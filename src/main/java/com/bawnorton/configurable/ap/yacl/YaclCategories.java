package com.bawnorton.configurable.ap.yacl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class YaclCategories extends YaclElement {
    private final List<YaclCategory> categories = new ArrayList<>();

    public void addCategory(YaclCategory category) {
        this.categories.add(category);
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("java.util.List");
        categories.forEach(category -> category.addNeededImports(adder));
    }

    @Override
    protected String getSpec(int depth) {
        return "List.of(%s)".formatted(
                categories.stream()
                        .map(yaclCategory -> yaclCategory.getSpec(depth + 1))
                        .collect(Collectors.joining(", "))
        ).trim();
    }
}
