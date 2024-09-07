package com.bawnorton.configurable.ap.yacl;

public class YaclOptionDescriptionText extends YaclDescriptionText {
    public YaclOptionDescriptionText(String owner, String methodName) {
        super(owner, methodName);
    }

    @Override
    protected String getSpec(int depth) {
        return "%s.%s(value)".formatted(
                owner.substring(owner.lastIndexOf('.') + 1),
                methodName
        );
    }

}
