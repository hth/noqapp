package com.noqapp.domain.types;

import java.util.HashMap;
import java.util.Map;

/**
 * hitender
 * 3/31/18 9:58 PM
 */
public enum UnitOfMeasurementEnum {
    CN("CN", "count (nos)"),
    DZ("DZ", "dozen"),
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

    public static Map<String, String> getAsMap() {
        return new HashMap<String, String>() {{
            put(CN.description, CN.name);
            put(DZ.description, DZ.name);
            put(MG.description, MG.name);
            put(GM.description, GM.name);
            put(KG.description, KG.name);
            put(ML.description, ML.name);
            put(LT.description, LT.name);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
