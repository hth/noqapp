package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * hitender
 * 5/24/20 12:25 PM
 */
public enum CustomerPriorityLevelEnum {
    I("I", "Cast Iron (0)", "B4CDDF"),
    S("S", "Silver (1)", "D66E53"),
    G("G", "Gold (2)", "8CDCDA"),
    P("P", "Platinum (3)", "F16A70"),
    R("R", "Unobtainium (Highest)", "4D4D4D");

    private final String description;
    private final String name;
    private final String colorCode;

    CustomerPriorityLevelEnum(String name, String description, String colorCode) {
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<String, String>() {{
            put(S.name, S.description);
            put(G.name, G.description);
            put(P.name, P.description);
            put(R.name, R.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
