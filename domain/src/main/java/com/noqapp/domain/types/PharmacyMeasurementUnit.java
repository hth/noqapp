package com.noqapp.domain.types;

import java.util.Arrays;
import java.util.List;

/**
 * hitender
 * 4/4/18 6:22 PM
 */
public enum PharmacyMeasurementUnit {
    PC("PC", "Piece"),
    GR("GR", "Gram"),
    ML("ML", "Milliliter"),
    NO("NO", "Number"),
    LA("LA", "Large"),
    XL("XL", "Extra Large"),
    ME("ME", "Medium"),
    SM("SM", "Small"),
    CM("CM", "Centimeter"),
    TA("TA", "Tablet"),
    IJ("IJ", "Injection"),
    KT("KT", "Kit"),
    IV("IV", "Intravenous"),
    VI("VI", "Vial");

    private final String description;
    private final String name;

    PharmacyMeasurementUnit(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static List<PharmacyMeasurementUnit> asList() {
        PharmacyMeasurementUnit[] all = PharmacyMeasurementUnit.values();
        return Arrays.asList(all);
    }

    @Override
    public String toString() {
        return description;
    }
}
