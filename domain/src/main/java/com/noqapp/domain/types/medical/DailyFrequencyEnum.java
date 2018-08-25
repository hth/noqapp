package com.noqapp.domain.types.medical;

/**
 * hitender
 * 8/17/18 5:31 PM
 */
public enum DailyFrequencyEnum {
    OD("OD", "1 time a day", 1),
    TD("TD", "2 times a day", 2),
    HD("HD", "3 times a day", 3),
    FD("FD", "4 times a day", 4),
    VD("VD", "5 times a day", 5);

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
