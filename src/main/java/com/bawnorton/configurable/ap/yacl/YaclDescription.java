package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public abstract class YaclDescription extends YaclElement {
    protected final YaclElement text;
    protected final YaclElement image;

    protected YaclDescription(YaclElement text, YaclElement image) {
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

    protected abstract String getBuilderSpec();

    @Override
    protected String getSpec(int depth) {
        StringBuilder spec = new StringBuilder();
        spec.append(getBuilderSpec());
        spec.append("\n%1$s.text(%2$s)".formatted(
                "\t".repeat(depth),
                text.getSpec(depth + 1)
        ));
        if(image != null) {
            spec.append("\n%1$s.%2$s".formatted(
                    "\t".repeat(depth),
                    image.getSpec(depth + 1)
            ));
        }
        spec.append("\n%1$s.build()".formatted("\t".repeat(depth)));
        return spec.toString();
    }
}
