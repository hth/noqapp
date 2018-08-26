package com.noqapp.domain.types;

/**
 * Any product that can be available lawfully without prescription. Medicine and Drugs should never be listed. Feel free
 * to extend this list. Might consider adding non-prescription medicine to the list.
 * hitender
 * 3/31/18 8:56 AM
 */
public enum ProductTypeEnum {
    GE("GE", "General"),
    OR("OR", "Organic Produce (Fruits/Vegetables)"),
    FR("FR", "Fresh Produce (Fruits/Vegetables)"),
    GM("GM", "GMO Produce (Fruits/Vegetables)"),
    VE("VE", "Vegetarian Food"),
    NV("NV", "Non-Vegetarian Food"),
    EL("EL", "Electronic"),
    PH("PH", "Pharmacy");

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
