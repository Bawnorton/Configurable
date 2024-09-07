package com.bawnorton.configurable.ap.yacl;

import com.bawnorton.configurable.Image;

public class YaclOptionGroupDescriptionImage extends YaclDescriptionImage {
    public YaclOptionGroupDescriptionImage(Image image, String customOwner, String customMethod) {
        super(image, customOwner, customMethod);
    }

    @Override
    protected String getCustomImageSpec() {
        return "%s.%s()".formatted(
                customOwner.substring(customOwner.lastIndexOf('.') + 1),
                customMethod
        );
    }
}