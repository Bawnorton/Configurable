package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclCategory extends YaclElement {
    private final YaclElement categoryName;
    private final YaclElement categoryTooltip;
    private final YaclElement options;
    private final YaclOptionGroups groups;

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
        String spec = """
        ConfigCategory.createBuilder()
        %1$s.name(%2$s)
        %1$s.tooltip(%3$s)
        %1$s.options(%4$s)
        """.formatted(
                "\t".repeat(depth),
                categoryName.getSpec(depth + 1),
                categoryTooltip.getSpec(depth + 1),
                options.getSpec(depth + 1)
        );
        if(!groups.isEmpty()) {
            spec += "%1$s.groups(%2$s)\n".formatted("\t".repeat(depth), groups.getSpec(depth + 1));
        }
        spec += "%1$s.build()\n".formatted("\t".repeat(depth));
        return spec.trim();
    }
}
