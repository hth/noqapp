package com.noqapp.domain.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-07-19 17:03
 */
public enum AppointmentStateEnum {
    O("O", "Off", "No Appointment"),
    A("A", "Traditional Appointment", "Appointment"),
    S("S", "Walk-ins Appointment", "Slots");

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
        return new LinkedHashMap<String, String>() {{
            put(O.name, O.description);
            put(A.name, A.description);
            put(S.name, S.description);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
