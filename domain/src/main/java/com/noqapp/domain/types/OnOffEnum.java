package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * hitender
 * 5/16/20 12:26 PM
 */
public enum OnOffEnum {
    O("O", "On"),
    F("F", "Off");

    private final String name;
    private final String description;

    OnOffEnum(String name, String description) {
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
            put(O.name, O.description);
            put(F.name, F.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
