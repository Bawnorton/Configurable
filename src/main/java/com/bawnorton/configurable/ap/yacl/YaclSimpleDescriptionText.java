package com.bawnorton.configurable.ap.yacl;

public class YaclSimpleDescriptionText extends YaclTextElement {
    private final String key;

    public YaclSimpleDescriptionText(String configName, String key) {
        super(configName);
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
