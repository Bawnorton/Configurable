package com.bawnorton.configurable.ap.yacl;

public class YaclOptionName extends YaclTextElement {
    private final String optionName;

    public YaclOptionName(String configName, String optionName) {
        super(configName);
        this.optionName = optionName;
    }

    @Override
    protected String getKey() {
        return "option.%s".formatted(optionName);
    }
}
