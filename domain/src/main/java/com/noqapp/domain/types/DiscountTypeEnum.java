package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-06-10 11:18
 */
public enum DiscountTypeEnum {
    P("P", "Percent"),
    F("F", "Fixed");

    private String name;
    private String description;

    DiscountTypeEnum(String name, String description) {
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
        return new LinkedHashMap<>() {{
            put(P.name, P.description);
            put(F.name, F.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
