package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclOption extends YaclElement {
    private final String generic;
    private final YaclElement optionName;
    private final YaclElement optionDescription;
    private final YaclElement optionBinding;
    private final YaclElement optionController;

    public YaclOption(String generic, YaclOptionName optionName, YaclDescription optionDescription, YaclOptionBinding optionBinding, YaclOptionController optionController) {
        this.generic = generic;
        this.optionName = optionName;
        this.optionDescription = optionDescription;
        this.optionBinding = optionBinding;
        this.optionController = optionController;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("dev.isxander.yacl3.api.Option");
        optionName.addNeededImports(adder);
        optionDescription.addNeededImports(adder);
        optionBinding.addNeededImports(adder);
        optionController.addNeededImports(adder);
    }

    @Override
    protected String getSpec(int depth) {
        return """
        Option.<%2$s>createBuilder()
        %1$s.name(%3$s)
        %1$s.description(%4$s)
        %1$s.binding(%5$s)
        %1$s.controller(%6$s)
        %1$s.build()
        """.formatted(
                "\t".repeat(depth),
                generic,
                optionName.getSpec(depth + 1),
                optionDescription.getSpec(depth + 1),
                optionBinding.getSpec(depth + 1),
                optionController.getSpec(depth + 1)
        ).trim();
    }
}
