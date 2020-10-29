package com.noqapp.domain.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * hitender
 * 3/31/18 9:58 PM
 */
public enum UnitOfMeasurementEnum {
    CN("CN", "count (nos)"),
    DZ("DZ", "dozen"),
    HD("HD", "1/2 dozen"),
    MG("MG", "mg"),
    GM("GM", "gm"),
    KG("KG", "kg"),
    ML("ML", "ml"),
    LT("LT", "lt"),

    CM("CM", "cm"),
    LA("LA", "Large"),
    XL("XL", "Extra Large"),
    ME("ME", "Medium"),
    SM("SM", "Small"),
    KT("KT", "Kit");

    public static EnumSet<UnitOfMeasurementEnum> PHARMACY = EnumSet.of(MG, GM, ML, LT, CM, LA, XL, ME, SM, KT);
    public static UnitOfMeasurementEnum[] PHARMACY_VALUES = {MG, GM, ML, LT, CM, LA, XL, ME, SM, KT};
    public static EnumSet<UnitOfMeasurementEnum> HEALTH_CARE = EnumSet.of(CN);
    public static UnitOfMeasurementEnum[] HEALTH_CARE_VALUES = {CN};
    public static EnumSet<UnitOfMeasurementEnum> GROCERY = EnumSet.of(CN, HD, DZ, MG, GM, KG, ML, LT);
    public static UnitOfMeasurementEnum[] GROCERY_VALUES = {CN, HD, DZ, MG, GM, KG, ML, LT};

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
        return new HashMap<>() {{
            put(CN.description, CN.name);
            put(HD.description, HD.name);
            put(DZ.description, DZ.name);
            put(MG.description, MG.name);
            put(GM.description, GM.name);
            put(KG.description, KG.name);
            put(ML.description, ML.name);
            put(LT.description, LT.name);

            put(CM.description, CM.name);
            put(LA.description, LA.name);
            put(XL.description, XL.name);
            put(ME.description, ME.name);
            put(SM.description, SM.name);
            put(KT.description, KT.name);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
