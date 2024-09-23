package com.bawnorton.configurable.ap.yacl;

import java.util.function.Consumer;

public class YaclRoot extends YaclElement {
    private final YaclElement title;
    private final YaclElement categories;
    private final YaclElement save;

    public YaclRoot(YaclTitle title, YaclCategories categories, YaclSave save) {
        this.title = title;
        this.categories = categories;
        this.save = save;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("dev.isxander.yacl3.api.YetAnotherConfigLib");
        title.addNeededImports(adder);
        categories.addNeededImports(adder);
        save.addNeededImports(adder);
    }

    @Override
    public String getSpec(int depth) {
        return """
        YetAnotherConfigLib.createBuilder()
        %1$s.title(%2$s)
        %1$s.categories(%3$s)
        %1$s.save(%4$s)
        %1$s.build()
        """.formatted(
                "\t".repeat(depth),
                title.getSpec(depth + 1),
                categories.getSpec(depth + 1),
                save.getSpec(depth + 1)
        ).trim();
    }
}
