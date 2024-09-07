package com.bawnorton.configurable.ap.yacl;

public class YaclOptionGroupDescription extends YaclDescription {
    public YaclOptionGroupDescription(YaclElement text, YaclDescriptionImage image) {
        super(text, image);
    }

    @Override
    protected String getBuilderSpec() {
        return "OptionDescription.createBuilder()";
    }
}
