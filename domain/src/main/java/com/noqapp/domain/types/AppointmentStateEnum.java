package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-07-19 17:03
 */
public enum AppointmentStateEnum {
    O("O", "Off", "No Appointment"),
    A("A", "Traditional Appointments", "Appointment"),
    S("S", "Walk-in Appointments", "Slots"),

    /* Mixture of Walk-ins and traditional appointments. To be implemented. */
    F("F", "Flex Appointments", "Flex");

    private final String description;
    private final String name;
    private final String additionalDescription;

    AppointmentStateEnum(String name, String description, String additionalDescription) {
        this.name = name;
        this.description = description;
        this.additionalDescription = additionalDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public static Map<String, String> asMapWithNameAsKey() {
        return new LinkedHashMap<>() {{
            put(O.name, O.description);
            put(A.name, A.description);
            put(S.name, S.description);
//            put(F.name, F.description);
        }};
    }

    public static Map<String, String> appointmentsFor(BusinessSupportEnum businessSupport) {
        Map<String, String> map = asMapWithNameAsKey();
        switch (businessSupport) {
            case OD:
                map.remove(A.name);
                map.remove(S.name);
                break;
            case QQ:
                //Show all
                break;
            case OQ:
                map.remove(A.name);
                break;
        }

        return map;
    }

    @Override
    public String toString() {
        return description;
    }
}
