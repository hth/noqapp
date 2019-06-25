package com.noqapp.domain.types;

import java.util.EnumSet;

/**
 * User: hitender
 * Date: 2019-06-25 07:18
 */
public enum CommunicationModeEnum {
    R("R", "Receive All"),
    M("M", "Mute On Notification"),
    S("S", "Stop Receiving");

    public static EnumSet<CommunicationModeEnum> SMS = EnumSet.of(R, S);
    public static EnumSet<CommunicationModeEnum> FCM = EnumSet.of(R, M);

    private String name;
    private String description;

    CommunicationModeEnum(String name, String description) {
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
