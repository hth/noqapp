package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-06-14 10:43
 */
public enum CouponTypeEnum {
    G("G", "Global. For everyone."),
    F("F", "Family. Just for family members."),
    I("I", "Individual. Only to be used by the assigned person.");

    private String name;
    private String description;

    CouponTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<String, String>() {{
            put(G.name, G.description);
            put(F.name, F.description);
            put(I.name, I.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
