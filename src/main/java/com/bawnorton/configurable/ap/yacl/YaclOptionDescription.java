package com.bawnorton.configurable.ap.yacl;

public class YaclOptionDescription extends YaclDescription {
    public YaclOptionDescription(YaclElement text, YaclOptionDescriptionImage image) {
        super(text, image);
    }

    @Override
    protected String getBuilderSpec() {
        return "value -> OptionDescription.createBuilder()";
    }
}
