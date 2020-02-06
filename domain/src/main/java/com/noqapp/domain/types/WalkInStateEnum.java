package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

public enum WalkInStateEnum {
    E("E", "Enabled"),
    D("D", "Disabled");

    private final String name;
    private final String description;

    WalkInStateEnum(String name, String description) {
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
            put(E.name, E.description);
            put(D.name, D.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
