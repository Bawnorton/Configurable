package com.bawnorton.configurable.ap.yacl;

import com.bawnorton.configurable.Image;

public class YaclOptionDescriptionImage extends YaclDescriptionImage {
    public YaclOptionDescriptionImage(Image image, String customOwner, String customMethod) {
        super(image, customOwner, customMethod);
    }

    @Override
    protected String getCustomImageSpec() {
        return "%s.%s(value)".formatted(
                customOwner.substring(customOwner.lastIndexOf('.') + 1),
                customMethod
        );
    }
}
