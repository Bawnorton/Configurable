package com.bawnorton.configurable.ap.yacl;

import com.bawnorton.configurable.Image;
import com.bawnorton.configurable.ap.helper.MappingsHelper;
import java.util.function.Consumer;

public abstract class YaclDescriptionImage extends YaclElement {
    protected final Image image;
    protected final String customOwner;
    protected final String customMethod;

    protected YaclDescriptionImage(Image image, String customOwner, String customMethod) {
        this.image = image;
        this.customOwner = customOwner;
        this.customMethod = customMethod;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("java.nio.file.Path");
        adder.accept(MappingsHelper.getIdentifier());
        if (customOwner != null) {
            adder.accept(customOwner);
        }
    }
    protected abstract String getCustomImageSpec();

    @Override
    protected String getSpec(int depth) {
        if(customOwner != null && customMethod != null) {
            return "customImage(%s)".formatted(getCustomImageSpec());
        }
        //? if >=1.21 {
        /*String id = "Identifier.of(\"%s\")".formatted(image.value());
        *///?} else {
        String id = "new Identifier(\"%s\")".formatted(image.value());
        //?}
        return switch (image.type()) {
            case RESOURCE -> {
                if(!image.path().isEmpty()) {
                    yield "image(Path.of(\"%s\"), %s)".formatted(
                            image.path(),
                            id
                    );
                }

                int textureWidth = image.textureWidth() == 0 ? image.width() : image.textureWidth();
                int textureHeight = image.textureHeight() == 0 ? image.height() : image.textureHeight();

                yield "image(%s, %sF, %sF, %s, %s, %s, %s)".formatted(
                        id,
                        image.u(),
                        image.v(),
                        image.width(),
                        image.height(),
                        textureWidth,
                        textureHeight
                );
            }
            case WEBP -> {
                if(!image.path().isEmpty()) {
                    yield "webpImage(Path.of(\"%s\"), %s)".formatted(
                            image.path(),
                            id
                    );
                }

                yield "webpImage(%s)".formatted(id);
            }
        };
    }

    public interface Factory<T extends YaclDescriptionImage> {
        T create(Image image, String customOwner, String customMethod);
    }
}
