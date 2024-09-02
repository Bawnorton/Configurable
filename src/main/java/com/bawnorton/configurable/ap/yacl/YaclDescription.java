package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclDescription extends YaclElement {
    private final YaclElement text;
    private final YaclElement image;

    public YaclDescription(YaclDescriptionText text, YaclDescriptionImage image) {
        this.text = text;
        this.image = image;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("dev.isxander.yacl3.api.OptionDescription");
        text.addNeededImports(adder);
        if(image != null) {
            image.addNeededImports(adder);
        }
    }

    @Override
    protected String getSpec(int depth) {
        if (image == null) {
            return "OptionDescription.of(%s)".formatted(text.getSpec(depth + 1));
        }
        return """
        OptionDescription.createBuilder()
        %1$s.image(%2$s)
        %1$s.text(%3$s)
        %1$s.build()
        """.formatted(
                "\t".repeat(depth),
                image.getSpec(depth + 1),
                text.getSpec(depth + 1)
        ).trim();
    }
}
