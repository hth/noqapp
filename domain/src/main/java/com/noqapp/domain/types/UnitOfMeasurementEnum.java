package com.noqapp.domain.types;

/**
 * hitender
 * 3/31/18 9:58 PM
 */
public enum UnitOfMeasurementEnum {
    CN("CN", "count (nos)"),
    //DZ("DZ", "dozen"),
    MG("MG", "mg (milligram)"),
    GM("GM", "gm (gram)"),
    KG("KG", "kg (kilogram)"),
    ML("ML", "ml (milliliter)"),
    LT("LT", "lt (liter)");

    private final String name;
    private final String description;

    UnitOfMeasurementEnum(String name, String description) {
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
