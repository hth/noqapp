package com.noqapp.domain.types.medical;

/**
 * hitender
 * 8/17/18 5:31 PM
 */
public enum DailyFrequencyEnum {
    OD("OD", "One time", 1),
    TD("TD", "Two times", 2),
    HD("HD", "Three times", 3),
    FD("FD", "Four times", 4),
    VD("VD", "Five times", 5);

    private final String description;
    private final String name;
    private int times;

    DailyFrequencyEnum(String name, String description, int times) {
        this.name = name;
        this.description = description;
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTimes() {
        return times;
    }

    @Override
    public String toString() {
        return description;
    }
}
