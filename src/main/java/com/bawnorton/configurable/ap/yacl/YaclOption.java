package com.bawnorton.configurable.ap.yacl;

import com.bawnorton.configurable.OptionType;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class YaclOption extends YaclElement {
    private final String generic;
    private final YaclElement optionName;
    private final YaclElement optionDescription;
    private final YaclElement optionBinding;
    private final YaclElement optionController;
    private final String optionFlags;
    private final YaclElement listeners;

    public YaclOption(String generic,
            YaclOptionName optionName,
            YaclDescription optionDescription,
            YaclOptionBinding optionBinding,
            YaclOptionController optionController,
            OptionType[] optionTypes,
            YaclListeners listeners) {
        this.generic = generic;
        this.optionName = optionName;
        this.optionDescription = optionDescription;
        this.optionBinding = optionBinding;
        this.optionController = optionController;
        this.optionFlags = Arrays.stream(optionTypes)
                .map(type -> "OptionFlag.%s".formatted(type.name()))
                .collect(Collectors.joining(", "));
        this.listeners = listeners;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("dev.isxander.yacl3.api.Option");
        adder.accept("dev.isxander.yacl3.api.OptionFlag");
        optionName.addNeededImports(adder);
        optionDescription.addNeededImports(adder);
        optionBinding.addNeededImports(adder);
        optionController.addNeededImports(adder);
        listeners.addNeededImports(adder);
    }

    @Override
    protected String getSpec(int depth) {
        return """
        Option.<%2$s>createBuilder()
        %1$s.name(%3$s)
        %1$s.description(%4$s)
        %1$s.binding(%5$s)
        %1$s.controller(%6$s)
        %1$s.flag(%7$s)
        %1$s.listeners(%8$s)
        %1$s.build()
        """.formatted(
                "\t".repeat(depth),
                generic,
                optionName.getSpec(depth + 1),
                optionDescription.getSpec(depth + 1),
                optionBinding.getSpec(depth + 1),
                optionController.getSpec(depth + 1),
                optionFlags,
                listeners.getSpec(depth + 1)
        ).trim();
    }
}
