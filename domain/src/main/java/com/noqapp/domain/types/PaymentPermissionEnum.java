package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-04-27 00:02
 */
public enum PaymentPermissionEnum {
    A("A", "Allowed Permission"),
    D("D", "Deny Permission");

    private final String description;
    private final String name;

    PaymentPermissionEnum(String name, String description) {
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
            put(A.name, A.description);
            put(D.name, D.description);
        }};
    }
}
