package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * hitender
 * 2018-12-09 11:04
 */
public enum DataProtectionEnum {
    L("L", "Low"),
    M("M", "Medium"),
    H("H", "High");

    private final String description;
    private final String name;

    DataProtectionEnum(String name, String description) {
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
            put(L.name, L.description);
            put(M.name, M.description);
            put(H.name, H.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
