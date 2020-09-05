package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 2019-05-23 17:02
 */
public enum AppointmentStatusEnum {
    U("U", "Un-Confirmed"),
    C("C", "Cancelled"),
    A("A", "Accepted"),
    R("R", "Rejected"),
    S("S", "Serviced"),
    W("W", "Appointment Confirmed");

    private final String description;
    private final String name;

    AppointmentStatusEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
