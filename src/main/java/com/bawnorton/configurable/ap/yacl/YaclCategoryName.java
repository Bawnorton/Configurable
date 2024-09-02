package com.bawnorton.configurable.ap.yacl;

public class YaclCategoryName extends YaclTextElement {
    private final String categoryName;

    public YaclCategoryName(String configName, String categoryName) {
        super(configName);
        this.categoryName = categoryName;
    }

    @Override
    protected String getKey() {
        return "category.%s".formatted(categoryName);
    }
}
