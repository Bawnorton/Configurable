package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclDescription extends YaclElement {
    private final YaclElement text;
    private final YaclElement image;

    public YaclDescription(YaclElement text, YaclDescriptionImage image) {
        this.text = text;
        this.image = image;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("dev.isxander.yacl3.api.OptionDescription");
        //? if yarn {
        adder.accept("net.minecraft.text.Text");
        //?} elif mojmap {
        /*adder.accept("net.minecraft.network.chat.Component");
        *///?}
        text.addNeededImports(adder);
        if(image != null) {
            image.addNeededImports(adder);
        }
    }

    @Override
    protected String getSpec(int depth) {
        if (image == null && text instanceof YaclDescriptionText) {
            return "OptionDescription.of(%s)".formatted(text.getSpec(depth + 1));
        }
        StringBuilder spec = new StringBuilder();
        spec.append("value -> OptionDescription.createBuilder()");
        spec.append("%1$s.text(%2$s)".formatted(
                "\t".repeat(depth),
                text.getSpec(depth + 1)
        ));
        if(image != null) {
            spec.append("%1$s.%2$s".formatted(
                    "\t".repeat(depth),
                    image.getSpec(depth + 1)
            ));
        }
        spec.append("%1$s.build()".formatted("\t".repeat(depth)));
        return spec.toString();
    }
}
