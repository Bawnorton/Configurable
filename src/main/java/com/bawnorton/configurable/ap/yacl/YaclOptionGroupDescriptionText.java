package com.bawnorton.configurable.ap.yacl;

public class YaclOptionGroupDescriptionText extends YaclDescriptionText {
    public YaclOptionGroupDescriptionText(String owner, String methodName) {
        super(owner, methodName);
    }

    @Override
    protected String getSpec(int depth) {
        return "%s.%s()".formatted(
                owner.substring(owner.lastIndexOf('.') + 1),
                methodName
        );
    }

}
