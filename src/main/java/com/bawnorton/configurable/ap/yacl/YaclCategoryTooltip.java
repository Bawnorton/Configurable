package com.bawnorton.configurable.ap.yacl;

public class YaclCategoryTooltip extends YaclTextElement {
    private final String categoryName;

    public YaclCategoryTooltip(String configName, String categoryName) {
        super(configName);
        this.categoryName = categoryName;
    }

    @Override
    protected String getKey() {
        return "category.%s.tooltip".formatted(categoryName);
    }
}
