package com.noqapp.domain.types;

/**
 * hitender
 * 3/31/18 8:56 AM
 */
public enum ProductTypeEnum {
    GE("GE", "General"),
    ME("ME", "Medicine"),
    OR("OR", "Organic Produce (Fruits/Vegetables)"),
    FR("FR", "Fresh Produce (Fruits/Vegetables)"),
    GM("GM", "GMO Produce (Fruits/Vegetables)"),
    VE("VE", "Vegetarian Food"),
    NV("NV", "Non-Vegetarian Food"),
    EL("EL", "Electronic");

    private final String name;
    private final String description;

    ProductTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
