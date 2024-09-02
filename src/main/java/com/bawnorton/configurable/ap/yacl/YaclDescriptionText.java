package com.bawnorton.configurable.ap.yacl;

public class YaclDescriptionText extends YaclTextElement {
    private final String key;

    public YaclDescriptionText(String configName, String key) {
        super(configName);
        this.key = key;
    }

    @Override
    protected String getKey() {
        return "%s.description".formatted(key);
    }
}
