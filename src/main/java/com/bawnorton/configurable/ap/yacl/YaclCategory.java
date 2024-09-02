package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclCategory extends YaclElement {
    private final YaclElement categoryName;
    private final YaclElement categoryTooltip;
    private final YaclElement options;
    private final YaclElement groups;

    public YaclCategory(YaclCategoryName categoryName, YaclCategoryTooltip categoryTooltip, YaclOptions options, YaclOptionGroups groups) {
        this.categoryName = categoryName;
        this.categoryTooltip = categoryTooltip;
        this.options = options;
        this.groups = groups;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("dev.isxander.yacl3.api.ConfigCategory");
        categoryName.addNeededImports(adder);
        categoryTooltip.addNeededImports(adder);
        options.addNeededImports(adder);
        groups.addNeededImports(adder);
    }

    @Override
    protected String getSpec(int depth) {
        return """
        ConfigCategory.createBuilder()
        %1$s.name(%2$s)
        %1$s.tooltip(%3$s)
        %1$s.options(%4$s)
        %1$s.groups(%5$s)
        %1$s.build()
        """.formatted(
                "\t".repeat(depth),
                categoryName.getSpec(depth + 1),
                categoryTooltip.getSpec(depth + 1),
                options.getSpec(depth + 1),
                groups.getSpec(depth + 1)
        ).trim();
    }
}
