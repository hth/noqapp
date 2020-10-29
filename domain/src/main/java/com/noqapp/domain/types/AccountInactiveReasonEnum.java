package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 11/18/16 6:05 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum AccountInactiveReasonEnum {
    ANV("ANV", "Account Not Validated"),
    BOC("BOC", "Breach Of Compliance"),
    BUP("BUP", "Breach Of Users Policy & Conduct"),
    ADP("ADP", "Access denied for limited period");

    private final String name;
    private final String description;

    AccountInactiveReasonEnum(String name, String description) {
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
            put(ANV.name, ANV.description);
            put(BOC.name, BOC.description);
            put(BUP.name, BUP.description);
            put(ADP.name, ADP.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}

