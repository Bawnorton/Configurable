package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclOptionGroup extends YaclElement {
    private final YaclElement optionGroupName;
    private final YaclElement optionGroupDescription;
    private final YaclElement options;
    private final boolean collapsed;

    public YaclOptionGroup(YaclOptionGroupName optionGroupName, YaclOptionGroupDescription optionGroupDescription, YaclOptions options, boolean collapsed) {
        this.optionGroupName = optionGroupName;
        this.optionGroupDescription = optionGroupDescription;
        this.options = options;
        this.collapsed = collapsed;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("dev.isxander.yacl3.api.OptionGroup");
        optionGroupName.addNeededImports(adder);
        optionGroupDescription.addNeededImports(adder);
        options.addNeededImports(adder);
    }

    @Override
    protected String getSpec(int depth) {
        return """
        OptionGroup.createBuilder()
        %1$s.name(%2$s)
        %1$s.description(%3$s)
        %1$s.options(%4$s)
        %1$s.collapsed(%5$s)
        %1$s.build()
        """.formatted(
                "\t".repeat(depth),
                optionGroupName.getSpec(depth + 1),
                optionGroupDescription.getSpec(depth + 1),
                options.getSpec(depth + 1),
                collapsed
        ).trim();
    }
}
