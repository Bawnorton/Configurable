package com.bawnorton.configurable.ap.yacl;

import com.bawnorton.configurable.Image;
import java.util.function.Consumer;

public class YaclDescriptionImage extends YaclElement {
    private final Image image;

    public YaclDescriptionImage(Image image) {
        this.image = image;
    }

    @Override
    protected void addNeededImports(Consumer<String> adder) {
        adder.accept("java.nio.file.Path");
    }

    @Override
    protected String getSpec(int depth) {
        return switch (image.type()) {
            case RESOURCE -> {
                if(!image.path().isEmpty()) {
                    yield "image(Path.of(\"%s\"), Identifier.of(\"%s\"))".formatted(
                            image.path(),
                            image.id()
                    );
                }

                int textureWidth = image.textureWidth() == 0 ? image.width() : image.textureWidth();
                int textureHeight = image.textureHeight() == 0 ? image.height() : image.textureHeight();

                yield "image(Identifier.of(\"%s\"), %s, %s, %s, %s, %s, %s)".formatted(
                        image.id(),
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
                    yield "webpImage(Path.of(\"%s\"), Identifier.of(\"%s\"))".formatted(
                            image.path(),
                            image.id()
                    );
                }

                yield "webpImage(Identifier.of(\"%s\"))".formatted(image.id());
            }
        };
    }
}
