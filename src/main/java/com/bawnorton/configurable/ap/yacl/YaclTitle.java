package com.bawnorton.configurable.ap.yacl;

public class YaclTitle extends YaclTextElement {
    public YaclTitle(String configName) {
        super(configName);
    }

    @Override
    protected String getKey() {
        return "title";
    }
}
