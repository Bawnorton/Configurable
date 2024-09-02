package com.bawnorton.configurable.ap.yacl;

public class YaclOptionGroupName extends YaclTextElement {
    private final String optionGroupName;

    public YaclOptionGroupName(String configName, String optionGroupName) {
        super(configName);
        this.optionGroupName = optionGroupName;
    }

    @Override
    protected String getKey() {
        return "option_group.%s".formatted(optionGroupName);
    }
}
